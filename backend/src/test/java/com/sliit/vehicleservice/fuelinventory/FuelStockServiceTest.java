package com.sliit.vehicleservice.fuelinventory;

import com.sliit.vehicleservice.fuelinventory.dto.FuelQuantityUpdateDTO;
import com.sliit.vehicleservice.fuelinventory.dto.FuelStockRequestDTO;
import com.sliit.vehicleservice.fuelinventory.dto.FuelStockResponseDTO;
import com.sliit.vehicleservice.fuelinventory.dto.PriceUpdateDTO;
import com.sliit.vehicleservice.fuelinventory.entity.FuelStock;
import com.sliit.vehicleservice.fuelinventory.exception.DuplicateResourceException;
import com.sliit.vehicleservice.fuelinventory.exception.InvalidStockOperationException;
import com.sliit.vehicleservice.fuelinventory.exception.ResourceNotFoundException;
import com.sliit.vehicleservice.fuelinventory.repository.FuelStockRepository;
import com.sliit.vehicleservice.fuelinventory.service.impl.FuelStockServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FuelStockServiceTest {

    @Mock
    private FuelStockRepository fuelStockRepository;

    @InjectMocks
    private FuelStockServiceImpl fuelStockService;

    private FuelStock sampleFuelStock;
    private FuelStockRequestDTO sampleRequestDTO;

    @BeforeEach
    void setUp() {
        sampleFuelStock = new FuelStock(1L, "Petrol Octane 92", "Tank-01", 30000.0, 15000.0, 5000.0, new BigDecimal("368.00"));
        sampleRequestDTO = new FuelStockRequestDTO("Petrol Octane 92", "Tank-01", 30000.0, 15000.0, 5000.0, new BigDecimal("368.00"));
    }

    @Test
    @DisplayName("Should successfully create a new fuel stock")
    void testCreateFuelStock_Success() {
        when(fuelStockRepository.existsByTankNo("Tank-01")).thenReturn(false);
        when(fuelStockRepository.save(any(FuelStock.class))).thenReturn(sampleFuelStock);

        FuelStockResponseDTO response = fuelStockService.createFuelStock(sampleRequestDTO);

        assertNotNull(response);
        assertEquals("Petrol Octane 92", response.getFuelType());
        assertEquals("Tank-01", response.getTankNo());
        assertEquals(15000.0, response.getCurrentQuantity());
        assertFalse(response.isLowStock());
        assertEquals("IN STOCK", response.getStatus());
        verify(fuelStockRepository, times(1)).save(any(FuelStock.class));
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when tank number already exists")
    void testCreateFuelStock_DuplicateTankNo() {
        when(fuelStockRepository.existsByTankNo("Tank-01")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> fuelStockService.createFuelStock(sampleRequestDTO));
        verify(fuelStockRepository, never()).save(any(FuelStock.class));
    }

    @Test
    @DisplayName("Should throw InvalidStockOperationException when current quantity exceeds capacity")
    void testCreateFuelStock_QuantityExceedsCapacity() {
        sampleRequestDTO.setCurrentQuantity(35000.0); // Capacity is 30000.0
        when(fuelStockRepository.existsByTankNo("Tank-01")).thenReturn(false);

        assertThrows(InvalidStockOperationException.class, () -> fuelStockService.createFuelStock(sampleRequestDTO));
    }

    @Test
    @DisplayName("Should correctly identify LOW STOCK condition when currentQuantity <= reorderLevel")
    void testLowStockCondition() {
        FuelStock lowStock = new FuelStock(2L, "Auto Diesel", "Tank-02", 20000.0, 3000.0, 4000.0, new BigDecimal("340.00"));
        assertTrue(lowStock.isLowStock());

        when(fuelStockRepository.findById(2L)).thenReturn(Optional.of(lowStock));
        FuelStockResponseDTO response = fuelStockService.getFuelStockById(2L);

        assertTrue(response.isLowStock());
        assertEquals("LOW STOCK", response.getStatus());
    }

    @Test
    @DisplayName("Should successfully add fuel quantity")
    void testUpdateQuantity_Add() {
        when(fuelStockRepository.findById(1L)).thenReturn(Optional.of(sampleFuelStock));
        when(fuelStockRepository.save(any(FuelStock.class))).thenAnswer(invocation -> invocation.getArgument(0));

        FuelQuantityUpdateDTO updateDTO = new FuelQuantityUpdateDTO("ADD", 5000.0);
        FuelStockResponseDTO response = fuelStockService.updateQuantity(1L, updateDTO);

        assertEquals(20000.0, response.getCurrentQuantity());
    }

    @Test
    @DisplayName("Should throw exception when adding fuel exceeds tank capacity")
    void testUpdateQuantity_AddExceedsCapacity() {
        when(fuelStockRepository.findById(1L)).thenReturn(Optional.of(sampleFuelStock));

        FuelQuantityUpdateDTO updateDTO = new FuelQuantityUpdateDTO("ADD", 25000.0); // 15000 + 25000 > 30000
        assertThrows(InvalidStockOperationException.class, () -> fuelStockService.updateQuantity(1L, updateDTO));
    }

    @Test
    @DisplayName("Should successfully deduct fuel quantity")
    void testUpdateQuantity_Deduct() {
        when(fuelStockRepository.findById(1L)).thenReturn(Optional.of(sampleFuelStock));
        when(fuelStockRepository.save(any(FuelStock.class))).thenAnswer(invocation -> invocation.getArgument(0));

        FuelQuantityUpdateDTO updateDTO = new FuelQuantityUpdateDTO("DEDUCT", 5000.0);
        FuelStockResponseDTO response = fuelStockService.updateQuantity(1L, updateDTO);

        assertEquals(10000.0, response.getCurrentQuantity());
    }

    @Test
    @DisplayName("Should throw exception when deducting more fuel than current quantity")
    void testUpdateQuantity_DeductExceedsCurrent() {
        when(fuelStockRepository.findById(1L)).thenReturn(Optional.of(sampleFuelStock));

        FuelQuantityUpdateDTO updateDTO = new FuelQuantityUpdateDTO("DEDUCT", 20000.0); // current is only 15000
        assertThrows(InvalidStockOperationException.class, () -> fuelStockService.updateQuantity(1L, updateDTO));
    }

    @Test
    @DisplayName("Should successfully update fuel unit price")
    void testUpdatePrice() {
        when(fuelStockRepository.findById(1L)).thenReturn(Optional.of(sampleFuelStock));
        when(fuelStockRepository.save(any(FuelStock.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PriceUpdateDTO priceDTO = new PriceUpdateDTO(new BigDecimal("395.50"));
        FuelStockResponseDTO response = fuelStockService.updatePrice(1L, priceDTO);

        assertEquals(new BigDecimal("395.50"), response.getUnitPrice());
    }

    @Test
    @DisplayName("Should delete fuel stock successfully")
    void testDeleteFuelStock_Success() {
        when(fuelStockRepository.findById(1L)).thenReturn(Optional.of(sampleFuelStock));

        assertDoesNotThrow(() -> fuelStockService.deleteFuelStock(1L));
        verify(fuelStockRepository, times(1)).delete(sampleFuelStock);
    }
}