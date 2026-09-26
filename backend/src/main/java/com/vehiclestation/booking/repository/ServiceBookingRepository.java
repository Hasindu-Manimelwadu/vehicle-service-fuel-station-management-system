package com.vehiclestation.booking.repository;

import com.vehiclestation.booking.entity.ServiceBooking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ServiceBookingRepository extends JpaRepository<ServiceBooking, Long> {

    /**
     * Returns all bookings for the given customer, newest first.
     */
    List<ServiceBooking> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

    /**
     * Returns a booking only when it belongs to the specified customer.
     * Used to enforce ownership before reading, updating, or deleting.
     */
    Optional<ServiceBooking> findByBookingIdAndCustomerId(Long bookingId, Long customerId);
}
