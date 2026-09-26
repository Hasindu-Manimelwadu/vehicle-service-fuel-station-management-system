package com.sliit.vehiclemgmt.service;

import com.sliit.vehiclemgmt.dto.ServiceRecordDTO;
import com.sliit.vehiclemgmt.dto.VehicleDTO;
import com.sliit.vehiclemgmt.entity.Customer;
import com.sliit.vehiclemgmt.entity.ServiceRecord;
import com.sliit.vehiclemgmt.entity.Vehicle;
import com.sliit.vehiclemgmt.exception.ConflictException;
import com.sliit.vehiclemgmt.exception.ResourceNotFoundException;
import com.sliit.vehiclemgmt.repository.CustomerRepository;
import com.sliit.vehiclemgmt.repository.ServiceRecordRepository;
import com.sliit.vehiclemgmt.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for Vehicle Management operations.
 * Implements vehicle registration, updates, deletion, customer vehicle queries, and service history lookup.
 */
@Service
@Transactional
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final CustomerRepository customerRepository;
    private final ServiceRecordRepository serviceRecordRepository;

    @Autowired
    public VehicleService(VehicleRepository vehicleRepository,
                          CustomerRepository customerRepository,
                          ServiceRecordRepository serviceRecordRepository) {
        this.vehicleRepository = vehicleRepository;
        this.customerRepository = customerRepository;
        this.serviceRecordRepository = serviceRecordRepository;
    }

    /**
     * Register a new vehicle under a customer account.
     */
    public VehicleDTO registerVehicle(VehicleDTO dto) {
        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + dto.getCustomerId()));

        String formattedPlate = dto.getPlateNumber().trim().toUpperCase();
        if (vehicleRepository.existsByPlateNumber(formattedPlate)) {
            throw new ConflictException("Vehicle with plate number '" + formattedPlate + "' is already registered.");
        }

        Vehicle vehicle = new Vehicle(
                customer,
                formattedPlate,
                dto.getMake().trim(),
                dto.getModel().trim(),
                dto.getYear(),
                dto.getVehicleType().trim(),
                dto.getFuelType().trim(),
                dto.getColor().trim()
        );

        Vehicle saved = vehicleRepository.save(vehicle);
        return VehicleDTO.fromEntity(saved);
    }

    /**
     * Retrieve all vehicles belonging to a specific customer.
     */
    @Transactional(readOnly = true)
    public List<VehicleDTO> getVehiclesByCustomer(Long customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer not found with ID: " + customerId);
        }
        List<Vehicle> vehicles = vehicleRepository.findByCustomerCustomerId(customerId);
        return vehicles.stream()
                .map(VehicleDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Retrieve a specific vehicle by ID.
     */
    @Transactional(readOnly = true)
    public VehicleDTO getVehicleById(Long vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with ID: " + vehicleId));
        return VehicleDTO.fromEntity(vehicle);
    }

    /**
     * Update an existing vehicle's details.
     */
    public VehicleDTO updateVehicle(Long vehicleId, VehicleDTO dto) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with ID: " + vehicleId));

        String newPlate = dto.getPlateNumber().trim().toUpperCase();
        if (!vehicle.getPlateNumber().equalsIgnoreCase(newPlate) && vehicleRepository.existsByPlateNumber(newPlate)) {
            throw new ConflictException("Vehicle plate number '" + newPlate + "' is already assigned to another vehicle.");
        }

        vehicle.setPlateNumber(newPlate);
        vehicle.setMake(dto.getMake().trim());
        vehicle.setModel(dto.getModel().trim());
        vehicle.setYear(dto.getYear());
        vehicle.setVehicleType(dto.getVehicleType().trim());
        vehicle.setFuelType(dto.getFuelType().trim());
        vehicle.setColor(dto.getColor().trim());

        Vehicle updated = vehicleRepository.save(vehicle);
        return VehicleDTO.fromEntity(updated);
    }

    /**
     * Delete a vehicle from the customer's account.
     */
    public void deleteVehicle(Long vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with ID: " + vehicleId));
        vehicleRepository.delete(vehicle);
    }

    /**
     * View vehicle service history (integration with Service Records placeholder table).
     */
    @Transactional(readOnly = true)
    public List<ServiceRecordDTO> getServiceHistory(Long vehicleId) {
        if (!vehicleRepository.existsById(vehicleId)) {
            throw new ResourceNotFoundException("Vehicle not found with ID: " + vehicleId);
        }

        List<ServiceRecord> records = serviceRecordRepository.findByVehicleVehicleIdOrderByServiceDateDesc(vehicleId);
        return records.stream()
                .map(ServiceRecordDTO::fromEntity)
                .collect(Collectors.toList());
    }
}
