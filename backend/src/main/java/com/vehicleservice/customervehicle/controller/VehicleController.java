package com.sliit.vehiclemgmt.controller;

import com.sliit.vehiclemgmt.dto.ApiResponse;
import com.sliit.vehiclemgmt.dto.ServiceRecordDTO;
import com.sliit.vehiclemgmt.dto.VehicleDTO;
import com.sliit.vehiclemgmt.service.VehicleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Vehicle Management endpoints.
 * Handles registering new vehicles, updating details, deletion, customer vehicle queries, and service history.
 */
@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    @Autowired
    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    /**
     * Register a new vehicle
     * POST /api/vehicles
     */
    @PostMapping
    public ResponseEntity<ApiResponse<VehicleDTO>> registerVehicle(@Valid @RequestBody VehicleDTO dto) {
        VehicleDTO created = vehicleService.registerVehicle(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Vehicle registered successfully.", created));
    }

    /**
     * Get all vehicles belonging to a specific customer
     * GET /api/vehicles/customer/{customerId}
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<ApiResponse<List<VehicleDTO>>> getVehiclesByCustomer(@PathVariable("customerId") Long customerId) {
        List<VehicleDTO> vehicles = vehicleService.getVehiclesByCustomer(customerId);
        return ResponseEntity.ok(ApiResponse.success("Customer vehicles retrieved successfully.", vehicles));
    }

    /**
     * Get a specific vehicle by ID
     * GET /api/vehicles/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VehicleDTO>> getVehicleById(@PathVariable("id") Long id) {
        VehicleDTO vehicle = vehicleService.getVehicleById(id);
        return ResponseEntity.ok(ApiResponse.success("Vehicle retrieved successfully.", vehicle));
    }

    /**
     * Update an existing vehicle
     * PUT /api/vehicles/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<VehicleDTO>> updateVehicle(
            @PathVariable("id") Long id,
            @Valid @RequestBody VehicleDTO dto) {
        VehicleDTO updated = vehicleService.updateVehicle(id, dto);
        return ResponseEntity.ok(ApiResponse.success("Vehicle updated successfully.", updated));
    }

    /**
     * Delete a vehicle by ID
     * DELETE /api/vehicles/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteVehicle(@PathVariable("id") Long id) {
        vehicleService.deleteVehicle(id);
        return ResponseEntity.ok(ApiResponse.success("Vehicle removed successfully.", null));
    }

    /**
     * View vehicle service history (read-only integration with Service Records table)
     * GET /api/vehicles/{id}/service-history
     */
    @GetMapping("/{id}/service-history")
    public ResponseEntity<ApiResponse<List<ServiceRecordDTO>>> getVehicleServiceHistory(@PathVariable("id") Long id) {
        List<ServiceRecordDTO> history = vehicleService.getServiceHistory(id);
        return ResponseEntity.ok(ApiResponse.success("Service history retrieved successfully.", history));
    }
}
