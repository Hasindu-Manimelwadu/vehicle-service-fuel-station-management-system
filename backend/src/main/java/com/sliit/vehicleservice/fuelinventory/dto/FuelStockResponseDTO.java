package com.sliit.vehicleservice.fuelinventory.dto;

import java.math.BigDecimal;

public class FuelStockResponseDTO {

    private Long fuelStockId;
    private String fuelType;
    private String tankNo;
    private Double tankCapacity;
    private Double currentQuantity;
    private Double reorderLevel;
    private BigDecimal unitPrice;
    private boolean lowStock;
    private String status;
    private Double percentageFilled;

    public FuelStockResponseDTO() {}

    public FuelStockResponseDTO(Long fuelStockId, String fuelType, String tankNo, Double tankCapacity,
                                Double currentQuantity, Double reorderLevel, BigDecimal unitPrice,
                                boolean lowStock, String status, Double percentageFilled) {
        this.fuelStockId = fuelStockId;
        this.fuelType = fuelType;
        this.tankNo = tankNo;
        this.tankCapacity = tankCapacity;
        this.currentQuantity = currentQuantity;
        this.reorderLevel = reorderLevel;
        this.unitPrice = unitPrice;
        this.lowStock = lowStock;
        this.status = status;
        this.percentageFilled = percentageFilled;
    }

    public Long getFuelStockId() { return fuelStockId; }
    public void setFuelStockId(Long fuelStockId) { this.fuelStockId = fuelStockId; }

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

    public boolean isLowStock() { return lowStock; }
    public void setLowStock(boolean lowStock) { this.lowStock = lowStock; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Double getPercentageFilled() { return percentageFilled; }
    public void setPercentageFilled(Double percentageFilled) { this.percentageFilled = percentageFilled; }
}