package com.sliit.vsfms.service;

import com.sliit.vsfms.model.FuelSale;
import com.sliit.vsfms.model.InventoryItem;
import com.sliit.vsfms.repository.FuelSaleRepository;
import com.sliit.vsfms.repository.InventoryItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class FuelSaleService {

    private final FuelSaleRepository fuelSaleRepository;
    private final InventoryItemRepository inventoryItemRepository;

    public FuelSaleService(FuelSaleRepository fuelSaleRepository,
                           InventoryItemRepository inventoryItemRepository) {
        this.fuelSaleRepository = fuelSaleRepository;
        this.inventoryItemRepository = inventoryItemRepository;
    }

    @Transactional
    public FuelSale createSale(Long inventoryItemId,
                               Double quantity,
                               String vehicleNumber,
                               String customerName) {

        InventoryItem inventoryItem = inventoryItemRepository
                .findById(inventoryItemId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Inventory item not found"));

        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than zero");
        }

        if (inventoryItem.getAvailableQuantity() < quantity) {
            throw new IllegalArgumentException(
                    "Insufficient inventory quantity");
        }

        BigDecimal unitPrice = inventoryItem.getUnitPrice();

        BigDecimal totalAmount = unitPrice.multiply(
                BigDecimal.valueOf(quantity)
        );

        FuelSale fuelSale = new FuelSale();

        fuelSale.setInvoiceNumber(generateInvoiceNumber());
        fuelSale.setInventoryItem(inventoryItem);
        fuelSale.setQuantity(quantity);
        fuelSale.setUnitPrice(unitPrice);
        fuelSale.setTotalAmount(totalAmount);
        fuelSale.setSaleDateTime(LocalDateTime.now());
        fuelSale.setVehicleNumber(vehicleNumber);
        fuelSale.setCustomerName(customerName);

        inventoryItem.setAvailableQuantity(
                inventoryItem.getAvailableQuantity() - quantity
        );

        inventoryItemRepository.save(inventoryItem);

        return fuelSaleRepository.save(fuelSale);
    }

    private String generateInvoiceNumber() {
        return "INV-" +
                UUID.randomUUID()
                        .toString()
                        .substring(0, 8)
                        .toUpperCase();
    }
}