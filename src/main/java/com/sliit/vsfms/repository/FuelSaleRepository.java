package com.sliit.vsfms.repository;

import com.sliit.vsfms.model.FuelSale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FuelSaleRepository extends JpaRepository<FuelSale, Long> {

    Optional<FuelSale> findByInvoiceNumber(String invoiceNumber);

    List<FuelSale> findBySaleDateTimeBetween(
            LocalDateTime startDateTime,
            LocalDateTime endDateTime
    );

    List<FuelSale> findByCustomerNameContainingIgnoreCase(String customerName);

    List<FuelSale> findByVehicleNumberContainingIgnoreCase(String vehicleNumber);
}