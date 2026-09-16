package com.sliit.vehicleservice.fuelinventory.repository;

import com.sliit.vehicleservice.fuelinventory.entity.SparePart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SparePartRepository extends JpaRepository<SparePart, Long> {

    boolean existsByPartNameIgnoreCase(String partName);

    boolean existsByPartNameIgnoreCaseAndPartIdNot(String partName, Long partId);

    List<SparePart> findByPartNameContainingIgnoreCase(String partName);

    List<SparePart> findByCategoryIgnoreCase(String category);

    List<SparePart> findByStatusIgnoreCase(String status);

    @Query("SELECT s FROM SparePart s WHERE LOWER(s.partName) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(s.category) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<SparePart> searchSpareParts(@Param("query") String query);

    @Query("SELECT s FROM SparePart s WHERE s.quantity <= s.reorderLevel")
    List<SparePart> findLowStockSpareParts();

    @Query("SELECT COUNT(s) FROM SparePart s WHERE s.quantity <= s.reorderLevel")
    long countLowStockSpareParts();

    @Query("SELECT COALESCE(SUM(s.quantity), 0) FROM SparePart s")
    Integer sumTotalQuantity();

    @Query("SELECT DISTINCT s.category FROM SparePart s ORDER BY s.category ASC")
    List<String> findDistinctCategories();
}