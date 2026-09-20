package com.sliit.vsfms.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_items")
public class InventoryItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull @Enumerated(EnumType.STRING) private InventoryItemType itemType;
    @NotBlank private String name;
    @NotBlank @Column(nullable = false, unique = true) private String sku;
    @NotNull @DecimalMin("0.00") private BigDecimal quantity = BigDecimal.ZERO;
    @NotBlank private String unit;
    @NotNull @DecimalMin("0.00") private BigDecimal unitPrice = BigDecimal.ZERO;
    @NotNull @DecimalMin("0.00") private BigDecimal reorderLevel = BigDecimal.ZERO;
    private LocalDateTime lastUpdated;

    @PrePersist @PreUpdate
    void update() { lastUpdated = LocalDateTime.now(); if (sku != null) sku = sku.trim().toUpperCase(); }
    @Transient public boolean isLowStock() { return quantity != null && reorderLevel != null && quantity.compareTo(reorderLevel) <= 0; }
    public Long getId(){return id;} public void setId(Long v){id=v;}
    public InventoryItemType getItemType(){return itemType;} public void setItemType(InventoryItemType v){itemType=v;}
    public String getName(){return name;} public void setName(String v){name=v;}
    public String getSku(){return sku;} public void setSku(String v){sku=v;}
    public BigDecimal getQuantity(){return quantity;} public void setQuantity(BigDecimal v){quantity=v;}
    public String getUnit(){return unit;} public void setUnit(String v){unit=v;}
    public BigDecimal getUnitPrice(){return unitPrice;} public void setUnitPrice(BigDecimal v){unitPrice=v;}
    public BigDecimal getReorderLevel(){return reorderLevel;} public void setReorderLevel(BigDecimal v){reorderLevel=v;}
    public LocalDateTime getLastUpdated(){return lastUpdated;} public void setLastUpdated(LocalDateTime v){lastUpdated=v;}
}
