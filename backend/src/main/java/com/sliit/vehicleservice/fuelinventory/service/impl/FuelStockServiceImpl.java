package com.sliit.vehicleservice.fuelinventory.service.impl;

import com.sliit.vehicleservice.fuelinventory.dto.FuelQuantityUpdateDTO;
import com.sliit.vehicleservice.fuelinventory.dto.FuelStockRequestDTO;
import com.sliit.vehicleservice.fuelinventory.dto.FuelStockResponseDTO;
import com.sliit.vehicleservice.fuelinventory.dto.PriceUpdateDTO;
import com.sliit.vehicleservice.fuelinventory.entity.FuelStock;
import com.sliit.vehicleservice.fuelinventory.exception.DuplicateResourceException;
import com.sliit.vehicleservice.fuelinventory.exception.InvalidStockOperationException;
import com.sliit.vehicleservice.fuelinventory.exception.ResourceNotFoundException;
import com.sliit.vehicleservice.fuelinventory.repository.FuelStockRepository;
import com.sliit.vehicleservice.fuelinventory.service.FuelStockService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class FuelStockServiceImpl implements FuelStockService {

    private final FuelStockRepository fuelStockRepository;

    public FuelStockServiceImpl(FuelStockRepository fuelStockRepository) {
        this.fuelStockRepository = fuelStockRepository;
    }

    @Override
    public FuelStockResponseDTO createFuelStock(FuelStockRequestDTO requestDTO) {
        if (fuelStockRepository.existsByTankNo(requestDTO.getTankNo().trim())) {
            throw new DuplicateResourceException("Tank number '" + requestDTO.getTankNo().trim() + "' is already registered");
        }

        if (requestDTO.getCurrentQuantity() > requestDTO.getTankCapacity()) {
            throw new InvalidStockOperationException(
                    "Current quantity (" + requestDTO.getCurrentQuantity() + " L) cannot exceed tank capacity (" + requestDTO.getTankCapacity() + " L)"
            );
        }

        FuelStock fuelStock = new FuelStock();
        fuelStock.setFuelType(requestDTO.getFuelType().trim());
        fuelStock.setTankNo(requestDTO.getTankNo().trim());
        fuelStock.setTankCapacity(requestDTO.getTankCapacity());
        fuelStock.setCurrentQuantity(requestDTO.getCurrentQuantity());
        fuelStock.setReorderLevel(requestDTO.getReorderLevel());
        fuelStock.setUnitPrice(requestDTO.getUnitPrice());

        FuelStock savedStock = fuelStockRepository.save(fuelStock);
        return mapToResponseDTO(savedStock);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FuelStockResponseDTO> getAllFuelStocks(String search, Boolean lowStockOnly, String tankNo) {
        List<FuelStock> stocks;

        if (Boolean.TRUE.equals(lowStockOnly)) {
            stocks = fuelStockRepository.findLowStockFuel();
        } else if (search != null && !search.trim().isEmpty()) {
            stocks = fuelStockRepository.searchFuelStocks(search.trim());
        } else if (tankNo != null && !tankNo.trim().isEmpty()) {
            stocks = fuelStockRepository.findByTankNoContainingIgnoreCase(tankNo.trim());
        } else {
            stocks = fuelStockRepository.findAll();
        }

        return stocks.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public FuelStockResponseDTO getFuelStockById(Long id) {
        FuelStock stock = fuelStockRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fuel stock not found with ID: " + id));
        return mapToResponseDTO(stock);
    }

    @Override
    public FuelStockResponseDTO updateFuelStock(Long id, FuelStockRequestDTO requestDTO) {
        FuelStock existingStock = fuelStockRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fuel stock not found with ID: " + id));

        if (fuelStockRepository.existsByTankNoAndFuelStockIdNot(requestDTO.getTankNo().trim(), id)) {
            throw new DuplicateResourceException("Tank number '" + requestDTO.getTankNo().trim() + "' is already assigned to another tank");
        }

        if (requestDTO.getCurrentQuantity() > requestDTO.getTankCapacity()) {
            throw new InvalidStockOperationException(
                    "Current quantity (" + requestDTO.getCurrentQuantity() + " L) cannot exceed tank capacity (" + requestDTO.getTankCapacity() + " L)"
            );
        }

        existingStock.setFuelType(requestDTO.getFuelType().trim());
        existingStock.setTankNo(requestDTO.getTankNo().trim());
        existingStock.setTankCapacity(requestDTO.getTankCapacity());
        existingStock.setCurrentQuantity(requestDTO.getCurrentQuantity());
        existingStock.setReorderLevel(requestDTO.getReorderLevel());
        existingStock.setUnitPrice(requestDTO.getUnitPrice());

        FuelStock updated = fuelStockRepository.save(existingStock);
        return mapToResponseDTO(updated);
    }

    @Override
    public void deleteFuelStock(Long id) {
        FuelStock stock = fuelStockRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fuel stock not found with ID: " + id));
        fuelStockRepository.delete(stock);
    }

    @Override
    public FuelStockResponseDTO updateQuantity(Long id, FuelQuantityUpdateDTO updateDTO) {
        FuelStock stock = fuelStockRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fuel stock not found with ID: " + id));

        String action = updateDTO.getAction().toUpperCase();
        double amount = updateDTO.getQuantity();
        double current = stock.getCurrentQuantity();
        double capacity = stock.getTankCapacity();
        double newQuantity;

        switch (action) {
            case "ADD" -> {
                if (current + amount > capacity) {
                    double availableSpace = capacity - current;
                    throw new InvalidStockOperationException(
                            "Adding " + amount + " L exceeds tank capacity. Maximum available space is " + availableSpace + " L"
                    );
                }
                newQuantity = current + amount;
            }
            case "DEDUCT" -> {
                if (amount > current) {
                    throw new InvalidStockOperationException(
                            "Cannot deduct " + amount + " L. Current stock is only " + current + " L"
                    );
                }
                newQuantity = current - amount;
            }
            case "SET" -> {
                if (amount > capacity) {
                    throw new InvalidStockOperationException(
                            "Cannot set quantity (" + amount + " L) greater than tank capacity (" + capacity + " L)"
                    );
                }
                newQuantity = amount;
            }
            default -> throw new InvalidStockOperationException("Invalid action: " + action + ". Must be ADD, DEDUCT, or SET");
        }

        stock.setCurrentQuantity(newQuantity);
        FuelStock updated = fuelStockRepository.save(stock);
        return mapToResponseDTO(updated);
    }

    @Override
    public FuelStockResponseDTO updatePrice(Long id, PriceUpdateDTO priceUpdateDTO) {
        FuelStock stock = fuelStockRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fuel stock not found with ID: " + id));

        stock.setUnitPrice(priceUpdateDTO.getUnitPrice());
        FuelStock updated = fuelStockRepository.save(stock);
        return mapToResponseDTO(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FuelStockResponseDTO> getLowStockFuelStocks() {
        return fuelStockRepository.findLowStockFuel().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    private FuelStockResponseDTO mapToResponseDTO(FuelStock stock) {
        boolean isLow = stock.isLowStock();
        String status;
        if (stock.getCurrentQuantity() <= 0) {
            status = "EMPTY";
        } else if (isLow) {
            status = "LOW STOCK";
        } else {
            status = "IN STOCK";
        }

        double percentage = 0.0;
        if (stock.getTankCapacity() > 0) {
            percentage = BigDecimal.valueOf((stock.getCurrentQuantity() / stock.getTankCapacity()) * 100.0)
                    .setScale(1, RoundingMode.HALF_UP)
                    .doubleValue();
        }

        return new FuelStockResponseDTO(
                stock.getFuelStockId(),
                stock.getFuelType(),
                stock.getTankNo(),
                stock.getTankCapacity(),
                stock.getCurrentQuantity(),
                stock.getReorderLevel(),
                stock.getUnitPrice(),
                isLow,
                status,
                percentage
        );
    }
}