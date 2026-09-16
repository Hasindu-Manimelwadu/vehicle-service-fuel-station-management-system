package com.vehiclestation.servicerecord.repository;

import com.vehiclestation.servicerecord.entity.TechnicianAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TechnicianAssignmentRepository extends JpaRepository<TechnicianAssignment, Long> {

    Optional<TechnicianAssignment> findByServiceRecord_ServiceRecordId(Long serviceRecordId);

    List<TechnicianAssignment> findByTechnician_UserIdOrderByAssignedDateDesc(Long technicianId);
}
