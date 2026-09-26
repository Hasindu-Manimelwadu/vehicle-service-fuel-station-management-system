package com.vehiclestation.booking.controller;

import com.vehiclestation.booking.dto.BookingRequest;
import com.vehiclestation.booking.dto.BookingResponse;
import com.vehiclestation.booking.service.ServiceBookingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class ServiceBookingController {

    private final ServiceBookingService bookingService;

    public ServiceBookingController(ServiceBookingService bookingService) {
        this.bookingService = bookingService;
    }

    /**
     * Creates a new service booking directly as CONFIRMED.
     * Extracts customerId securely from the validated JWT token claim.
     */
    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody BookingRequest request
    ) {
        Long customerId = extractCustomerId(jwt);
        BookingResponse response = bookingService.createBooking(customerId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * Retrieves all confirmed bookings for the authenticated customer.
     */
    @GetMapping("/my")
    public ResponseEntity<List<BookingResponse>> getMyBookings(
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long customerId = extractCustomerId(jwt);
        List<BookingResponse> responses = bookingService.getMyBookings(customerId);

        return ResponseEntity.ok(responses);
    }

    /**
     * Retrieves a single booking by ID for the authenticated customer.
     */
    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingResponse> getBookingById(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long bookingId
    ) {
        Long customerId = extractCustomerId(jwt);
        BookingResponse response = bookingService.getBookingById(bookingId, customerId);

        return ResponseEntity.ok(response);
    }

    /** Updates a booking owned by the authenticated customer. */
    @PutMapping("/{bookingId}")
    public ResponseEntity<BookingResponse> updateBooking(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long bookingId,
            @Valid @RequestBody BookingRequest request
    ) {
        Long customerId = extractCustomerId(jwt);
        return ResponseEntity.ok(bookingService.updateBooking(bookingId, customerId, request));
    }

    /** Permanently deletes a booking owned by the authenticated customer. */
    @DeleteMapping("/{bookingId}")
    public ResponseEntity<Void> deleteBooking(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long bookingId
    ) {
        Long customerId = extractCustomerId(jwt);
        bookingService.deleteBooking(bookingId, customerId);
        return ResponseEntity.noContent().build();
    }

    private Long extractCustomerId(Jwt jwt) {
        if (jwt == null) {
            throw new IllegalArgumentException("Authentication credentials are required");
        }
        Object claim = jwt.getClaim("userId");
        if (claim instanceof Number number) {
            return number.longValue();
        }
        if (claim != null) {
            return Long.parseLong(claim.toString());
        }
        throw new IllegalArgumentException("User ID not found in JWT token claims");
    }
}
