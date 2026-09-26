package com.sliit.vehicleservice.fuelinventory.controller;

import com.sliit.vehicleservice.fuelinventory.dto.ApiResponse;
import com.sliit.vehicleservice.fuelinventory.dto.DashboardSummaryDTO;
import com.sliit.vehicleservice.fuelinventory.service.InventoryDashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/inventory")
@CrossOrigin(origins = "*")
public class InventoryDashboardController {

    private final InventoryDashboardService dashboardService;

    public InventoryDashboardController(InventoryDashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/dashboard-summary")
    public ResponseEntity<ApiResponse<DashboardSummaryDTO>> getDashboardSummary() {
        DashboardSummaryDTO summary = dashboardService.getDashboardSummary();
        return ResponseEntity.ok(ApiResponse.success("Inventory summary loaded", summary));
    }
}