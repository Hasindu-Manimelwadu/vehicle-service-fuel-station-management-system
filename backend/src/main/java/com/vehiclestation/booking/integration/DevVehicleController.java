package com.vehiclestation.booking.integration;

import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

/**
 * Development-only vehicle endpoint providing mock vehicles for the authenticated customer.
 * Restricts access to the authenticated user and provides a secure GET /api/vehicles/my endpoint.
 */
@RestController
@RequestMapping("/api/vehicles")
@Profile("dev")
public class DevVehicleController {

    /**
     * Secure endpoint returning demo vehicles for the authenticated customer.
     */
    @GetMapping("/my")
    public ResponseEntity<List<Map<String, Object>>> getMyVehicles(
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long customerId = extractCustomerId(jwt);
        return ResponseEntity.ok(getDemoVehicles(customerId));
    }

    /**
     * Legacy customer ID endpoint with verified JWT ownership check.
     * Prevents arbitrary customer ID tampering.
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Map<String, Object>>> getVehiclesForCustomer(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long customerId
    ) {
        Long authenticatedCustomerId = extractCustomerId(jwt);
        if (!authenticatedCustomerId.equals(customerId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Access denied: cannot access vehicles belonging to another customer"
            );
        }
        return ResponseEntity.ok(getDemoVehicles(customerId));
    }

    private List<Map<String, Object>> getDemoVehicles(Long customerId) {
        return List.of(
                vehicle(1L, "CAB-1234", "Toyota", "Corolla", 2021, "Silver"),
                vehicle(2L, "WP-5678", "Honda", "Civic", 2022, "Black"),
                vehicle(3L, "NW-9988", "Nissan", "Leaf", 2020, "Blue")
        );
    }

    private Long extractCustomerId(Jwt jwt) {
        if (jwt == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication credentials are required");
        }
        Object claim = jwt.getClaim("userId");
        if (claim instanceof Number number) {
            return number.longValue();
        }
        if (claim != null) {
            try {
                return Long.parseLong(claim.toString());
            } catch (NumberFormatException e) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid user ID in token");
            }
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User ID claim not found in JWT token");
    }

    private Map<String, Object> vehicle(
            Long id,
            String licensePlateNumber,
            String make,
            String model,
            Integer year,
            String color
    ) {
        return Map.of(
                "id", id,
                "licensePlateNumber", licensePlateNumber,
                "make", make,
                "model", model,
                "year", year,
                "color", color
        );
    }
}
