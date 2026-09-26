package com.sliit.vehicleservice.fuelinventory.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;


@Entity
@Table(name = "spare_part")
public class SparePart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "part_id")
    private Long partId;

    @NotBlank(message = "Part name is required and cannot be blank")
    @Column(name = "part_name", nullable = false, length = 150)
    private String partName;

    @NotBlank(message = "Category is required and cannot be blank")
    @Column(name = "category", nullable = false, length = 100)
    private String category;

    @NotBlank(message = "Status is required and must be a valid value")
    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @NotNull(message = "Quantity is required")
    @PositiveOrZero(message = "Quantity cannot be negative")
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.00", inclusive = true, message = "Unit price cannot be negative")
    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @NotNull(message = "Reorder level is required")
    @PositiveOrZero(message = "Reorder level cannot be negative")
    @Column(name = "reorder_level", nullable = false)
    private Integer reorderLevel;

    public SparePart() {
    }

    public SparePart(Long partId, String partName, String category, String status,
                     Integer quantity, BigDecimal unitPrice, Integer reorderLevel) {
        this.partId = partId;
        this.partName = partName;
        this.category = category;
        this.status = status;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.reorderLevel = reorderLevel;
    }


    public boolean isLowStock() {
        return quantity != null && reorderLevel != null && quantity <= reorderLevel;
    }

    public Long getPartId() {
        return partId;
    }

    public void setPartId(Long partId) {
        this.partId = partId;
    }

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Integer getReorderLevel() {
        return reorderLevel;
    }

    public void setReorderLevel(Integer reorderLevel) {
        this.reorderLevel = reorderLevel;
    }

    @Override
    public String toString() {
        return "SparePart{" +
                "partId=" + partId +
                ", partName='" + partName + '\'' +
                ", category='" + category + '\'' +
                ", status='" + status + '\'' +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", reorderLevel=" + reorderLevel +
                '}';
    }
}