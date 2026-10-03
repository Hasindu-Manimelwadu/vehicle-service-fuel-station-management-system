package com.vsfsms.staffmodule.service;

import com.vsfsms.staffmodule.exception.BusinessValidationException;
import com.vsfsms.staffmodule.exception.DuplicateResourceException;
import com.vsfsms.staffmodule.exception.ResourceNotFoundException;
import com.vsfsms.staffmodule.model.Staff;
import com.vsfsms.staffmodule.model.WorkShift;
import com.vsfsms.staffmodule.repository.WorkShiftRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class WorkShiftService {

    private static final int MAX_SHIFT_MINUTES = 16 * 60;

    @Autowired private WorkShiftRepository workShiftRepository;
    @Autowired private StaffService staffService;
    @Autowired private SalaryService salaryService;   // shift length sets the normal day for overtime

    public List<WorkShift> getAllShifts() {
        return workShiftRepository.findAll();
    }

    public List<WorkShift> getShiftsByStaff(String staffId) {
        return workShiftRepository.findByStaffId(staffId);
    }

    public WorkShift getShiftById(String shiftId) {
        return workShiftRepository.findById(shiftId)
                .orElseThrow(() -> new ResourceNotFoundException("Work shift not found with id: " + shiftId));
    }

    public WorkShift createShift(WorkShift shift) {
        if (shift.getShiftId() == null || shift.getShiftId().isBlank()) {
            shift.setShiftId(generateId());
        } else if (workShiftRepository.existsById(shift.getShiftId())) {
            throw new DuplicateResourceException("shiftId", "A shift with ID " + shift.getShiftId() + " already exists.");
        }
        validate(shift, null);
        WorkShift saved = workShiftRepository.save(shift);
        salaryService.recalculateOvertime(saved.getStaffId(), saved.getShiftDate());
        return saved;
    }

    public WorkShift updateShift(String shiftId, WorkShift updated) {
        WorkShift existing = getShiftById(shiftId);
        validate(updated, shiftId);
        String oldStaff = existing.getStaffId();
        LocalDate oldDate = existing.getShiftDate();
        existing.setStaffId(updated.getStaffId());
        existing.setShiftDate(updated.getShiftDate());
        existing.setStartTime(updated.getStartTime());
        existing.setEndTime(updated.getEndTime());
        existing.setShiftStatus(updated.getShiftStatus());
        WorkShift saved = workShiftRepository.save(existing);
        salaryService.recalculateOvertime(saved.getStaffId(), saved.getShiftDate());
        salaryService.recalculateOvertime(oldStaff, oldDate);
        return saved;
    }

    public void deleteShift(String shiftId) {
        WorkShift existing = getShiftById(shiftId);
        workShiftRepository.deleteById(shiftId);
        salaryService.recalculateOvertime(existing.getStaffId(), existing.getShiftDate());
    }

    // ------------------------------------------------------------------
    // Business rules
    //  * staff member must exist and not be TERMINATED
    //  * end time must differ from start time; an end time earlier than the
    //    start is treated as an overnight shift (e.g. 22:00 -> 06:00)
    //  * a shift may not be longer than 16 hours
    //  * no overlapping (non-cancelled) shifts for the same staff on the same date
    //  * new SCHEDULED shifts can't be placed in the past
    // ------------------------------------------------------------------
    private void validate(WorkShift s, String selfId) {
        staffService.assertStaffExists(s.getStaffId());
        Staff staff = staffService.getStaffById(s.getStaffId());
        if (staff.getEmploymentStatus() == Staff.EmploymentStatus.TERMINATED
                && s.getShiftStatus() != WorkShift.ShiftStatus.CANCELLED) {
            throw new BusinessValidationException("staffId", "Cannot assign shifts to a terminated staff member.");
        }

        if (s.getShiftDate().isBefore(staff.getDateJoined())) {
            throw new BusinessValidationException("shiftDate", "Shift date is before the staff member joined (" + staff.getDateJoined() + ").");
        }

        if (selfId == null && s.getShiftStatus() == WorkShift.ShiftStatus.SCHEDULED
                && s.getShiftDate().isBefore(LocalDate.now())) {
            throw new BusinessValidationException("shiftDate", "A new scheduled shift cannot be in the past.");
        }

        int start = minutes(s.getStartTime().getHour(), s.getStartTime().getMinute());
        int end = minutes(s.getEndTime().getHour(), s.getEndTime().getMinute());
        if (start == end) {
            throw new BusinessValidationException("endTime", "End time must be different from start time.");
        }
        if (end < start) end += 24 * 60; // overnight
        if (end - start > MAX_SHIFT_MINUTES) {
            throw new BusinessValidationException("endTime", "A shift cannot be longer than 16 hours.");
        }

        if (s.getShiftStatus() == WorkShift.ShiftStatus.CANCELLED) return;

        for (WorkShift other : workShiftRepository.findByStaffIdAndShiftDate(s.getStaffId(), s.getShiftDate())) {
            if (other.getShiftId().equals(selfId) || other.getShiftStatus() == WorkShift.ShiftStatus.CANCELLED) continue;
            int os = minutes(other.getStartTime().getHour(), other.getStartTime().getMinute());
            int oe = minutes(other.getEndTime().getHour(), other.getEndTime().getMinute());
            if (oe <= os) oe += 24 * 60;
            if (start < oe && os < end) {
                throw new BusinessValidationException("startTime",
                        "This overlaps shift " + other.getShiftId() + " (" + other.getStartTime() + "-" + other.getEndTime() + ") for the same staff member.");
            }
        }
    }

    private static int minutes(int h, int m) { return h * 60 + m; }

    private String generateId() {
        String id;
        do {
            id = "SHF" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (workShiftRepository.existsById(id));
        return id;
    }
}
