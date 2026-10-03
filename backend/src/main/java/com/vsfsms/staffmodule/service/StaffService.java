package com.vsfsms.staffmodule.service;

import com.vsfsms.staffmodule.exception.BusinessValidationException;
import com.vsfsms.staffmodule.exception.DuplicateResourceException;
import com.vsfsms.staffmodule.exception.ResourceNotFoundException;
import com.vsfsms.staffmodule.model.Staff;
import com.vsfsms.staffmodule.repository.AttendanceRepository;
import com.vsfsms.staffmodule.repository.SalaryRepository;
import com.vsfsms.staffmodule.repository.StaffRepository;
import com.vsfsms.staffmodule.repository.WorkShiftRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class StaffService {

    @Autowired private StaffRepository staffRepository;
    @Autowired private WorkShiftRepository workShiftRepository;
    @Autowired private AttendanceRepository attendanceRepository;
    @Autowired private SalaryRepository salaryRepository;

    public List<Staff> getAllStaff() {
        return staffRepository.findAll();
    }

    public List<Staff> searchStaff(String q) {
        String t = q.trim();
        return staffRepository
                .findByFullNameContainingIgnoreCaseOrEmployeeNoContainingIgnoreCaseOrEmailContainingIgnoreCaseOrDesignationContainingIgnoreCase(t, t, t, t);
    }

    public Staff getStaffById(String staffId) {
        return staffRepository.findById(staffId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found with id: " + staffId));
    }

    /** Used by the other services to reject records that point at a non-existent staff member. */
    public void assertStaffExists(String staffId) {
        if (staffId == null || !staffRepository.existsById(staffId)) {
            throw new BusinessValidationException(
                    "staffId", "No staff member exists with id: " + staffId);
        }
    }

    public Staff createStaff(Staff staff) {
        normalize(staff);
        if (staff.getStaffId() == null || staff.getStaffId().isBlank()) {
            staff.setStaffId(generateId());
        } else if (staffRepository.existsById(staff.getStaffId())) {
            throw new DuplicateResourceException("staffId", "A staff member with ID " + staff.getStaffId() + " already exists.");
        }
        if (staffRepository.existsByEmailIgnoreCase(staff.getEmail())) {
            throw new DuplicateResourceException("email", "A staff member with this email already exists.");
        }
        if (staffRepository.existsByEmployeeNoIgnoreCase(staff.getEmployeeNo())) {
            throw new DuplicateResourceException("employeeNo", "A staff member with this employee number already exists.");
        }
        return staffRepository.save(staff);
    }

    public Staff updateStaff(String staffId, Staff updated) {
        Staff existing = getStaffById(staffId);
        normalize(updated);
        if (staffRepository.existsByEmailIgnoreCaseAndStaffIdNot(updated.getEmail(), staffId)) {
            throw new DuplicateResourceException("email", "Another staff member already uses this email.");
        }
        if (staffRepository.existsByEmployeeNoIgnoreCaseAndStaffIdNot(updated.getEmployeeNo(), staffId)) {
            throw new DuplicateResourceException("employeeNo", "Another staff member already uses this employee number.");
        }
        existing.setEmployeeNo(updated.getEmployeeNo());
        existing.setFullName(updated.getFullName());
        existing.setPhone(updated.getPhone());
        existing.setEmail(updated.getEmail());
        existing.setDesignation(updated.getDesignation());
        existing.setDateJoined(updated.getDateJoined());
        existing.setEmploymentStatus(updated.getEmploymentStatus());
        existing.setBasicSalary(updated.getBasicSalary());
        existing.setUserId(updated.getUserId());
        return staffRepository.save(existing);
    }

    /**
     * Deletes the staff member and all their shifts, attendance and salary rows.
     * (schema.sql has ON DELETE CASCADE, but tables auto-created by Hibernate
     * don't, so we clean up explicitly to never leave orphaned records.)
     */
    @Transactional
    public void deleteStaff(String staffId) {
        if (!staffRepository.existsById(staffId)) {
            throw new ResourceNotFoundException("Staff not found with id: " + staffId);
        }
        workShiftRepository.deleteByStaffId(staffId);
        attendanceRepository.deleteByStaffId(staffId);
        salaryRepository.deleteByStaffId(staffId);
        staffRepository.deleteById(staffId);
    }

    // ------------------------------------------------------------------
    private void normalize(Staff s) {
        if (s.getFullName() != null) s.setFullName(s.getFullName().trim().replaceAll("\\s+", " "));
        if (s.getEmail() != null) s.setEmail(s.getEmail().trim().toLowerCase());
        if (s.getEmployeeNo() != null) s.setEmployeeNo(s.getEmployeeNo().trim().toUpperCase());
        if (s.getPhone() != null) s.setPhone(s.getPhone().trim());
        if (s.getDesignation() != null) s.setDesignation(s.getDesignation().trim());
        if (s.getUserId() != null && s.getUserId().isBlank()) s.setUserId(null);
        if (s.getStaffId() != null) s.setStaffId(s.getStaffId().trim());
    }

    private String generateId() {
        String id;
        do {
            id = "STF" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (staffRepository.existsById(id));
        return id;
    }
}
