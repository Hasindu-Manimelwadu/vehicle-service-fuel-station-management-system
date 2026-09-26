package com.vehiclestation.schedule.repository;

import com.vehiclestation.schedule.entity.ServiceSchedule;
import com.vehiclestation.schedule.enums.ScheduleStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ServiceScheduleRepository extends JpaRepository<ServiceSchedule, Long> {

    boolean existsByScheduledDateAndStartTime(
            LocalDate scheduledDate,
            java.time.LocalTime startTime
    );

    List<ServiceSchedule> findByScheduledDateAndScheduleStatusOrderByStartTimeAsc(
            LocalDate scheduledDate,
            ScheduleStatus scheduleStatus
    );

    /**
     * Acquires a pessimistic write lock on the schedule row to prevent
     * concurrent double-bookings of the same time slot.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM ServiceSchedule s WHERE s.scheduleId = :id")
    Optional<ServiceSchedule> findByIdWithLock(@Param("id") Long id);
}
