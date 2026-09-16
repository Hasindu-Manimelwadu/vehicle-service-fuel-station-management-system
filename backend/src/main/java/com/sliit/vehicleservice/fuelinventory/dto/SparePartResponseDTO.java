package com.sliit.vehicleservice.fuelinventory.dto;

import java.math.BigDecimal;

public class SparePartResponseDTO {

    private Long partId;
    private String partName;
    private String category;
    private String status;
    private Integer quantity;
    private BigDecimal unitPrice;
    private Integer reorderLevel;
    private boolean lowStock;
    private String stockStatus;

    public SparePartResponseDTO() {}

    public SparePartResponseDTO(Long partId, String partName, String category, String status,
                                Integer quantity, BigDecimal unitPrice, Integer reorderLevel,
                                boolean lowStock, String stockStatus) {
        this.partId = partId;
        this.partName = partName;
        this.category = category;
        this.status = status;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.reorderLevel = reorderLevel;
        this.lowStock = lowStock;
        this.stockStatus = stockStatus;
    }

    public Long getPartId() { return partId; }
    public void setPartId(Long partId) { this.partId = partId; }

    public String getPartName() { return partName; }
    public void setPartName(String partName) { this.partName = partName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public Integer getReorderLevel() { return reorderLevel; }
    public void setReorderLevel(Integer reorderLevel) { this.reorderLevel = reorderLevel; }

    public boolean isLowStock() { return lowStock; }
    public void setLowStock(boolean lowStock) { this.lowStock = lowStock; }

    public String getStockStatus() { return stockStatus; }
    public void setStockStatus(String stockStatus) { this.stockStatus = stockStatus; }
}