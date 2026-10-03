package com.vsfsms.staffmodule.service;

import com.vsfsms.staffmodule.exception.BusinessValidationException;
import com.vsfsms.staffmodule.exception.DuplicateResourceException;
import com.vsfsms.staffmodule.exception.ResourceNotFoundException;
import com.vsfsms.staffmodule.model.Salary;
import com.vsfsms.staffmodule.model.Staff;
import com.vsfsms.staffmodule.repository.SalaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

@Service
public class SalaryService {

    @Autowired private SalaryRepository salaryRepository;
    @Autowired private StaffService staffService;
    @Autowired private OvertimeCalculator overtimeCalculator;

    public List<Salary> getAllSalaries() {
        List<Salary> all = salaryRepository.findAll();
        all.forEach(this::refreshIfStale);
        return all;
    }

    public List<Salary> getSalariesByStaff(String staffId) {
        List<Salary> list = salaryRepository.findByStaffId(staffId);
        list.forEach(this::refreshIfStale);
        return list;
    }

    public Salary getSalaryById(String salaryId) {
        Salary s = salaryRepository.findById(salaryId)
                .orElseThrow(() -> new ResourceNotFoundException("Salary record not found with id: " + salaryId));
        refreshIfStale(s);
        return s;
    }

    public Salary createSalary(Salary salary) {
        if (salary.getSalaryId() == null || salary.getSalaryId().isBlank()) {
            salary.setSalaryId(generateId());
        } else if (salaryRepository.existsById(salary.getSalaryId())) {
            throw new DuplicateResourceException("salaryId", "A salary record with ID " + salary.getSalaryId() + " already exists.");
        }
        validateAndCalculate(salary);
        if (salaryRepository.existsByStaffIdAndMonth(salary.getStaffId(), salary.getMonth())) {
            throw new DuplicateResourceException("month", "A salary record for this staff member for " + salary.getMonth() + " already exists.");
        }
        return salaryRepository.save(salary);
    }

    public Salary updateSalary(String salaryId, Salary updated) {
        Salary existing = getSalaryById(salaryId);
        validateAndCalculate(updated);
        if (salaryRepository.existsByStaffIdAndMonthAndSalaryIdNot(updated.getStaffId(), updated.getMonth(), salaryId)) {
            throw new DuplicateResourceException("month", "A salary record for this staff member for " + updated.getMonth() + " already exists.");
        }
        existing.setStaffId(updated.getStaffId());
        existing.setMonth(updated.getMonth());
        existing.setBasicSalary(updated.getBasicSalary());
        existing.setOvertimeHours(updated.getOvertimeHours());
        existing.setOvertimePay(updated.getOvertimePay());
        existing.setDeductions(updated.getDeductions());
        existing.setPaymentDate(updated.getPaymentDate());
        existing.setPaymentStatus(updated.getPaymentStatus());
        existing.setNetSalary(updated.getNetSalary());
        return salaryRepository.save(existing);
    }

    public void deleteSalary(String salaryId) {
        if (!salaryRepository.existsById(salaryId)) {
            throw new ResourceNotFoundException("Salary record not found with id: " + salaryId);
        }
        salaryRepository.deleteById(salaryId);
    }

    /** Overtime the salary form would get, without saving anything. */
    public OvertimeCalculator.Result previewOvertime(String staffId, String month, BigDecimal basicSalary) {
        staffService.assertStaffExists(staffId);
        if (month == null || !month.matches("^\\d{4}-(0[1-9]|1[0-2])$")) {
            throw new BusinessValidationException("month", "Month must be in YYYY-MM format, e.g. 2026-09");
        }
        BigDecimal basic = basicSalary != null ? basicSalary : staffService.getStaffById(staffId).getBasicSalary();
        return overtimeCalculator.calculate(staffId, month, basic);
    }

    public java.util.Map<String, Object> overtimeRules() {
        return overtimeCalculator.rules();
    }

