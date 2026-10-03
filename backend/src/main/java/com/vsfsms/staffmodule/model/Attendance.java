package com.vsfsms.staffmodule.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "attendance", uniqueConstraints = @UniqueConstraint(columnNames = {"staff_id", "date"}))
public class Attendance {

    @Id
    @Size(max = 20, message = "Attendance ID must be at most 20 characters")
    @Pattern(regexp = "^[A-Za-z0-9_-]*$", message = "Attendance ID may contain only letters, digits, '-' and '_'")
    @Column(name = "attendance_id", length = 20)
    private String attendanceId;

    @NotBlank(message = "Staff member is required")
    @Size(max = 20, message = "Staff ID must be at most 20 characters")
    @Column(name = "staff_id", nullable = false, length = 20)
    private String staffId;

    @NotNull(message = "Date is required")
    @PastOrPresent(message = "Attendance cannot be recorded for a future date")
    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "check_in_time")
    private LocalTime checkInTime;

    @Column(name = "check_out_time")
    private LocalTime checkOutTime;

    // Calculated from check-in/out by AttendanceService when both times are present
    @DecimalMin(value = "0.00", message = "Working hours cannot be negative")
    @DecimalMax(value = "24.00", message = "Working hours cannot exceed 24")
    @Digits(integer = 2, fraction = 2, message = "Working hours can have at most 2 decimal places")
    @Column(name = "working_hours", precision = 5, scale = 2)
    private BigDecimal workingHours;

    @NotNull(message = "Attendance status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "attendance_status", nullable = false, length = 20)
    private AttendanceStatus attendanceStatus = AttendanceStatus.PRESENT;

    // Overtime for this day (worked hours beyond the scheduled shift, or the
    // standard day if no shift). Not stored - filled in by AttendanceService.
    @Transient
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private BigDecimal overtimeHours;

    public enum AttendanceStatus {
        PRESENT, ABSENT, LATE, HALF_DAY, ON_LEAVE
    }

    // ---- Getters & Setters ----
    public String getAttendanceId() { return attendanceId; }
    public void setAttendanceId(String attendanceId) { this.attendanceId = attendanceId; }

    public String getStaffId() { return staffId; }
    public void setStaffId(String staffId) { this.staffId = staffId; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalTime getCheckInTime() { return checkInTime; }
    public void setCheckInTime(LocalTime checkInTime) { this.checkInTime = checkInTime; }

    public LocalTime getCheckOutTime() { return checkOutTime; }
    public void setCheckOutTime(LocalTime checkOutTime) { this.checkOutTime = checkOutTime; }

    public BigDecimal getWorkingHours() { return workingHours; }
    public void setWorkingHours(BigDecimal workingHours) { this.workingHours = workingHours; }

    public BigDecimal getOvertimeHours() { return overtimeHours; }
    public void setOvertimeHours(BigDecimal overtimeHours) { this.overtimeHours = overtimeHours; }

    public AttendanceStatus getAttendanceStatus() { return attendanceStatus; }
    public void setAttendanceStatus(AttendanceStatus attendanceStatus) { this.attendanceStatus = attendanceStatus; }
}
