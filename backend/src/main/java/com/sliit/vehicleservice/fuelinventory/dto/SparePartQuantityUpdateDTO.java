package com.sliit.vehicleservice.fuelinventory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public class SparePartQuantityUpdateDTO {

    @NotBlank(message = "Action is required")
    @Pattern(regexp = "^(ADD|SET|DEDUCT)$", message = "Action must be ADD, SET, or DEDUCT")
    private String action;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be greater than 0")
    private Integer quantity;

    public SparePartQuantityUpdateDTO() {}

    public SparePartQuantityUpdateDTO(String action, Integer quantity) {
        this.action = action;
        this.quantity = quantity;
    }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}