    /**
     * Called when attendance changes: refreshes overtime (and net) on that
     * month's salary record. PAID records are left untouched, since that
     * money has already gone out.
     */
    public void recalculateOvertime(String staffId, LocalDate date) {
        if (staffId == null || date == null) return;
        String month = YearMonth.from(date).toString();
        salaryRepository.findByStaffIdAndMonth(staffId, month).ifPresent(this::refreshIfStale);
    }

    /**
     * Re-applies the overtime calculation to an unpaid record and saves it if
     * anything changed. This keeps records created before attendance was
     * entered (or by an older version of the app, which left overtime at 0)
     * in step with the attendance data. PAID records are never changed.
     */
    private void refreshIfStale(Salary s) {
        if (s.getPaymentStatus() == Salary.PaymentStatus.PAID) return;
        if (s.getStaffId() == null || s.getMonth() == null || s.getBasicSalary() == null) return;
        BigDecimal oldHours = s.getOvertimeHours(), oldPay = s.getOvertimePay(), oldNet = s.getNetSalary();
        applyOvertimeAndNet(s);
        if (s.getNetSalary().signum() < 0) s.setNetSalary(BigDecimal.ZERO);
        if (differs(oldHours, s.getOvertimeHours()) || differs(oldPay, s.getOvertimePay())
                || differs(oldNet, s.getNetSalary())) {
            salaryRepository.save(s);
        }
    }

    /** overtime from attendance, then net = basic + overtime pay - deductions. */
    private void applyOvertimeAndNet(Salary s) {
        OvertimeCalculator.Result ot = overtimeCalculator.calculate(s.getStaffId(), s.getMonth(), s.getBasicSalary());
        s.setOvertimeHours(ot.overtimeHours());
        s.setOvertimePay(ot.overtimePay());
        if (s.getDeductions() == null) s.setDeductions(BigDecimal.ZERO);
        s.setNetSalary(s.getBasicSalary().add(ot.overtimePay()).subtract(s.getDeductions())
                .setScale(2, java.math.RoundingMode.HALF_UP));
    }

    private static boolean differs(BigDecimal a, BigDecimal b) {
        if (a == null || b == null) return a != b;
        return a.compareTo(b) != 0;
    }

    // ------------------------------------------------------------------
    // Business rules
    //  * staff member must exist
    //  * month can't be before the month they joined, nor in the future
    //  * deductions can't exceed basic + overtime (net must be >= 0)
    //  * PAID requires a payment date; payment date can't be in the future
    //    or before the start of the salary month
    //  * overtime hours/pay come from the month's attendance (OvertimeCalculator)
    //  * net = basic + overtime - deductions (always server-calculated)
    // ------------------------------------------------------------------
    private void validateAndCalculate(Salary s) {
        staffService.assertStaffExists(s.getStaffId());
        Staff staff = staffService.getStaffById(s.getStaffId());

        YearMonth month = YearMonth.parse(s.getMonth());
        if (month.isBefore(YearMonth.from(staff.getDateJoined()))) {
            throw new BusinessValidationException("month", "Month is before the staff member joined (" + staff.getDateJoined() + ").");
        }
        if (month.isAfter(YearMonth.now())) {
            throw new BusinessValidationException("month", "Salary month cannot be in the future.");
        }

        applyOvertimeAndNet(s);
        if (s.getNetSalary().signum() < 0) {
            throw new BusinessValidationException("deductions", "Deductions cannot be greater than basic salary + overtime.");
        }

        LocalDate pd = s.getPaymentDate();
        if (s.getPaymentStatus() == Salary.PaymentStatus.PAID && pd == null) {
            throw new BusinessValidationException("paymentDate", "Payment date is required when status is PAID.");
        }
        if (pd != null) {
            if (pd.isAfter(LocalDate.now())) {
                throw new BusinessValidationException("paymentDate", "Payment date cannot be in the future.");
            }
            if (pd.isBefore(month.atDay(1))) {
                throw new BusinessValidationException("paymentDate", "Payment date cannot be before the salary month.");
            }
        }
    }

    private String generateId() {
        String id;
        do {
            id = "SAL" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (salaryRepository.existsById(id));
        return id;
    }
}
