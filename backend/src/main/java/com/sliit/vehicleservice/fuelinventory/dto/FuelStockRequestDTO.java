package com.sliit.vehicleservice.fuelinventory.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public class FuelStockRequestDTO {

    @NotBlank(message = "Fuel type is required and cannot be blank")
    private String fuelType;

    @NotBlank(message = "Tank number is required and cannot be blank")
    private String tankNo;

    @NotNull(message = "Tank capacity is required")
    @Positive(message = "Tank capacity must be greater than 0")
    private Double tankCapacity;

    @NotNull(message = "Current quantity is required")
    @PositiveOrZero(message = "Current quantity cannot be negative")
    private Double currentQuantity;

    @NotNull(message = "Reorder level is required")
    @PositiveOrZero(message = "Reorder level cannot be negative")
    private Double reorderLevel;

    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.00", inclusive = true, message = "Unit price cannot be negative")
    private BigDecimal unitPrice;

    public FuelStockRequestDTO() {}

    public FuelStockRequestDTO(String fuelType, String tankNo, Double tankCapacity,
                               Double currentQuantity, Double reorderLevel, BigDecimal unitPrice) {
        this.fuelType = fuelType;
        this.tankNo = tankNo;
        this.tankCapacity = tankCapacity;
        this.currentQuantity = currentQuantity;
        this.reorderLevel = reorderLevel;
        this.unitPrice = unitPrice;
    }

    public String getFuelType() { return fuelType; }
    public void setFuelType(String fuelType) { this.fuelType = fuelType; }

    public String getTankNo() { return tankNo; }
    public void setTankNo(String tankNo) { this.tankNo = tankNo; }

    public Double getTankCapacity() { return tankCapacity; }
    public void setTankCapacity(Double tankCapacity) { this.tankCapacity = tankCapacity; }

    public Double getCurrentQuantity() { return currentQuantity; }
    public void setCurrentQuantity(Double currentQuantity) { this.currentQuantity = currentQuantity; }

    public Double getReorderLevel() { return reorderLevel; }
    public void setReorderLevel(Double reorderLevel) { this.reorderLevel = reorderLevel; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
}