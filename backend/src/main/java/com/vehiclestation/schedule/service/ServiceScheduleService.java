package com.vehiclestation.schedule.service;

import com.vehiclestation.schedule.entity.ServiceSchedule;
import com.vehiclestation.schedule.enums.ScheduleStatus;
import com.vehiclestation.schedule.repository.ServiceScheduleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class ServiceScheduleService {

    private final ServiceScheduleRepository scheduleRepository;

    public ServiceScheduleService(ServiceScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    /**
     * Returns all AVAILABLE slots for the requested date, ordered by start time.
     */
    @Transactional(readOnly = true)
    public List<ServiceSchedule> getAvailableSlots(LocalDate date) {
        return scheduleRepository.findByScheduledDateAndScheduleStatusOrderByStartTimeAsc(
                date,
                ScheduleStatus.AVAILABLE
        );
    }

    /**
     * Creates a new time slot. Admin-only — secured at the controller level.
     * End time is automatically set to one hour after the start time if not provided.
     */
    @Transactional
    public ServiceSchedule createSchedule(
            LocalDate scheduledDate,
            LocalTime startTime,
            LocalTime endTime
    ) {
        LocalTime resolvedEndTime = (endTime != null) ? endTime : startTime.plusHours(1);
        ServiceSchedule schedule = new ServiceSchedule(scheduledDate, startTime, resolvedEndTime);
        return scheduleRepository.save(schedule);
    }
}
