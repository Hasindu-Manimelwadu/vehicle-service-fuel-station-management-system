package com.sliit.vsfms.repository;
import com.sliit.vsfms.model.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
public interface FuelSaleRepository extends JpaRepository<FuelSale, Long> {
    List<FuelSale> findTop10ByOrderBySaleDateTimeDesc();
    @Query("select coalesce(sum(s.totalAmount),0) from FuelSale s where s.paymentStatus = :status")
    BigDecimal totalRevenueByStatus(@Param("status") PaymentStatus status);
    long countByPaymentStatus(PaymentStatus status);
    @Query("select coalesce(sum(s.quantity),0) from FuelSale s where s.paymentStatus = :status")
    BigDecimal totalQuantityByStatus(@Param("status") PaymentStatus status);
    List<FuelSale> findBySaleDateTimeBetweenOrderBySaleDateTimeDesc(LocalDateTime from, LocalDateTime to);
}
