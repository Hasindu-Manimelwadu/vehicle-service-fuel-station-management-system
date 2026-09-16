package com.vehiclestation.servicerecord.repository;

import com.vehiclestation.servicerecord.entity.ServiceRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceRecordRepository extends JpaRepository<ServiceRecord, Long> {
}
