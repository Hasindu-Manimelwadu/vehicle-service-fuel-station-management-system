package com.sliit.vehiclemgmt.service;

import com.sliit.vehiclemgmt.dto.*;
import com.sliit.vehiclemgmt.entity.Customer;
import com.sliit.vehiclemgmt.entity.Role;
import com.sliit.vehiclemgmt.exception.BadRequestException;
import com.sliit.vehiclemgmt.exception.ConflictException;
import com.sliit.vehiclemgmt.exception.ResourceNotFoundException;
import com.sliit.vehiclemgmt.exception.UnauthorizedException;
import com.sliit.vehiclemgmt.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service class for Customer Management operations.
 * Implements business logic for customer registration, authentication, profile updates, and staff viewing.
 */
@Service
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public CustomerService(CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Self-registration of a new customer with BCrypt password hashing.
     */
    public CustomerResponseDTO register(CustomerRegisterDTO dto) {
        // Check for duplicate email
        if (customerRepository.existsByEmail(dto.getEmail().trim().toLowerCase())) {
            throw new ConflictException("An account with email '" + dto.getEmail() + "' already exists.");
        }

        // Check for duplicate username
        if (customerRepository.existsByUsername(dto.getUsername().trim())) {
            throw new ConflictException("Username '" + dto.getUsername() + "' is already taken.");
        }

        // Hash the password securely with BCrypt
        String hashedPassword = passwordEncoder.encode(dto.getPassword());

        Customer customer = new Customer(
                dto.getFullName().trim(),
                dto.getEmail().trim().toLowerCase(),
                dto.getPhone().trim(),
                dto.getAddress().trim(),
                dto.getNicNumber().trim().toUpperCase(),
                dto.getUsername().trim(),
                hashedPassword,
                Role.CUSTOMER
        );

        Customer saved = customerRepository.save(customer);
        return CustomerResponseDTO.fromEntity(saved);
    }

    /**
     * Customer & Staff Login Authentication.
     */
    @Transactional(readOnly = true)
    public CustomerLoginResponseDTO login(CustomerLoginDTO dto) {
        String identifier = dto.getUsername().trim();
        Customer customer = customerRepository.findByUsernameOrEmail(identifier, identifier.toLowerCase())
                .orElseThrow(() -> new UnauthorizedException("Invalid username/email or password."));

        // Verify password against BCrypt hash
        if (!passwordEncoder.matches(dto.getPassword(), customer.getPasswordHash())) {
            throw new UnauthorizedException("Invalid username/email or password.");
        }

        // Generate a lightweight session token for API verification
        String token = "TOKEN-" + UUID.randomUUID().toString();

        return new CustomerLoginResponseDTO(
                customer.getCustomerId(),
                customer.getFullName(),
                customer.getEmail(),
                customer.getUsername(),
                customer.getRole(),
                token
        );
    }

    /**
     * Fetch customer profile by Customer ID.
     */
    @Transactional(readOnly = true)
    public CustomerResponseDTO getCustomerById(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + customerId));
        return CustomerResponseDTO.fromEntity(customer);
    }

    /**
     * Update customer personal details and optionally password.
     */
    public CustomerResponseDTO updateCustomer(Long customerId, CustomerUpdateDTO dto) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + customerId));

        // If email is changed, check for conflict
        String newEmail = dto.getEmail().trim().toLowerCase();
        if (!customer.getEmail().equalsIgnoreCase(newEmail) && customerRepository.existsByEmail(newEmail)) {
            throw new ConflictException("Email '" + newEmail + "' is already in use by another account.");
        }

        customer.setFullName(dto.getFullName().trim());
        customer.setEmail(newEmail);
        customer.setPhone(dto.getPhone().trim());
        customer.setAddress(dto.getAddress().trim());
        customer.setNicNumber(dto.getNicNumber().trim().toUpperCase());

        // Update password if provided
        if (dto.getNewPassword() != null && !dto.getNewPassword().trim().isEmpty()) {
            if (dto.getNewPassword().trim().length() < 6) {
                throw new BadRequestException("New password must be at least 6 characters long.");
            }
            customer.setPasswordHash(passwordEncoder.encode(dto.getNewPassword().trim()));
        }

        Customer updated = customerRepository.save(customer);
        return CustomerResponseDTO.fromEntity(updated);
    }

    /**
     * Staff / Admin View: Search and list customers.
     */
    @Transactional(readOnly = true)
    public List<CustomerResponseDTO> getAllCustomers(String query, String userRole) {
        // Enforce basic role-based access check
        if (userRole == null || (!userRole.equalsIgnoreCase("STAFF") && !userRole.equalsIgnoreCase("ADMIN"))) {
            throw new UnauthorizedException("Access Denied: Only Staff or Administrator can access customer directory.");
        }

        List<Customer> customers;
        if (query != null && !query.trim().isEmpty()) {
            customers = customerRepository.searchCustomers(query.trim());
        } else {
            customers = customerRepository.findAll();
        }

        return customers.stream()
                .map(CustomerResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
}
