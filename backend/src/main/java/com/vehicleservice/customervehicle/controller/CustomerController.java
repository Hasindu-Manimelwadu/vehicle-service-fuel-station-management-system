package com.sliit.vehiclemgmt.controller;

import com.sliit.vehiclemgmt.dto.*;
import com.sliit.vehiclemgmt.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Customer Management endpoints.
 * Handles self-registration, authentication, profile inspection/updates, and staff customer listing.
 */
@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    /**
     * Customer Self-Registration
     * POST /api/customers/register
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<CustomerResponseDTO>> registerCustomer(@Valid @RequestBody CustomerRegisterDTO dto) {
        CustomerResponseDTO response = customerService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Customer registered successfully.", response));
    }

    /**
     * Customer & Staff Login
     * POST /api/customers/login
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<CustomerLoginResponseDTO>> login(@Valid @RequestBody CustomerLoginDTO dto) {
        CustomerLoginResponseDTO response = customerService.login(dto);
        return ResponseEntity.ok(ApiResponse.success("Login successful.", response));
    }

    /**
     * View Customer Profile by ID
     * GET /api/customers/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CustomerResponseDTO>> getCustomerProfile(@PathVariable("id") Long id) {
        CustomerResponseDTO response = customerService.getCustomerById(id);
        return ResponseEntity.ok(ApiResponse.success("Profile retrieved successfully.", response));
    }

    /**
     * Update Customer Profile by ID
     * PUT /api/customers/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CustomerResponseDTO>> updateCustomerProfile(
            @PathVariable("id") Long id,
            @Valid @RequestBody CustomerUpdateDTO dto) {
        CustomerResponseDTO response = customerService.updateCustomer(id, dto);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully.", response));
    }

    /**
     * Staff & Admin View: Search & list all customers
     * GET /api/customers
     * Accessible by Staff and Admin users.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<CustomerResponseDTO>>> listCustomers(
            @RequestParam(name = "query", required = false) String query,
            @RequestHeader(name = "X-User-Role", required = false, defaultValue = "STAFF") String roleHeader,
            @RequestParam(name = "role", required = false) String roleParam) {

        // Support role passed via header (standard) or query parameter for easy testing in browser
        String effectiveRole = (roleParam != null && !roleParam.isEmpty()) ? roleParam : roleHeader;

        List<CustomerResponseDTO> customers = customerService.getAllCustomers(query, effectiveRole);
        return ResponseEntity.ok(ApiResponse.success("Customers retrieved successfully.", customers));
    }
}
