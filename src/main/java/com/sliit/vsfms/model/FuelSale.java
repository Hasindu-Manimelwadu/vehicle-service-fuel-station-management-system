package com.sliit.vsfms.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "fuel_sales")
public class FuelSale {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, unique = true) private String invoiceNumber;
    @NotNull @ManyToOne(fetch = FetchType.EAGER) @JoinColumn(name = "fuel_item_id", nullable = false) private InventoryItem fuelItem;
    @NotNull @DecimalMin("0.01") private BigDecimal quantity;
    @DecimalMin("0.00") private BigDecimal unitPrice;
    @DecimalMin("0.00") private BigDecimal totalAmount;
    private String customerName;
    private String vehicleNumber;
    @NotNull @Enumerated(EnumType.STRING) private PaymentMethod paymentMethod = PaymentMethod.CASH;
    @NotNull @Enumerated(EnumType.STRING) private PaymentStatus paymentStatus = PaymentStatus.PAID;
    private LocalDateTime saleDateTime;
    @PrePersist void prePersist(){ if(saleDateTime == null) saleDateTime = LocalDateTime.now(); }
    public Long getId(){return id;} public void setId(Long v){id=v;}
    public String getInvoiceNumber(){return invoiceNumber;} public void setInvoiceNumber(String v){invoiceNumber=v;}
    public InventoryItem getFuelItem(){return fuelItem;} public void setFuelItem(InventoryItem v){fuelItem=v;}
    public BigDecimal getQuantity(){return quantity;} public void setQuantity(BigDecimal v){quantity=v;}
    public BigDecimal getUnitPrice(){return unitPrice;} public void setUnitPrice(BigDecimal v){unitPrice=v;}
    public BigDecimal getTotalAmount(){return totalAmount;} public void setTotalAmount(BigDecimal v){totalAmount=v;}
    public String getCustomerName(){return customerName;} public void setCustomerName(String v){customerName=v;}
    public String getVehicleNumber(){return vehicleNumber;} public void setVehicleNumber(String v){vehicleNumber=v;}
    public PaymentMethod getPaymentMethod(){return paymentMethod;} public void setPaymentMethod(PaymentMethod v){paymentMethod=v;}
    public PaymentStatus getPaymentStatus(){return paymentStatus;} public void setPaymentStatus(PaymentStatus v){paymentStatus=v;}
    public LocalDateTime getSaleDateTime(){return saleDateTime;} public void setSaleDateTime(LocalDateTime v){saleDateTime=v;}
}
