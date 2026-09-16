package com.sliit.vehicleservice.fuelinventory.controller;

import com.sliit.vehicleservice.fuelinventory.dto.ApiResponse;
import com.sliit.vehicleservice.fuelinventory.dto.FuelQuantityUpdateDTO;
import com.sliit.vehicleservice.fuelinventory.dto.FuelStockRequestDTO;
import com.sliit.vehicleservice.fuelinventory.dto.FuelStockResponseDTO;
import com.sliit.vehicleservice.fuelinventory.dto.PriceUpdateDTO;
import com.sliit.vehicleservice.fuelinventory.service.FuelStockService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/fuel-stocks")
@CrossOrigin(origins = "*")
public class FuelStockController {

    private final FuelStockService fuelStockService;

    public FuelStockController(FuelStockService fuelStockService) {
        this.fuelStockService = fuelStockService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<FuelStockResponseDTO>> createFuelStock(
            @Valid @RequestBody FuelStockRequestDTO requestDTO) {
        FuelStockResponseDTO created = fuelStockService.createFuelStock(requestDTO);
        return new ResponseEntity<>(
                ApiResponse.success("Fuel stock created successfully", created),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<FuelStockResponseDTO>>> getAllFuelStocks(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean lowStockOnly,
            @RequestParam(required = false) String tankNo) {
        List<FuelStockResponseDTO> stocks = fuelStockService.getAllFuelStocks(search, lowStockOnly, tankNo);
        return ResponseEntity.ok(ApiResponse.success(stocks));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FuelStockResponseDTO>> getFuelStockById(@PathVariable Long id) {
        FuelStockResponseDTO stock = fuelStockService.getFuelStockById(id);
        return ResponseEntity.ok(ApiResponse.success(stock));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FuelStockResponseDTO>> updateFuelStock(
            @PathVariable Long id,
            @Valid @RequestBody FuelStockRequestDTO requestDTO) {
        FuelStockResponseDTO updated = fuelStockService.updateFuelStock(id, requestDTO);
        return ResponseEntity.ok(ApiResponse.success("Fuel stock updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteFuelStock(@PathVariable Long id) {
        fuelStockService.deleteFuelStock(id);
        return ResponseEntity.ok(ApiResponse.success("Fuel stock deleted successfully", null));
    }

    @PatchMapping("/{id}/quantity")
    public ResponseEntity<ApiResponse<FuelStockResponseDTO>> updateQuantity(
            @PathVariable Long id,
            @Valid @RequestBody FuelQuantityUpdateDTO updateDTO) {
        FuelStockResponseDTO updated = fuelStockService.updateQuantity(id, updateDTO);
        return ResponseEntity.ok(ApiResponse.success("Fuel stock quantity updated successfully", updated));
    }

    @PatchMapping("/{id}/price")
    public ResponseEntity<ApiResponse<FuelStockResponseDTO>> updatePrice(
            @PathVariable Long id,
            @Valid @RequestBody PriceUpdateDTO priceUpdateDTO) {
        FuelStockResponseDTO updated = fuelStockService.updatePrice(id, priceUpdateDTO);
        return ResponseEntity.ok(ApiResponse.success("Fuel unit price updated successfully", updated));
    }

    @GetMapping("/low-stock")
    public ResponseEntity<ApiResponse<List<FuelStockResponseDTO>>> getLowStockFuelStocks() {
        List<FuelStockResponseDTO> lowStocks = fuelStockService.getLowStockFuelStocks();
        return ResponseEntity.ok(ApiResponse.success("Low stock fuel tanks retrieved", lowStocks));
    }
}