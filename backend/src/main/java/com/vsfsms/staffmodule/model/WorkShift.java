package com.vsfsms.staffmodule.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "work_shift")
public class WorkShift {

    @Id
    @Size(max = 20, message = "Shift ID must be at most 20 characters")
    @Pattern(regexp = "^[A-Za-z0-9_-]*$", message = "Shift ID may contain only letters, digits, '-' and '_'")
    @Column(name = "shift_id", length = 20)
    private String shiftId;

    @NotBlank(message = "Staff member is required")
    @Size(max = 20, message = "Staff ID must be at most 20 characters")
    @Column(name = "staff_id", nullable = false, length = 20)
    private String staffId;

    @NotNull(message = "Shift date is required")
    @Column(name = "shift_date", nullable = false)
    private LocalDate shiftDate;

    @NotNull(message = "Start time is required")
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    // Cross-field rules (end != start, max length, overlaps) live in WorkShiftService
    @NotNull(message = "End time is required")
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @NotNull(message = "Shift status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "shift_status", nullable = false, length = 20)
    private ShiftStatus shiftStatus = ShiftStatus.SCHEDULED;

    public enum ShiftStatus {
        SCHEDULED, ONGOING, COMPLETED, CANCELLED
    }

    // ---- Getters & Setters ----
    public String getShiftId() { return shiftId; }
    public void setShiftId(String shiftId) { this.shiftId = shiftId; }

    public String getStaffId() { return staffId; }
    public void setStaffId(String staffId) { this.staffId = staffId; }

    public LocalDate getShiftDate() { return shiftDate; }
    public void setShiftDate(LocalDate shiftDate) { this.shiftDate = shiftDate; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public ShiftStatus getShiftStatus() { return shiftStatus; }
    public void setShiftStatus(ShiftStatus shiftStatus) { this.shiftStatus = shiftStatus; }
}
