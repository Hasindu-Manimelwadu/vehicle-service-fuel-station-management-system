package com.vehicleservice.customervehicle.service;

import com.vehicleservice.customervehicle.entity.Customer;
import com.vehicleservice.customervehicle.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer registerCustomer(Customer customer) {
        if (customerRepository.existsByEmail(customer.getEmail())) {
            throw new IllegalArgumentException("An account with this email already exists.");
        }
        return customerRepository.save(customer);
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with id: " + id));
    }

    public Customer updateCustomer(Long id, Customer updatedDetails) {
        Customer existing = getCustomerById(id);
        existing.setFullName(updatedDetails.getFullName());
        existing.setPhoneNumber(updatedDetails.getPhoneNumber());
        existing.setAddress(updatedDetails.getAddress());
        // Email and password updates are handled through the shared
        // Authentication module, not here.
        return customerRepository.save(existing);
    }

    public void deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new IllegalArgumentException("Customer not found with id: " + id);
        }
        customerRepository.deleteById(id);
    }
}
