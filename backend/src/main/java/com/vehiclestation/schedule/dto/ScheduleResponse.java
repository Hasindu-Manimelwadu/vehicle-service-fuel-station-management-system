package com.vehiclestation.schedule.dto;

import com.vehiclestation.schedule.enums.ScheduleStatus;

import java.time.LocalDate;
import java.time.LocalTime;

public class ScheduleResponse {

    private Long scheduleId;
    private LocalDate scheduledDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private ScheduleStatus scheduleStatus;

    public ScheduleResponse() {
    }

    public ScheduleResponse(
            Long scheduleId,
            LocalDate scheduledDate,
            LocalTime startTime,
            LocalTime endTime,
            ScheduleStatus scheduleStatus
    ) {
        this.scheduleId = scheduleId;
        this.scheduledDate = scheduledDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.scheduleStatus = scheduleStatus;
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
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public ScheduleStatus getScheduleStatus() {
        return scheduleStatus;
    }

    public void setScheduleStatus(ScheduleStatus scheduleStatus) {
        this.scheduleStatus = scheduleStatus;
    }
}
