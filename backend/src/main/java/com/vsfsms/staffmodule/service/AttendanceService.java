package com.vsfsms.staffmodule.service;

import com.vsfsms.staffmodule.exception.BusinessValidationException;
import com.vsfsms.staffmodule.exception.DuplicateResourceException;
import com.vsfsms.staffmodule.exception.ResourceNotFoundException;
import com.vsfsms.staffmodule.model.Attendance;
import com.vsfsms.staffmodule.model.Attendance.AttendanceStatus;
import com.vsfsms.staffmodule.model.Staff;
import com.vsfsms.staffmodule.repository.AttendanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
public class AttendanceService {

    @Autowired private AttendanceRepository attendanceRepository;
    @Autowired private StaffService staffService;
    @Autowired private SalaryService salaryService;
    @Autowired private OvertimeCalculator overtimeCalculator;

    public List<Attendance> getAllAttendance() {
        return withOvertime(attendanceRepository.findAll());
    }

    public List<Attendance> getAttendanceByStaff(String staffId) {
        return withOvertime(attendanceRepository.findByStaffId(staffId));
    }

    public Attendance getAttendanceById(String attendanceId) {
        return withOvertime(attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance record not found with id: " + attendanceId)));
    }

    private List<Attendance> withOvertime(List<Attendance> list) {
        list.forEach(this::withOvertime);
        return list;
    }

    private Attendance withOvertime(Attendance a) {
        a.setOvertimeHours(overtimeCalculator.dailyOvertime(a));
        return a;
    }

    public Attendance createAttendance(Attendance attendance) {
        if (attendance.getAttendanceId() == null || attendance.getAttendanceId().isBlank()) {
            attendance.setAttendanceId(generateId());
        } else if (attendanceRepository.existsById(attendance.getAttendanceId())) {
            throw new DuplicateResourceException("attendanceId", "An attendance record with ID " + attendance.getAttendanceId() + " already exists.");
        }
        validateAndCalculate(attendance);
        if (attendanceRepository.existsByStaffIdAndDate(attendance.getStaffId(), attendance.getDate())) {
            throw new DuplicateResourceException("date", "Attendance for this staff member on " + attendance.getDate() + " is already recorded.");
        }
        Attendance saved = attendanceRepository.save(attendance);
        salaryService.recalculateOvertime(saved.getStaffId(), saved.getDate());
        return withOvertime(saved);
    }

    public Attendance updateAttendance(String attendanceId, Attendance updated) {
        Attendance existing = getAttendanceById(attendanceId);
        validateAndCalculate(updated);
        if (attendanceRepository.existsByStaffIdAndDateAndAttendanceIdNot(updated.getStaffId(), updated.getDate(), attendanceId)) {
            throw new DuplicateResourceException("date", "Attendance for this staff member on " + updated.getDate() + " is already recorded.");
        }
        String oldStaff = existing.getStaffId();
        java.time.LocalDate oldDate = existing.getDate();
        existing.setStaffId(updated.getStaffId());
        existing.setDate(updated.getDate());
        existing.setCheckInTime(updated.getCheckInTime());
        existing.setCheckOutTime(updated.getCheckOutTime());
        existing.setWorkingHours(updated.getWorkingHours());
        existing.setAttendanceStatus(updated.getAttendanceStatus());
        Attendance saved = attendanceRepository.save(existing);
        // refresh overtime for the new month and, if it moved, the old one too
        salaryService.recalculateOvertime(saved.getStaffId(), saved.getDate());
        if (!oldStaff.equals(saved.getStaffId()) || !sameMonth(oldDate, saved.getDate())) {
            salaryService.recalculateOvertime(oldStaff, oldDate);
        }
        return withOvertime(saved);
    }

    public void deleteAttendance(String attendanceId) {
        Attendance existing = getAttendanceById(attendanceId);
        attendanceRepository.deleteById(attendanceId);
        salaryService.recalculateOvertime(existing.getStaffId(), existing.getDate());
    }

    private static boolean sameMonth(java.time.LocalDate a, java.time.LocalDate b) {
        return a.getYear() == b.getYear() && a.getMonth() == b.getMonth();
    }

    // ------------------------------------------------------------------
    // Business rules
    //  * staff member must exist; date can't be before they joined
    //  * ABSENT / ON_LEAVE -> no check-in/out, 0 hours
    //  * PRESENT / LATE / HALF_DAY -> check-in required
    //  * check-out requires check-in and must differ from it; a check-out
    //    earlier than the check-in means the day ran past midnight
    //    (e.g. 22:00 -> 06:00 = 8 h), same as overnight work shifts
    //  * working hours are calculated from the two times when both are given
    // ------------------------------------------------------------------
    private void validateAndCalculate(Attendance a) {
        staffService.assertStaffExists(a.getStaffId());
        Staff staff = staffService.getStaffById(a.getStaffId());
        if (a.getDate().isBefore(staff.getDateJoined())) {
            throw new BusinessValidationException("date", "Date is before the staff member joined (" + staff.getDateJoined() + ").");
        }

        AttendanceStatus st = a.getAttendanceStatus();
        boolean notWorking = st == AttendanceStatus.ABSENT || st == AttendanceStatus.ON_LEAVE;

        if (notWorking) {
            if (a.getCheckInTime() != null || a.getCheckOutTime() != null) {
                throw new BusinessValidationException("checkInTime",
                        "Check-in/out times must be empty when status is " + st.name().replace('_', ' ') + ".");
            }
            a.setWorkingHours(BigDecimal.ZERO.setScale(2));
            return;
        }

        if (a.getCheckInTime() == null) {
            throw new BusinessValidationException("checkInTime", "Check-in time is required when status is " + st.name().replace('_', ' ') + ".");
        }
        if (a.getCheckOutTime() != null) {
            if (a.getCheckOutTime().equals(a.getCheckInTime())) {
                throw new BusinessValidationException("checkOutTime", "Check-out time must be different from check-in time.");
            }
            long mins = OvertimeCalculator.spanMinutes(a.getCheckInTime(), a.getCheckOutTime());
            if (mins > 20 * 60) {
                throw new BusinessValidationException("checkOutTime",
                        "That is more than 20 hours of work. Check the check-in and check-out times.");
            }
            a.setWorkingHours(BigDecimal.valueOf(mins).divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP));
        } else {
            // still checked in -> hours unknown yet
            a.setWorkingHours(null);
        }
    }

    private String generateId() {
        String id;
        do {
            id = "ATD" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (attendanceRepository.existsById(id));
        return id;
    }
}
