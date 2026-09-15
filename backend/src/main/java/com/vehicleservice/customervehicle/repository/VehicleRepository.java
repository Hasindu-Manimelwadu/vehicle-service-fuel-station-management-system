package com.vehicleservice.customervehicle.repository;

import com.vehicleservice.customervehicle.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    List<Vehicle> findByCustomerId(Long customerId);
    Optional<Vehicle> findByLicensePlateNumber(String licensePlateNumber);
    boolean existsByLicensePlateNumber(String licensePlateNumber);
}
