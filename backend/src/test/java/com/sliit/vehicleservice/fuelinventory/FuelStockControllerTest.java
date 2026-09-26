package com.sliit.vehicleservice.fuelinventory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sliit.vehicleservice.fuelinventory.controller.FuelStockController;
import com.sliit.vehicleservice.fuelinventory.dto.FuelStockRequestDTO;
import com.sliit.vehicleservice.fuelinventory.dto.FuelStockResponseDTO;
import com.sliit.vehicleservice.fuelinventory.exception.GlobalExceptionHandler;
import com.sliit.vehicleservice.fuelinventory.service.FuelStockService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FuelStockController.class)
@Import(GlobalExceptionHandler.class)
class FuelStockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FuelStockService fuelStockService;

    @Test
    @DisplayName("POST /api/v1/fuel-stocks should return 201 CREATED for valid payload")
    void testCreateFuelStock_Success() throws Exception {
        FuelStockRequestDTO request = new FuelStockRequestDTO(
                "Petrol Octane 92", "Tank-01", 30000.0, 15000.0, 5000.0, new BigDecimal("368.00")
        );
        FuelStockResponseDTO response = new FuelStockResponseDTO(
                1L, "Petrol Octane 92", "Tank-01", 30000.0, 15000.0, 5000.0, new BigDecimal("368.00"),
                false, "IN STOCK", 50.0
        );

        when(fuelStockService.createFuelStock(any(FuelStockRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/fuel-stocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.tankNo").value("Tank-01"))
                .andExpect(jsonPath("$.data.fuelType").value("Petrol Octane 92"));
    }

    @Test
    @DisplayName("POST /api/v1/fuel-stocks should return 400 BAD REQUEST when fuelType is blank")
    void testCreateFuelStock_ValidationError_BlankFuelType() throws Exception {
        FuelStockRequestDTO request = new FuelStockRequestDTO(
                "", "Tank-01", 30000.0, 15000.0, 5000.0, new BigDecimal("368.00")
        );

        mockMvc.perform(post("/api/v1/fuel-stocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.validationErrors.fuelType").exists());
    }

    @Test
    @DisplayName("GET /api/v1/fuel-stocks should return 200 OK and list of stocks")
    void testGetAllFuelStocks_Success() throws Exception {
        FuelStockResponseDTO stock1 = new FuelStockResponseDTO(
                1L, "Petrol Octane 92", "Tank-01", 30000.0, 15000.0, 5000.0, new BigDecimal("368.00"),
                false, "IN STOCK", 50.0
        );

        when(fuelStockService.getAllFuelStocks(null, null, null)).thenReturn(List.of(stock1));

        mockMvc.perform(get("/api/v1/fuel-stocks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].tankNo").value("Tank-01"));
    }
}