package com.sliit.vehicleservice.fuelinventory.repository;

import com.sliit.vehicleservice.fuelinventory.entity.FuelStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FuelStockRepository extends JpaRepository<FuelStock, Long> {

    boolean existsByTankNo(String tankNo);

    boolean existsByTankNoAndFuelStockIdNot(String tankNo, Long fuelStockId);

    Optional<FuelStock> findByTankNo(String tankNo);

    List<FuelStock> findByFuelTypeContainingIgnoreCase(String fuelType);

    List<FuelStock> findByTankNoContainingIgnoreCase(String tankNo);

    @Query("SELECT f FROM FuelStock f WHERE LOWER(f.fuelType) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(f.tankNo) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<FuelStock> searchFuelStocks(@Param("query") String query);

    @Query("SELECT f FROM FuelStock f WHERE f.currentQuantity <= f.reorderLevel")
    List<FuelStock> findLowStockFuel();

    @Query("SELECT COUNT(f) FROM FuelStock f WHERE f.currentQuantity <= f.reorderLevel")
    long countLowStockFuel();

    @Query("SELECT COALESCE(SUM(f.tankCapacity), 0.0) FROM FuelStock f")
    Double sumTotalCapacity();

    @Query("SELECT COALESCE(SUM(f.currentQuantity), 0.0) FROM FuelStock f")
    Double sumTotalCurrentQuantity();
}