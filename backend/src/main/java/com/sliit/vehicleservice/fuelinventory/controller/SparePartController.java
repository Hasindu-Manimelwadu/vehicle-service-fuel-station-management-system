package com.sliit.vehicleservice.fuelinventory.controller;

import com.sliit.vehicleservice.fuelinventory.dto.ApiResponse;
import com.sliit.vehicleservice.fuelinventory.dto.SparePartQuantityUpdateDTO;
import com.sliit.vehicleservice.fuelinventory.dto.SparePartRequestDTO;
import com.sliit.vehicleservice.fuelinventory.dto.SparePartResponseDTO;
import com.sliit.vehicleservice.fuelinventory.service.SparePartService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/spare-parts")
@CrossOrigin(origins = "*")
public class SparePartController {

    private final SparePartService sparePartService;

    public SparePartController(SparePartService sparePartService) {
        this.sparePartService = sparePartService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SparePartResponseDTO>> createSparePart(
            @Valid @RequestBody SparePartRequestDTO requestDTO) {
        SparePartResponseDTO created = sparePartService.createSparePart(requestDTO);
        return new ResponseEntity<>(
                ApiResponse.success("Spare part added successfully", created),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SparePartResponseDTO>>> getAllSpareParts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Boolean lowStockOnly) {
        List<SparePartResponseDTO> parts = sparePartService.getAllSpareParts(search, category, status, lowStockOnly);
        return ResponseEntity.ok(ApiResponse.success(parts));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SparePartResponseDTO>> getSparePartById(@PathVariable Long id) {
        SparePartResponseDTO part = sparePartService.getSparePartById(id);
        return ResponseEntity.ok(ApiResponse.success(part));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SparePartResponseDTO>> updateSparePart(
            @PathVariable Long id,
            @Valid @RequestBody SparePartRequestDTO requestDTO) {
        SparePartResponseDTO updated = sparePartService.updateSparePart(id, requestDTO);
        return ResponseEntity.ok(ApiResponse.success("Spare part updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSparePart(@PathVariable Long id) {
        sparePartService.deleteSparePart(id);
        return ResponseEntity.ok(ApiResponse.success("Spare part removed successfully", null));
    }

    @PatchMapping("/{id}/quantity")
    public ResponseEntity<ApiResponse<SparePartResponseDTO>> updateQuantity(
            @PathVariable Long id,
            @Valid @RequestBody SparePartQuantityUpdateDTO updateDTO) {
        SparePartResponseDTO updated = sparePartService.updateQuantity(id, updateDTO);
        return ResponseEntity.ok(ApiResponse.success("Spare part quantity updated successfully", updated));
    }

    @GetMapping("/low-stock")
    public ResponseEntity<ApiResponse<List<SparePartResponseDTO>>> getLowStockSpareParts() {
        List<SparePartResponseDTO> lowStockParts = sparePartService.getLowStockSpareParts();
        return ResponseEntity.ok(ApiResponse.success("Low stock spare parts retrieved", lowStockParts));
    }

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<String>>> getAllCategories() {
        List<String> categories = sparePartService.getAllCategories();
        return ResponseEntity.ok(ApiResponse.success(categories));
    }
}