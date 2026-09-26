package com.sliit.vsfms.service;

import com.sliit.vsfms.model.FuelSale;
import com.sliit.vsfms.model.InventoryItem;
import com.sliit.vsfms.model.InventoryItemType;
import com.sliit.vsfms.model.PaymentStatus;
import com.sliit.vsfms.repository.FuelSaleRepository;
import com.sliit.vsfms.repository.InventoryItemRepository;

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


    // ==========================================
    // CONSTRUCTOR
    // ==========================================

    public FuelSaleService(
            FuelSaleRepository sales,
            InventoryItemRepository inventory) {

        this.sales = sales;
        this.inventory = inventory;
    }


    // ==========================================
    // READ - GET ALL SALES
    // ==========================================

    public List<FuelSale> findAll() {

        return sales.findAll();
    }


    // ==========================================
    // READ - GET RECENT SALES
    // ==========================================

    public List<FuelSale> recent() {

        return sales.findTop10ByOrderBySaleDateTimeDesc();
    }


    // ==========================================
    // READ - GET SALE BY ID
    // ==========================================

    public FuelSale findById(Long id) {

        return sales.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Fuel sale not found: " + id
                        )
                );
    }


    // ==========================================
    // GET FUEL ITEMS
    // ==========================================

    public List<InventoryItem> fuels() {

        return inventory.findAllByItemTypeOrderByNameAsc(
                InventoryItemType.FUEL
        );
    }


    // ==========================================
    // CREATE SALE
    // ==========================================

    @Transactional
    public FuelSale create(FuelSale sale) {

        // Find selected fuel item
        InventoryItem item =
                validateAndFindFuelItem(sale);


        // Validate quantity
        validateQuantity(
                sale.getQuantity()
        );


        // Check available stock
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


        // Calculate total amount
        sale.setTotalAmount(
                calculateTotal(
                        item.getUnitPrice(),
                        sale.getQuantity()
                )
        );


        // Set current date and time
        sale.setSaleDateTime(
                LocalDateTime.now()
        );


        // Generate invoice number
        sale.setInvoiceNumber(
                generateInvoiceNumber()
        );


        // Reduce fuel stock
        item.setQuantity(
                item.getQuantity()
                        .subtract(
                                sale.getQuantity()
                        )
        );


        // Save updated inventory
        inventory.save(item);


        // Save sale
        return sales.save(sale);
    }


    // ==========================================
    // UPDATE / EDIT SALE
    // ==========================================

    @Transactional
    public FuelSale update(
            Long id,
            FuelSale editedSale) {


        // Find existing sale
        FuelSale existing =
                findById(id);


        // Get old fuel item
        InventoryItem oldItem =
                existing.getFuelItem();


        // Get old quantity
        BigDecimal oldQuantity =
                existing.getQuantity();


        // Find newly selected fuel
        InventoryItem newItem =
                validateAndFindFuelItem(
                        editedSale
                );


        // Validate new quantity
        validateQuantity(
                editedSale.getQuantity()
        );


        /*
         * STEP 1
         *
         * Restore the quantity from
         * the original sale.
         */

        oldItem.setQuantity(
                oldItem.getQuantity()
                        .add(oldQuantity)
        );


        /*
         * STEP 2
         *
         * If user is editing the SAME fuel,
         * use oldItem because it now contains
         * the restored stock quantity.
         */

        if (Objects.equals(
                oldItem.getId(),
                newItem.getId())) {

            newItem = oldItem;
        }


        /*
         * STEP 3
         *
         * Check whether enough stock exists
         * for the new quantity.
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
         * STEP 4
         *
         * If the fuel type changed,
         * save the restored old fuel stock.
         */

        if (!Objects.equals(
                oldItem.getId(),
                newItem.getId())) {

            inventory.save(oldItem);
        }


        /*
         * STEP 5
         *
         * Deduct the new quantity
         * from the selected fuel.
         */

        newItem.setQuantity(
                newItem.getQuantity()
                        .subtract(
                                editedSale.getQuantity()
                        )
        );


        // Save new inventory quantity
        inventory.save(newItem);


        /*
         * STEP 6
         *
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
         * Invoice number and original
         * sale date/time are NOT changed.
         */


        // Save updated sale
        return sales.save(existing);
    }


    // ==========================================
    // DELETE SALE
    // ==========================================

    @Transactional
    public void delete(Long id) {

        // Find sale
        FuelSale sale =
                findById(id);


        // Get fuel item
        InventoryItem item =
                sale.getFuelItem();


        /*
         * Restore sold quantity
         * back to inventory.
         */

        item.setQuantity(
                item.getQuantity()
                        .add(
                                sale.getQuantity()
                        )
        );


        // Save inventory
        inventory.save(item);


        // Delete sale
        sales.delete(sale);
    }


    // ==========================================
    // REPORT - PAID REVENUE
    // ==========================================

    public BigDecimal paidRevenue() {

        BigDecimal value =
                sales.totalRevenueByStatus(
                        PaymentStatus.PAID
                );


        if (value == null) {

            return BigDecimal.ZERO;
        }


        return value;
    }


    // ==========================================
    // REPORT - PAID SALES COUNT
    // ==========================================

    public long paidCount() {

        return sales.countByPaymentStatus(
                PaymentStatus.PAID
        );
    }


    // ==========================================
    // REPORT - PAID FUEL QUANTITY
    // ==========================================

    public BigDecimal paidQuantity() {

        BigDecimal value =
                sales.totalQuantityByStatus(
                        PaymentStatus.PAID
                );


        if (value == null) {

            return BigDecimal.ZERO;
        }


        return value;
    }


    // ==========================================
    // REPORT - SALES BETWEEN TWO DATES
    // ==========================================

    public List<FuelSale> between(
            LocalDate from,
            LocalDate to) {


        LocalDateTime startDate =
                from.atStartOfDay();


        LocalDateTime endDate =
                to.plusDays(1)
                        .atStartOfDay()
                        .minusNanos(1);


        return sales
                .findBySaleDateTimeBetweenOrderBySaleDateTimeDesc(
                        startDate,
                        endDate
                );
    }


    // ==========================================
    // VALIDATE FUEL ITEM
    // ==========================================

    private InventoryItem validateAndFindFuelItem(
            FuelSale sale) {


        // Check selected fuel
        if (sale.getFuelItem() == null
                || sale.getFuelItem().getId() == null) {

            throw new IllegalArgumentException(
                    "Please select a fuel type."
            );
        }


        // Find fuel from database
        InventoryItem item =
                inventory
                        .findById(
                                sale.getFuelItem().getId()
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Fuel item not found."
                                )
                        );


        // Make sure selected item is fuel
        if (item.getItemType()
                != InventoryItemType.FUEL) {

            throw new IllegalArgumentException(
                    "Only fuel items can be sold."
            );
        }


        return item;
    }


    // ==========================================
    // VALIDATE QUANTITY
    // ==========================================

    private void validateQuantity(
            BigDecimal quantity) {


        if (quantity == null) {

            throw new IllegalArgumentException(
                    "Quantity is required."
            );
        }


        if (quantity.compareTo(
                BigDecimal.ZERO
        ) <= 0) {

            throw new IllegalArgumentException(
                    "Quantity must be greater than zero."
            );
        }
    }


    // ==========================================
    // CALCULATE TOTAL
    // ==========================================

    private BigDecimal calculateTotal(
            BigDecimal unitPrice,
            BigDecimal quantity) {


        if (unitPrice == null) {

            throw new IllegalArgumentException(
                    "Fuel unit price is not available."
            );
        }


        return unitPrice
                .multiply(quantity)
                .setScale(
                        2,
                        RoundingMode.HALF_UP
                );
    }


    // ==========================================
    // GENERATE INVOICE NUMBER
    // ==========================================

    private String generateInvoiceNumber() {


        String dateTime =
                LocalDateTime.now()
                        .format(
                                DateTimeFormatter.ofPattern(
                                        "yyyyMMddHHmmss"
                                )
                        );


        int randomNumber =
                ThreadLocalRandom
                        .current()
                        .nextInt(
                                100,
                                1000
                        );


        return "INV-"
                + dateTime
                + "-"
                + randomNumber;
    }
}