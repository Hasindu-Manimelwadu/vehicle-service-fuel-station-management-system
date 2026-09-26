package com.sliit.vehicleservice.fuelinventory;

import com.sliit.vehicleservice.fuelinventory.dto.SparePartQuantityUpdateDTO;
import com.sliit.vehicleservice.fuelinventory.dto.SparePartRequestDTO;
import com.sliit.vehicleservice.fuelinventory.dto.SparePartResponseDTO;
import com.sliit.vehicleservice.fuelinventory.entity.SparePart;
import com.sliit.vehicleservice.fuelinventory.exception.InvalidStockOperationException;
import com.sliit.vehicleservice.fuelinventory.exception.ResourceNotFoundException;
import com.sliit.vehicleservice.fuelinventory.repository.SparePartRepository;
import com.sliit.vehicleservice.fuelinventory.service.impl.SparePartServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SparePartServiceTest {

    @Mock
    private SparePartRepository sparePartRepository;

    @InjectMocks
    private SparePartServiceImpl sparePartService;

    private SparePart samplePart;
    private SparePartRequestDTO sampleRequestDTO;

    @BeforeEach
    void setUp() {
        samplePart = new SparePart(1L, "Oil Filter Toyota", "Filters", "AVAILABLE", 25, new BigDecimal("2500.00"), 10);
        sampleRequestDTO = new SparePartRequestDTO("Oil Filter Toyota", "Filters", "AVAILABLE", 25, new BigDecimal("2500.00"), 10);
    }

    @Test
    @DisplayName("Should successfully create a spare part")
    void testCreateSparePart_Success() {
        when(sparePartRepository.save(any(SparePart.class))).thenReturn(samplePart);

        SparePartResponseDTO response = sparePartService.createSparePart(sampleRequestDTO);

        assertNotNull(response);
        assertEquals("Oil Filter Toyota", response.getPartName());
        assertEquals(25, response.getQuantity());
        assertFalse(response.isLowStock());
        assertEquals("IN STOCK", response.getStockStatus());
    }

    @Test
    @DisplayName("Should detect LOW STOCK when quantity <= reorderLevel")
    void testLowStockDetection() {
        SparePart lowStockPart = new SparePart(2L, "Brake Pads", "Brakes", "AVAILABLE", 5, new BigDecimal("8500.00"), 10);
        assertTrue(lowStockPart.isLowStock());

        when(sparePartRepository.findById(2L)).thenReturn(Optional.of(lowStockPart));
        SparePartResponseDTO response = sparePartService.getSparePartById(2L);

        assertTrue(response.isLowStock());
        assertEquals("LOW STOCK", response.getStockStatus());
    }

    @Test
    @DisplayName("Should successfully deduct quantity and update status to LOW STOCK if under threshold")
    void testUpdateQuantity_DeductToLowStock() {
        when(sparePartRepository.findById(1L)).thenReturn(Optional.of(samplePart));
        when(sparePartRepository.save(any(SparePart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Deduct 18 units from 25: remainder is 7, which is <= reorderLevel (10)
        SparePartQuantityUpdateDTO updateDTO = new SparePartQuantityUpdateDTO("DEDUCT", 18);
        SparePartResponseDTO response = sparePartService.updateQuantity(1L, updateDTO);

        assertEquals(7, response.getQuantity());
        assertTrue(response.isLowStock());
        assertEquals("LOW STOCK", response.getStockStatus());
    }

    @Test
    @DisplayName("Should throw exception when deducting more units than available")
    void testUpdateQuantity_DeductMoreThanAvailable() {
        when(sparePartRepository.findById(1L)).thenReturn(Optional.of(samplePart));

        SparePartQuantityUpdateDTO updateDTO = new SparePartQuantityUpdateDTO("DEDUCT", 30);
        assertThrows(InvalidStockOperationException.class, () -> sparePartService.updateQuantity(1L, updateDTO));
    }
}