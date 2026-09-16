package com.sliit.vehicleservice.fuelinventory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sliit.vehicleservice.fuelinventory.controller.SparePartController;
import com.sliit.vehicleservice.fuelinventory.dto.SparePartQuantityUpdateDTO;
import com.sliit.vehicleservice.fuelinventory.dto.SparePartRequestDTO;
import com.sliit.vehicleservice.fuelinventory.dto.SparePartResponseDTO;
import com.sliit.vehicleservice.fuelinventory.exception.GlobalExceptionHandler;
import com.sliit.vehicleservice.fuelinventory.service.SparePartService;
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

@WebMvcTest(SparePartController.class)
@Import(GlobalExceptionHandler.class)
class SparePartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SparePartService sparePartService;

    @Test
    @DisplayName("POST /api/v1/spare-parts should return 201 CREATED for valid payload")
    void testCreateSparePart_Success() throws Exception {
        SparePartRequestDTO request = new SparePartRequestDTO(
                "Oil Filter", "Filters", "AVAILABLE", 20, new BigDecimal("2500.00"), 5
        );
        SparePartResponseDTO response = new SparePartResponseDTO(
                1L, "Oil Filter", "Filters", "AVAILABLE", 20, new BigDecimal("2500.00"), 5, false, "IN STOCK"
        );

        when(sparePartService.createSparePart(any(SparePartRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/spare-parts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.partName").value("Oil Filter"))
                .andExpect(jsonPath("$.data.quantity").value(20));
    }

    @Test
    @DisplayName("POST /api/v1/spare-parts should return 400 BAD REQUEST when partName is blank or quantity is negative")
    void testCreateSparePart_ValidationError() throws Exception {
        SparePartRequestDTO request = new SparePartRequestDTO(
                "", "Filters", "AVAILABLE", -5, new BigDecimal("2500.00"), 5
        );

        mockMvc.perform(post("/api/v1/spare-parts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.validationErrors.partName").exists())
                .andExpect(jsonPath("$.validationErrors.quantity").exists());
    }

    @Test
    @DisplayName("GET /api/v1/spare-parts should return 200 OK and list of parts")
    void testGetAllSpareParts_Success() throws Exception {
        SparePartResponseDTO part = new SparePartResponseDTO(
                1L, "Brake Pads", "Brakes", "AVAILABLE", 15, new BigDecimal("8500.00"), 5, false, "IN STOCK"
        );

        when(sparePartService.getAllSpareParts(null, null, null, null)).thenReturn(List.of(part));

        mockMvc.perform(get("/api/v1/spare-parts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].partName").value("Brake Pads"));
    }
}