package com.sliit.vehicleservice.fuelinventory.service.impl;

import com.sliit.vehicleservice.fuelinventory.dto.SparePartQuantityUpdateDTO;
import com.sliit.vehicleservice.fuelinventory.dto.SparePartRequestDTO;
import com.sliit.vehicleservice.fuelinventory.dto.SparePartResponseDTO;
import com.sliit.vehicleservice.fuelinventory.entity.SparePart;
import com.sliit.vehicleservice.fuelinventory.exception.InvalidStockOperationException;
import com.sliit.vehicleservice.fuelinventory.exception.ResourceNotFoundException;
import com.sliit.vehicleservice.fuelinventory.repository.SparePartRepository;
import com.sliit.vehicleservice.fuelinventory.service.SparePartService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SparePartServiceImpl implements SparePartService {

    private final SparePartRepository sparePartRepository;

    public SparePartServiceImpl(SparePartRepository sparePartRepository) {
        this.sparePartRepository = sparePartRepository;
    }

    @Override
    public SparePartResponseDTO createSparePart(SparePartRequestDTO requestDTO) {
        SparePart part = new SparePart();
        part.setPartName(requestDTO.getPartName().trim());
        part.setCategory(requestDTO.getCategory().trim());
        part.setQuantity(requestDTO.getQuantity());
        part.setUnitPrice(requestDTO.getUnitPrice());
        part.setReorderLevel(requestDTO.getReorderLevel());

        String resolvedStatus = resolveStockStatus(requestDTO.getStatus(), requestDTO.getQuantity(), requestDTO.getReorderLevel());
        part.setStatus(resolvedStatus);

        SparePart saved = sparePartRepository.save(part);
        return mapToResponseDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SparePartResponseDTO> getAllSpareParts(String search, String category, String status, Boolean lowStockOnly) {
        List<SparePart> parts;

        if (Boolean.TRUE.equals(lowStockOnly)) {
            parts = sparePartRepository.findLowStockSpareParts();
        } else if (search != null && !search.trim().isEmpty()) {
            parts = sparePartRepository.searchSpareParts(search.trim());
        } else if (category != null && !category.trim().isEmpty()) {
            parts = sparePartRepository.findByCategoryIgnoreCase(category.trim());
        } else if (status != null && !status.trim().isEmpty()) {
            parts = sparePartRepository.findByStatusIgnoreCase(status.trim());
        } else {
            parts = sparePartRepository.findAll();
        }

        return parts.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public SparePartResponseDTO getSparePartById(Long id) {
        SparePart part = sparePartRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Spare part not found with ID: " + id));
        return mapToResponseDTO(part);
    }

    @Override
    public SparePartResponseDTO updateSparePart(Long id, SparePartRequestDTO requestDTO) {
        SparePart existing = sparePartRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Spare part not found with ID: " + id));

        existing.setPartName(requestDTO.getPartName().trim());
        existing.setCategory(requestDTO.getCategory().trim());
        existing.setQuantity(requestDTO.getQuantity());
        existing.setUnitPrice(requestDTO.getUnitPrice());
        existing.setReorderLevel(requestDTO.getReorderLevel());

        String resolvedStatus = resolveStockStatus(requestDTO.getStatus(), requestDTO.getQuantity(), requestDTO.getReorderLevel());
        existing.setStatus(resolvedStatus);

        SparePart updated = sparePartRepository.save(existing);
        return mapToResponseDTO(updated);
    }

    @Override
    public void deleteSparePart(Long id) {
        SparePart part = sparePartRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Spare part not found with ID: " + id));
        sparePartRepository.delete(part);
    }

    @Override
    public SparePartResponseDTO updateQuantity(Long id, SparePartQuantityUpdateDTO updateDTO) {
        SparePart part = sparePartRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Spare part not found with ID: " + id));

        String action = updateDTO.getAction().toUpperCase();
        int amount = updateDTO.getQuantity();
        int current = part.getQuantity();
        int newQuantity;

        switch (action) {
            case "ADD" -> newQuantity = current + amount;
            case "DEDUCT" -> {
                if (amount > current) {
                    throw new InvalidStockOperationException(
                            "Cannot deduct " + amount + " units. Current stock is only " + current + " units"
                    );
                }
                newQuantity = current - amount;
            }
            case "SET" -> newQuantity = amount;
            default -> throw new InvalidStockOperationException("Invalid action: " + action + ". Must be ADD, DEDUCT, or SET");
        }

        part.setQuantity(newQuantity);
        part.setStatus(resolveStockStatus(part.getStatus(), newQuantity, part.getReorderLevel()));

        SparePart updated = sparePartRepository.save(part);
        return mapToResponseDTO(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SparePartResponseDTO> getLowStockSpareParts() {
        return sparePartRepository.findLowStockSpareParts().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAllCategories() {
        return sparePartRepository.findDistinctCategories();
    }

    private String resolveStockStatus(String requestedStatus, int quantity, int reorderLevel) {
        if ("DISCONTINUED".equalsIgnoreCase(requestedStatus)) {
            return "DISCONTINUED";
        }
        if (quantity == 0) {
            return "OUT_OF_STOCK";
        }
        if (quantity <= reorderLevel) {
            return "LOW_STOCK";
        }
        return "AVAILABLE";
    }

    private SparePartResponseDTO mapToResponseDTO(SparePart part) {
        boolean isLow = part.isLowStock();
        String stockStatus;
        if (part.getQuantity() == 0) {
            stockStatus = "OUT OF STOCK";
        } else if (isLow) {
            stockStatus = "LOW STOCK";
        } else {
            stockStatus = "IN STOCK";
        }

        return new SparePartResponseDTO(
                part.getPartId(),
                part.getPartName(),
                part.getCategory(),
                part.getStatus(),
                part.getQuantity(),
                part.getUnitPrice(),
                part.getReorderLevel(),
                isLow,
                stockStatus
        );
    }
}