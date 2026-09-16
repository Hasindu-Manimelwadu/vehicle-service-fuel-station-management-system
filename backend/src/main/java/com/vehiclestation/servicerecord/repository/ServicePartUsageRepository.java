package com.vehiclestation.servicerecord.repository;

import com.vehiclestation.servicerecord.entity.ServicePartUsage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ServicePartUsageRepository extends JpaRepository<ServicePartUsage, Long> {

    List<ServicePartUsage> findByServiceRecord_ServiceRecordIdOrderByRecordedAtAsc(Long serviceRecordId);

    Optional<ServicePartUsage> findByUsageIdAndServiceRecord_ServiceRecordId(Long usageId, Long serviceRecordId);
}
