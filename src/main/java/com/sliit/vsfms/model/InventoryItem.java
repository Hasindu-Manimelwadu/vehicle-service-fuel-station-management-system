package com.sliit.vsfms.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "inventory_items")
public class InventoryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String itemName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InventoryItemType itemType;

    @Column(nullable = false)
    private BigDecimal unitPrice;

    @Column(nullable = false)
    private Double availableQuantity;

    private Double reorderLevel;

    public InventoryItem() {
    }

    public InventoryItem(String itemName,
                         InventoryItemType itemType,
                         BigDecimal unitPrice,
                         Double availableQuantity,
                         Double reorderLevel) {

        this.itemName = itemName;
        this.itemType = itemType;
        this.unitPrice = unitPrice;
        this.availableQuantity = availableQuantity;
        this.reorderLevel = reorderLevel;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public InventoryItemType getItemType() {
        return itemType;
    }

    public void setItemType(InventoryItemType itemType) {
        this.itemType = itemType;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Double getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(Double availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public Double getReorderLevel() {
        return reorderLevel;
    }

    public void setReorderLevel(Double reorderLevel) {
        this.reorderLevel = reorderLevel;
    }
}