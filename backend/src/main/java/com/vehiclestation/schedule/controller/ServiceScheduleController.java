package com.vehiclestation.schedule.controller;

import com.vehiclestation.schedule.dto.ScheduleRequest;
import com.vehiclestation.schedule.dto.ScheduleResponse;
import com.vehiclestation.schedule.entity.ServiceSchedule;
import com.vehiclestation.schedule.service.ServiceScheduleService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/schedules")
public class ServiceScheduleController {

    private final ServiceScheduleService scheduleService;

    public ServiceScheduleController(ServiceScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    /**
     * Retrieves all AVAILABLE schedule slots for a given date.
     */
    @GetMapping("/available")
    public ResponseEntity<List<ScheduleResponse>> getAvailableSlots(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        List<ScheduleResponse> responses = scheduleService.getAvailableSlots(date)
                .stream()
                .map(this::mapToResponse)
                .toList();

        return ResponseEntity.ok(responses);
    }

    /**
     * Creates a new service schedule slot. Restricted to ADMIN users.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ScheduleResponse> createSchedule(
            @Valid @RequestBody ScheduleRequest request
    ) {
        ServiceSchedule created = scheduleService.createSchedule(
                request.getScheduledDate(),
                request.getStartTime(),
                request.getEndTime()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(mapToResponse(created));
    }

    private ScheduleResponse mapToResponse(ServiceSchedule schedule) {
        return new ScheduleResponse(
                schedule.getScheduleId(),
                schedule.getScheduledDate(),
                schedule.getStartTime(),
                schedule.getEndTime(),
                schedule.getScheduleStatus()
        );
    }
}
