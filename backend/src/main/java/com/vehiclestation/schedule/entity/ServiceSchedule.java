package com.vehiclestation.schedule.entity;

import com.vehiclestation.schedule.enums.ScheduleStatus;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(
        name = "service_schedules",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_schedule_date_start",
                        columnNames = {"scheduled_date", "start_time"}
                )
        }
)
public class ServiceSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Long scheduleId;

    @Column(name = "scheduled_date", nullable = false)
    private LocalDate scheduledDate;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "schedule_status", nullable = false, length = 20)
    private ScheduleStatus scheduleStatus;

    public ServiceSchedule() {
    }

    public ServiceSchedule(
            LocalDate scheduledDate,
            LocalTime startTime,
            LocalTime endTime
    ) {
        if (startTime != null && endTime != null && !endTime.isAfter(startTime)) {
            throw new IllegalArgumentException("Schedule end time must be later than start time");
        }
        this.scheduledDate = scheduledDate;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @PrePersist
    @PreUpdate
    public void validateAndPrepare() {
        if (scheduleStatus == null) {
            scheduleStatus = ScheduleStatus.AVAILABLE;
        }
        if (startTime != null && endTime != null && !endTime.isAfter(startTime)) {
            throw new IllegalStateException("Schedule end time must be later than start time");
        }
    }

    public Long getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Long scheduleId) {
        this.scheduleId = scheduleId;
    }

    public LocalDate getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(LocalDate scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        if (this.endTime != null && startTime != null && !this.endTime.isAfter(startTime)) {
            throw new IllegalArgumentException("Schedule end time must be later than start time");
        }
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        if (this.startTime != null && endTime != null && !endTime.isAfter(this.startTime)) {
            throw new IllegalArgumentException("Schedule end time must be later than start time");
        }
        this.endTime = endTime;
    }

    public ScheduleStatus getScheduleStatus() {
        return scheduleStatus;
    }

    public void setScheduleStatus(ScheduleStatus scheduleStatus) {
        this.scheduleStatus = scheduleStatus;
    }
}
