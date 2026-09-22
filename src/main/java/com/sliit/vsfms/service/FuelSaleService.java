package com.sliit.vsfms.service;

import com.sliit.vsfms.model.*;
import com.sliit.vsfms.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class FuelSaleService {

    private final FuelSaleRepository sales;
    private final InventoryItemRepository inventory;

    public FuelSaleService(
            FuelSaleRepository sales,
            InventoryItemRepository inventory) {

        this.sales = sales;
        this.inventory = inventory;
    }

    // READ - All sales
    public List<FuelSale> findAll() {
        return sales.findAll();
    }

    // READ - Recent sales
    public List<FuelSale> recent() {
        return sales.findTop10ByOrderBySaleDateTimeDesc();
    }

    // READ - Find by ID
    public FuelSale findById(Long id) {

        return sales.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Fuel sale not found: " + id
                        )
                );
    }

    // Get fuel items
    public List<InventoryItem> fuels() {

        return inventory
                .findAllByItemTypeOrderByNameAsc(
                        InventoryItemType.FUEL
                );
    }

    // CREATE
    @Transactional
    public FuelSale create(FuelSale sale) {

        InventoryItem item =
                validateAndFindFuelItem(sale);

        validateQuantity(
                sale.getQuantity()
        );

        // Check stock
        if (item.getQuantity()
                .compareTo(
                        sale.getQuantity()
                ) < 0) {

            throw new IllegalArgumentException(
                    "Insufficient fuel stock. Available: "
                            + item.getQuantity()
                            + " "
                            + item.getUnit()
            );
        }

        // Set fuel item
        sale.setFuelItem(item);

        // Set current fuel price
        sale.setUnitPrice(
                item.getUnitPrice()
        );

        // Calculate total
        sale.setTotalAmount(
                calculateTotal(
                        item.getUnitPrice(),
                        sale.getQuantity()
                )
        );

        // Set date
        sale.setSaleDateTime(
                LocalDateTime.now()
        );

        // Generate invoice
        sale.setInvoiceNumber(
                generateInvoiceNumber()
        );

        // Reduce inventory quantity
        item.setQuantity(
                item.getQuantity()
                        .subtract(
                                sale.getQuantity()
                        )
        );

        inventory.save(item);

        // Save sale
        return sales.save(sale);
    }

    // UPDATE
    @Transactional
    public FuelSale update(
            Long id,
            FuelSale editedSale) {

        // Find existing sale
        FuelSale existing =
                findById(id);

        // Original fuel
        InventoryItem oldItem =
                existing.getFuelItem();

        // Original quantity
        BigDecimal oldQuantity =
                existing.getQuantity();

        // New selected fuel
        InventoryItem newItem =
                validateAndFindFuelItem(
                        editedSale
                );

        // Validate edited quantity
        validateQuantity(
                editedSale.getQuantity()
        );

        /*
         * Restore the original quantity
         * back to inventory first.
         */
        oldItem.setQuantity(
                oldItem.getQuantity()
                        .add(oldQuantity)
        );

        inventory.save(oldItem);

        /*
         * If the user selected the same fuel,
         * continue using the restored object.
         */
        if (Objects.equals(
                oldItem.getId(),
                newItem.getId())) {

            newItem = oldItem;
        }

        /*
         * Check stock after restoring
         * original quantity.
         */
        if (newItem.getQuantity()
                .compareTo(
                        editedSale.getQuantity()
                ) < 0) {

            throw new IllegalArgumentException(
                    "Insufficient fuel stock. Available: "
                            + newItem.getQuantity()
                            + " "
                            + newItem.getUnit()
            );
        }

        /*
         * Deduct edited quantity
         * from selected fuel.
         */
        newItem.setQuantity(
                newItem.getQuantity()
                        .subtract(
                                editedSale.getQuantity()
                        )
        );

        inventory.save(newItem);

        /*
         * Update sale information.
         */

        existing.setFuelItem(
                newItem
        );

        existing.setQuantity(
                editedSale.getQuantity()
        );

        existing.setUnitPrice(
                newItem.getUnitPrice()
        );

        existing.setTotalAmount(
                calculateTotal(
                        newItem.getUnitPrice(),
                        editedSale.getQuantity()
                )
        );

        existing.setCustomerName(
                editedSale.getCustomerName()
        );

        existing.setVehicleNumber(
                editedSale.getVehicleNumber()
        );

        existing.setPaymentMethod(
                editedSale.getPaymentMethod()
        );

        existing.setPaymentStatus(
                editedSale.getPaymentStatus()
        );

        /*
         * We DO NOT change:
         *
         * existing invoice number
         * existing sale date/time
         */

        return sales.save(existing);
    }

    // DELETE
    @Transactional
    public void delete(Long id) {

        FuelSale sale =
                findById(id);

        InventoryItem item =
                sale.getFuelItem();

        /*
         * Return sold quantity
         * back to inventory.
         */
        item.setQuantity(
                item.getQuantity()
                        .add(
                                sale.getQuantity()
                        )
        );

        inventory.save(item);

        sales.delete(sale);
    }

    // Dashboard / Report methods

    public BigDecimal paidRevenue() {

        BigDecimal value =
                sales.totalRevenueByStatus(
                        PaymentStatus.PAID
                );

        return value == null
                ? BigDecimal.ZERO
                : value;
    }

    public long paidCount() {

        return sales.countByPaymentStatus(
                PaymentStatus.PAID
        );
    }

    public BigDecimal paidQuantity() {

        BigDecimal value =
                sales.totalQuantityByStatus(
                        PaymentStatus.PAID
                );

        return value == null
                ? BigDecimal.ZERO
                : value;
    }

    public List<FuelSale> between(
            LocalDate from,
            LocalDate to) {

        return sales
                .findBySaleDateTimeBetweenOrderBySaleDateTimeDesc(

                        from.atStartOfDay(),

                        to.plusDays(1)
                                .atStartOfDay()
                                .minusNanos(1)
                );
    }

    // Validate selected fuel
    private InventoryItem validateAndFindFuelItem(
            FuelSale sale) {

        if (sale.getFuelItem() == null
                || sale.getFuelItem().getId() == null) {

            throw new IllegalArgumentException(
                    "Please select a fuel type."
            );
        }

        InventoryItem item =
                inventory.findById(
                                sale.getFuelItem().getId()
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Fuel item not found."
                                )
                        );

        if (item.getItemType()
                != InventoryItemType.FUEL) {

            throw new IllegalArgumentException(
                    "Only fuel items can be sold."
            );
        }

        return item;
    }

    // Validate quantity
    private void validateQuantity(
            BigDecimal quantity) {

        if (quantity == null
                || quantity.compareTo(
                BigDecimal.ZERO
        ) <= 0) {

            throw new IllegalArgumentException(
                    "Quantity must be greater than zero."
            );
        }
    }

    // Calculate total price
    private BigDecimal calculateTotal(
            BigDecimal unitPrice,
            BigDecimal quantity) {

        return unitPrice
                .multiply(quantity)
                .setScale(
                        2,
                        RoundingMode.HALF_UP
                );
    }

    // Generate invoice number
    private String generateInvoiceNumber() {

        return "INV-"
                + LocalDateTime.now()
                .format(
                        DateTimeFormatter
                                .ofPattern(
                                        "yyyyMMddHHmmss"
                                )
                )
                + "-"
                + ThreadLocalRandom
                .current()
                .nextInt(
                        100,
                        999
                );
    }
}