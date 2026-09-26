package com.sliit.vehicleservice.fuelinventory.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;


@Entity
@Table(name = "fuel_stock")
public class FuelStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fuel_stock_id")
    private Long fuelStockId;

    @NotBlank(message = "Fuel type is required and cannot be blank")
    @Column(name = "fuel_type", nullable = false, length = 100)
    private String fuelType;

    @NotBlank(message = "Tank number is required and cannot be blank")
    @Column(name = "tank_no", nullable = false, unique = true, length = 50)
    private String tankNo;

    @NotNull(message = "Tank capacity is required")
    @Positive(message = "Tank capacity must be greater than 0")
    @Column(name = "tank_capacity", nullable = false)
    private Double tankCapacity;

    @NotNull(message = "Current quantity is required")
    @PositiveOrZero(message = "Current quantity cannot be negative")
    @Column(name = "current_quantity", nullable = false)
    private Double currentQuantity;

    @NotNull(message = "Reorder level is required")
    @PositiveOrZero(message = "Reorder level cannot be negative")
    @Column(name = "reorder_level", nullable = false)
    private Double reorderLevel;

    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.00", inclusive = true, message = "Unit price cannot be negative")
    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    public FuelStock() {
    }

    public FuelStock(Long fuelStockId, String fuelType, String tankNo, Double tankCapacity,
                     Double currentQuantity, Double reorderLevel, BigDecimal unitPrice) {
        this.fuelStockId = fuelStockId;
        this.fuelType = fuelType;
        this.tankNo = tankNo;
        this.tankCapacity = tankCapacity;
        this.currentQuantity = currentQuantity;
        this.reorderLevel = reorderLevel;
        this.unitPrice = unitPrice;
    }


    public boolean isLowStock() {
        return currentQuantity != null && reorderLevel != null && currentQuantity <= reorderLevel;
    }

    public Long getFuelStockId() {
        return fuelStockId;
    }

    public void setFuelStockId(Long fuelStockId) {
        this.fuelStockId = fuelStockId;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public String getTankNo() {
        return tankNo;
    }

    public void setTankNo(String tankNo) {
        this.tankNo = tankNo;
    }

    public Double getTankCapacity() {
        return tankCapacity;
    }

    public void setTankCapacity(Double tankCapacity) {
        this.tankCapacity = tankCapacity;
    }

    public Double getCurrentQuantity() {
        return currentQuantity;
    }

    public void setCurrentQuantity(Double currentQuantity) {
        this.currentQuantity = currentQuantity;
    }

    public Double getReorderLevel() {
        return reorderLevel;
    }

    public void setReorderLevel(Double reorderLevel) {
        this.reorderLevel = reorderLevel;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    @Override
    public String toString() {
        return "FuelStock{" +
                "fuelStockId=" + fuelStockId +
                ", fuelType='" + fuelType + '\'' +
                ", tankNo='" + tankNo + '\'' +
                ", tankCapacity=" + tankCapacity +
                ", currentQuantity=" + currentQuantity +
                ", reorderLevel=" + reorderLevel +
                ", unitPrice=" + unitPrice +
                '}';
    }
}