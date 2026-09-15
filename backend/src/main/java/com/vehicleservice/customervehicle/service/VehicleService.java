package com.vehicleservice.customervehicle.service;

import com.vehicleservice.customervehicle.entity.Customer;
import com.vehicleservice.customervehicle.entity.Vehicle;
import com.vehicleservice.customervehicle.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final CustomerService customerService;

    @Autowired
    public VehicleService(VehicleRepository vehicleRepository, CustomerService customerService) {
        this.vehicleRepository = vehicleRepository;
        this.customerService = customerService;
    }

    public Vehicle registerVehicle(Long customerId, Vehicle vehicle) {
        if (vehicleRepository.existsByLicensePlateNumber(vehicle.getLicensePlateNumber())) {
            throw new IllegalArgumentException("This vehicle is already registered.");
        }
        Customer customer = customerService.getCustomerById(customerId);
        vehicle.setCustomer(customer);
        return vehicleRepository.save(vehicle);
    }

    public List<Vehicle> getVehiclesByCustomer(Long customerId) {
        return vehicleRepository.findByCustomerId(customerId);
    }

    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    public Vehicle getVehicleById(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found with id: " + id));
    }

    public Vehicle updateVehicle(Long id, Vehicle updatedDetails) {
        Vehicle existing = getVehicleById(id);
        existing.setMake(updatedDetails.getMake());
        existing.setModel(updatedDetails.getModel());
        existing.setYear(updatedDetails.getYear());
        existing.setColor(updatedDetails.getColor());
        return vehicleRepository.save(existing);
    }

    public void deleteVehicle(Long id) {
        if (!vehicleRepository.existsById(id)) {
            throw new IllegalArgumentException("Vehicle not found with id: " + id);
        }
        vehicleRepository.deleteById(id);
    }

    // Placeholder: full service history will pull from the Service Record
    // module (owned by another team member) once that module exists.
    // For now this just confirms the vehicle exists so the frontend has
    // something to call.
    public Vehicle getVehicleServiceHistoryPlaceholder(Long vehicleId) {
        return getVehicleById(vehicleId);
    }
}
