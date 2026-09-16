package com.vehiclestation.servicerecord.controller;

import com.vehiclestation.servicerecord.dto.*;
import com.vehiclestation.servicerecord.service.ServiceRecordService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/service-records")
public class ServiceRecordController {

    private final ServiceRecordService serviceRecordService;

    public ServiceRecordController(ServiceRecordService serviceRecordService) {
        this.serviceRecordService = serviceRecordService;
    }

    @PostMapping
    public ResponseEntity<ServiceRecordResponse> create(
            @Valid @RequestBody CreateServiceRecordRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(serviceRecordService.create(request));
    }

    @GetMapping
    public List<ServiceRecordResponse> getAll() {
        return serviceRecordService.getAll();
    }

    @GetMapping("/technicians")
    public List<TechnicianOptionResponse> getAvailableTechnicians() {
        return serviceRecordService.getAvailableTechnicians();
    }

    @GetMapping("/{id}")
    public ServiceRecordResponse getById(@PathVariable Long id) {
        return serviceRecordService.getById(id);
    }

    @PutMapping("/{id}")
    public ServiceRecordResponse update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateServiceRecordRequest request
    ) {
        return serviceRecordService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
        serviceRecordService.delete(id);
        return ResponseEntity.ok(Map.of("message", "Service record deleted successfully"));
    }

    @PostMapping("/{id}/assign")
    public ServiceRecordResponse assignTechnician(
            @PathVariable Long id,
            @Valid @RequestBody AssignTechnicianRequest request
    ) {
        return serviceRecordService.assignTechnician(id, request);
    }

    @GetMapping("/my-jobs")
    public List<ServiceRecordResponse> getMyJobs(@AuthenticationPrincipal Jwt jwt) {
        return serviceRecordService.getMyJobs(currentUserId(jwt));
    }

    @GetMapping("/my-jobs/{id}")
    public ServiceRecordResponse getMyJob(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return serviceRecordService.getMyJob(currentUserId(jwt), id);
    }

    @PatchMapping("/{id}/start")
    public ServiceRecordResponse startService(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return serviceRecordService.startService(id, currentUserId(jwt));
    }

    @PatchMapping("/{id}/progress")
    public ServiceRecordResponse updateProgress(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody UpdateServiceProgressRequest request
    ) {
        return serviceRecordService.updateProgress(id, currentUserId(jwt), request);
    }

    @PostMapping("/{id}/parts")
    public ServiceRecordResponse addPart(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody AddPartUsageRequest request
    ) {
        return serviceRecordService.addPart(id, currentUserId(jwt), request);
    }

    @DeleteMapping("/{id}/parts/{usageId}")
    public ServiceRecordResponse removePart(
            @PathVariable Long id,
            @PathVariable Long usageId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return serviceRecordService.removePart(id, currentUserId(jwt), usageId);
    }

    @PatchMapping("/{id}/complete")
    public ServiceRecordResponse completeService(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return serviceRecordService.completeService(id, currentUserId(jwt));
    }

    private Long currentUserId(Jwt jwt) {
        Number claim = jwt.getClaim("userId");
        return claim.longValue();
    }
}
