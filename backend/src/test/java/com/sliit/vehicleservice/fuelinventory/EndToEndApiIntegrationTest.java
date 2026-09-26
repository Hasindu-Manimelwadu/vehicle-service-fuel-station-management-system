package com.sliit.vehicleservice.fuelinventory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EndToEndApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static Long createdFuelStockId;
    private static Long createdSparePartId;

    @Test
    @Order(1)
    @DisplayName("1. POST /api/v1/fuel-stocks - Create FuelStock")
    void test1_CreateFuelStock() throws Exception {
        String requestJson = """
                {
                    "fuelType": "Petrol Octane 92",
                    "tankNo": "Tank-01",
                    "tankCapacity": 30000.0,
                    "currentQuantity": 15000.0,
                    "reorderLevel": 5000.0,
                    "unitPrice": 368.00
                }
                """;

        MvcResult result = mockMvc.perform(post("/api/v1/fuel-stocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.fuelStockId").isNumber())
                .andExpect(jsonPath("$.data.fuelType").value("Petrol Octane 92"))
                .andExpect(jsonPath("$.data.tankNo").value("Tank-01"))
                .andExpect(jsonPath("$.data.status").value("IN STOCK"))
                .andExpect(jsonPath("$.data.percentageFilled").value(50.0))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        JsonNode root = objectMapper.readTree(responseBody);
        createdFuelStockId = root.path("data").path("fuelStockId").asLong();
        System.out.println(">>> 1. Create FuelStock Response:\n" + responseBody);
        assertTrue(createdFuelStockId > 0);
    }

    @Test
    @Order(2)
    @DisplayName("2. GET /api/v1/fuel-stocks/{id} - Get FuelStock by ID")
    void test2_GetFuelStockById() throws Exception {
        mockMvc.perform(get("/api/v1/fuel-stocks/" + createdFuelStockId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.fuelStockId").value(createdFuelStockId))
                .andExpect(jsonPath("$.data.tankNo").value("Tank-01"));
    }

    @Test
    @Order(3)
    @DisplayName("3. PUT /api/v1/fuel-stocks/{id} - Update FuelStock")
    void test3_UpdateFuelStock() throws Exception {
        String updateJson = """
                {
                    "fuelType": "Petrol Octane 92 Premium",
                    "tankNo": "Tank-01",
                    "tankCapacity": 30000.0,
                    "currentQuantity": 18000.0,
                    "reorderLevel": 5000.0,
                    "unitPrice": 375.00
                }
                """;

        MvcResult result = mockMvc.perform(put("/api/v1/fuel-stocks/" + createdFuelStockId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.fuelType").value("Petrol Octane 92 Premium"))
                .andExpect(jsonPath("$.data.currentQuantity").value(18000.0))
                .andExpect(jsonPath("$.data.unitPrice").value(375.00))
                .andReturn();

        System.out.println(">>> 3. Update FuelStock Response:\n" + result.getResponse().getContentAsString());
    }

    @Test
    @Order(4)
    @DisplayName("4. PATCH /api/v1/fuel-stocks/{id}/quantity - Update Quantity")
    void test4_UpdateFuelQuantity() throws Exception {
        String patchJson = """
                {
                    "action": "DEDUCT",
                    "quantity": 14000.0
                }
                """;
        // 18000 - 14000 = 4000.0 (which is <= reorderLevel 5000 -> LOW STOCK!)
        MvcResult result = mockMvc.perform(patch("/api/v1/fuel-stocks/" + createdFuelStockId + "/quantity")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.currentQuantity").value(4000.0))
                .andExpect(jsonPath("$.data.lowStock").value(true))
                .andExpect(jsonPath("$.data.status").value("LOW STOCK"))
                .andReturn();

        System.out.println(">>> 4. Fuel Quantity Update (Triggered Low Stock) Response:\n" + result.getResponse().getContentAsString());
    }

    @Test
    @Order(5)
    @DisplayName("5. GET /api/v1/fuel-stocks/low-stock - Query Low Stock Fuel")
    void test5_GetLowStockFuel() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/v1/fuel-stocks/low-stock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].fuelStockId").value(createdFuelStockId))
                .andExpect(jsonPath("$.data[0].lowStock").value(true))
                .andReturn();

        System.out.println(">>> 5. Low Stock Fuel Query Response:\n" + result.getResponse().getContentAsString());
    }

    @Test
    @Order(6)
    @DisplayName("6. POST /api/v1/spare-parts - Create SparePart")
    void test6_CreateSparePart() throws Exception {
        String requestJson = """
                {
                    "partName": "Brembo Front Brake Pads",
                    "category": "Brakes",
                    "status": "AVAILABLE",
                    "quantity": 25,
                    "unitPrice": 14500.00,
                    "reorderLevel": 10
                }
                """;

        MvcResult result = mockMvc.perform(post("/api/v1/spare-parts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.partId").isNumber())
                .andExpect(jsonPath("$.data.partName").value("Brembo Front Brake Pads"))
                .andExpect(jsonPath("$.data.category").value("Brakes"))
                .andExpect(jsonPath("$.data.quantity").value(25))
                .andExpect(jsonPath("$.data.lowStock").value(false))
                .andExpect(jsonPath("$.data.stockStatus").value("IN STOCK"))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        JsonNode root = objectMapper.readTree(responseBody);
        createdSparePartId = root.path("data").path("partId").asLong();
        System.out.println(">>> 6. Create SparePart Response:\n" + responseBody);
        assertTrue(createdSparePartId > 0);
    }

    @Test
    @Order(7)
    @DisplayName("7. PUT /api/v1/spare-parts/{id} - Update SparePart")
    void test7_UpdateSparePart() throws Exception {
        String updateJson = """
                {
                    "partName": "Brembo Front Brake Pads (Ceramic)",
                    "category": "Brakes",
                    "status": "AVAILABLE",
                    "quantity": 25,
                    "unitPrice": 15800.00,
                    "reorderLevel": 10
                }
                """;

        MvcResult result = mockMvc.perform(put("/api/v1/spare-parts/" + createdSparePartId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.partName").value("Brembo Front Brake Pads (Ceramic)"))
                .andExpect(jsonPath("$.data.unitPrice").value(15800.00))
                .andReturn();

        System.out.println(">>> 7. Update SparePart Response:\n" + result.getResponse().getContentAsString());
    }

    @Test
    @Order(8)
    @DisplayName("8. PATCH /api/v1/spare-parts/{id}/quantity - Deduct to Low Stock")
    void test8_DeductSparePartQuantity() throws Exception {
        String patchJson = """
                {
                    "action": "DEDUCT",
                    "quantity": 18
                }
                """;
        // 25 - 18 = 7 (which is <= reorderLevel 10 -> LOW STOCK!)
        MvcResult result = mockMvc.perform(patch("/api/v1/spare-parts/" + createdSparePartId + "/quantity")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.quantity").value(7))
                .andExpect(jsonPath("$.data.lowStock").value(true))
                .andExpect(jsonPath("$.data.stockStatus").value("LOW STOCK"))
                .andReturn();

        System.out.println(">>> 8. SparePart Quantity Deduct (Triggered Low Stock) Response:\n" + result.getResponse().getContentAsString());
    }

    @Test
    @Order(9)
    @DisplayName("9. GET /api/v1/spare-parts/low-stock - Query Low Stock Spare Parts")
    void test9_GetLowStockSpareParts() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/v1/spare-parts/low-stock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].partId").value(createdSparePartId))
                .andExpect(jsonPath("$.data[0].lowStock").value(true))
                .andReturn();

        System.out.println(">>> 9. Low Stock Spare Parts Query Response:\n" + result.getResponse().getContentAsString());
    }

    @Test
    @Order(10)
    @DisplayName("10. Validation Failure: POST /api/v1/fuel-stocks with invalid data")
    void test10_ValidationFailure_FuelStock() throws Exception {
        String invalidJson = """
                {
                    "fuelType": "",
                    "tankNo": "",
                    "tankCapacity": -500.0,
                    "currentQuantity": -10.0,
                    "reorderLevel": -5.0,
                    "unitPrice": -100.00
                }
                """;

        MvcResult result = mockMvc.perform(post("/api/v1/fuel-stocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.validationErrors.fuelType").exists())
                .andExpect(jsonPath("$.validationErrors.tankNo").exists())
                .andExpect(jsonPath("$.validationErrors.tankCapacity").exists())
                .andExpect(jsonPath("$.validationErrors.currentQuantity").exists())
                .andExpect(jsonPath("$.validationErrors.unitPrice").exists())
                .andReturn();

        System.out.println(">>> 10. Validation Failure Error Response:\n" + result.getResponse().getContentAsString());
    }

    @Test
    @Order(11)
    @DisplayName("11. Not Found Error: GET /api/v1/fuel-stocks/99999")
    void test11_NotFound_FuelStock() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/v1/fuel-stocks/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Fuel stock not found with ID: 99999"))
                .andReturn();

        System.out.println(">>> 11. Not Found Error Response:\n" + result.getResponse().getContentAsString());
    }

    @Test
    @Order(12)
    @DisplayName("12. DELETE /api/v1/fuel-stocks/{id} - Delete FuelStock")
    void test12_DeleteFuelStock() throws Exception {
        MvcResult result = mockMvc.perform(delete("/api/v1/fuel-stocks/" + createdFuelStockId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Fuel stock deleted successfully"))
                .andReturn();

        System.out.println(">>> 12. Delete FuelStock Response:\n" + result.getResponse().getContentAsString());

        // Verify it is truly gone
        mockMvc.perform(get("/api/v1/fuel-stocks/" + createdFuelStockId))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(13)
    @DisplayName("13. DELETE /api/v1/spare-parts/{id} - Delete SparePart")
    void test13_DeleteSparePart() throws Exception {
        MvcResult result = mockMvc.perform(delete("/api/v1/spare-parts/" + createdSparePartId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Spare part removed successfully"))
                .andReturn();

        System.out.println(">>> 13. Delete SparePart Response:\n" + result.getResponse().getContentAsString());

        // Verify it is truly gone
        mockMvc.perform(get("/api/v1/spare-parts/" + createdSparePartId))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(14)
    @DisplayName("14. GET /api/v1/inventory/dashboard-summary - Dashboard KPIs")
    void test14_DashboardSummary() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/v1/inventory/dashboard-summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalFuelTypes").isNumber())
                .andExpect(jsonPath("$.data.totalSpareParts").isNumber())
                .andReturn();

        System.out.println(">>> 14. Dashboard Summary Response:\n" + result.getResponse().getContentAsString());
    }
}