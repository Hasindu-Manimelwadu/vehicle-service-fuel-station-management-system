package com.sliit.vsfms.service;

import com.sliit.vsfms.model.FuelSale;
import com.sliit.vsfms.model.InventoryItem;
import com.sliit.vsfms.repository.FuelSaleRepository;
import com.sliit.vsfms.repository.InventoryItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
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

        validateQuantity(quantity);

        if (inventoryItem.getAvailableQuantity() < quantity) {
            throw new IllegalArgumentException(
                    "Insufficient inventory quantity");
        }

        BigDecimal unitPrice = inventoryItem.getUnitPrice();

        BigDecimal totalAmount =
                unitPrice.multiply(BigDecimal.valueOf(quantity));

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

    public List<FuelSale> getAllSales() {
        return fuelSaleRepository.findAll();
    }

    public FuelSale getSaleById(Long id) {
        return fuelSaleRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Fuel sale not found"));
    }

    public FuelSale getSaleByInvoiceNumber(String invoiceNumber) {
        return fuelSaleRepository.findByInvoiceNumber(invoiceNumber)
                .orElseThrow(() ->
                        new IllegalArgumentException("Invoice not found"));
    }

    @Transactional
    public FuelSale updateSale(Long id,
                               Long inventoryItemId,
                               Double quantity,
                               String vehicleNumber,
                               String customerName) {

        validateQuantity(quantity);

        FuelSale existingSale = getSaleById(id);

        InventoryItem oldInventoryItem =
                existingSale.getInventoryItem();

        InventoryItem newInventoryItem = inventoryItemRepository
                .findById(inventoryItemId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Inventory item not found"));

        double oldQuantity = existingSale.getQuantity();

        if (oldInventoryItem.getId().equals(newInventoryItem.getId())) {

            double availableAfterRestore =
                    oldInventoryItem.getAvailableQuantity()
                            + oldQuantity;

            if (availableAfterRestore < quantity) {
                throw new IllegalArgumentException(
                        "Insufficient inventory quantity");
            }

            oldInventoryItem.setAvailableQuantity(
                    availableAfterRestore - quantity
            );

            inventoryItemRepository.save(oldInventoryItem);

        } else {

            oldInventoryItem.setAvailableQuantity(
                    oldInventoryItem.getAvailableQuantity()
                            + oldQuantity
            );

            if (newInventoryItem.getAvailableQuantity() < quantity) {
                throw new IllegalArgumentException(
                        "Insufficient inventory quantity");
            }

            newInventoryItem.setAvailableQuantity(
                    newInventoryItem.getAvailableQuantity()
                            - quantity
            );

            inventoryItemRepository.save(oldInventoryItem);
            inventoryItemRepository.save(newInventoryItem);
        }

        BigDecimal unitPrice = newInventoryItem.getUnitPrice();

        BigDecimal totalAmount =
                unitPrice.multiply(BigDecimal.valueOf(quantity));

        existingSale.setInventoryItem(newInventoryItem);
        existingSale.setQuantity(quantity);
        existingSale.setUnitPrice(unitPrice);
        existingSale.setTotalAmount(totalAmount);
        existingSale.setVehicleNumber(vehicleNumber);
        existingSale.setCustomerName(customerName);

        return fuelSaleRepository.save(existingSale);
    }

    private void validateQuantity(Double quantity) {

        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than zero");
        }
    }

    private String generateInvoiceNumber() {

        return "INV-" +
                UUID.randomUUID()
                        .toString()
                        .substring(0, 8)
                        .toUpperCase();
    }
}