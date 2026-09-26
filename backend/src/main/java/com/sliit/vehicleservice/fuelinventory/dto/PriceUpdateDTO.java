package com.sliit.vehicleservice.fuelinventory.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class PriceUpdateDTO {

    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.00", inclusive = true, message = "Unit price cannot be negative")
    private BigDecimal unitPrice;

    public PriceUpdateDTO() {}

    public PriceUpdateDTO(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
}