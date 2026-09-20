package com.sliit.vsfms.service;

import com.sliit.vsfms.model.*;
import com.sliit.vsfms.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class FuelSaleService {
    private final FuelSaleRepository sales;
    private final InventoryItemRepository inventory;
    public FuelSaleService(FuelSaleRepository sales, InventoryItemRepository inventory){this.sales=sales;this.inventory=inventory;}
    public List<FuelSale> findAll(){return sales.findAll();}
    public List<FuelSale> recent(){return sales.findTop10ByOrderBySaleDateTimeDesc();}
    public FuelSale findById(Long id){return sales.findById(id).orElseThrow(()->new IllegalArgumentException("Fuel sale not found: "+id));}
    public List<InventoryItem> fuels(){return inventory.findAllByItemTypeOrderByNameAsc(InventoryItemType.FUEL);}
    @Transactional
    public FuelSale create(FuelSale sale){
        if(sale.getFuelItem()==null || sale.getFuelItem().getId()==null) throw new IllegalArgumentException("Please select a fuel type.");
        InventoryItem item=inventory.findById(sale.getFuelItem().getId()).orElseThrow(()->new IllegalArgumentException("Fuel item not found."));
        if(item.getItemType()!=InventoryItemType.FUEL) throw new IllegalArgumentException("Only fuel items can be sold.");
        if(sale.getQuantity()==null || sale.getQuantity().compareTo(BigDecimal.ZERO)<=0) throw new IllegalArgumentException("Quantity must be greater than zero.");
        if(item.getQuantity().compareTo(sale.getQuantity())<0) throw new IllegalArgumentException("Insufficient fuel stock. Available: "+item.getQuantity()+" "+item.getUnit());
        sale.setFuelItem(item); sale.setUnitPrice(item.getUnitPrice());
        sale.setTotalAmount(item.getUnitPrice().multiply(sale.getQuantity()).setScale(2, RoundingMode.HALF_UP));
        sale.setSaleDateTime(LocalDateTime.now());
        sale.setInvoiceNumber("INV-"+LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))+"-"+ThreadLocalRandom.current().nextInt(100,999));
        item.setQuantity(item.getQuantity().subtract(sale.getQuantity())); inventory.save(item);
        return sales.save(sale);
    }
    @Transactional
    public void delete(Long id){
        FuelSale sale=findById(id);
        InventoryItem item=sale.getFuelItem();
        item.setQuantity(item.getQuantity().add(sale.getQuantity()));
        inventory.save(item);
        sales.delete(sale);
    }
    public BigDecimal paidRevenue(){BigDecimal x=sales.totalRevenueByStatus(PaymentStatus.PAID);return x==null?BigDecimal.ZERO:x;}
    public long paidCount(){return sales.countByPaymentStatus(PaymentStatus.PAID);}
    public BigDecimal paidQuantity(){BigDecimal x=sales.totalQuantityByStatus(PaymentStatus.PAID);return x==null?BigDecimal.ZERO:x;}
    public List<FuelSale> between(LocalDate from, LocalDate to){return sales.findBySaleDateTimeBetweenOrderBySaleDateTimeDesc(from.atStartOfDay(),to.plusDays(1).atStartOfDay().minusNanos(1));}
}
