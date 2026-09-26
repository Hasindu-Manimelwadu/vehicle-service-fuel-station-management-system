package com.sliit.vehiclemgmt.repository;

import com.sliit.vehiclemgmt.entity.ServiceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Service Record data access operations.
 */
@Repository
public interface ServiceRecordRepository extends JpaRepository<ServiceRecord, Long> {

    List<ServiceRecord> findByVehicleVehicleIdOrderByServiceDateDesc(Long vehicleId);
}
