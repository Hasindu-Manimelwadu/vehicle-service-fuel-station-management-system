package com.vehiclestation.booking.entity;

import com.vehiclestation.booking.enums.BookingStatus;
import com.vehiclestation.booking.enums.ServiceType;
import com.vehiclestation.schedule.entity.ServiceSchedule;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "service_bookings")
public class ServiceBooking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Long bookingId;

    /**
     * Resolved from the JWT userId claim server-side.
     * References users.user_id — never accepted from the client request body.
     */
    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    /**
     * References vehicles.id from the Customer and Vehicle Management module.
     * Stored as a plain Long to keep modules decoupled until that module is merged into develop.
     * TODO: Add @ManyToOne FK to Vehicle entity after teammate's PR is merged into develop.
     */
    @Column(name = "vehicle_id", nullable = false)
    private Long vehicleId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "schedule_id", nullable = false)
    private ServiceSchedule schedule;

    @Enumerated(EnumType.STRING)
    @Column(name = "service_type", nullable = false, length = 30)
    private ServiceType serviceType;

    /**
     * Denormalized from ServiceSchedule for efficient querying and display.
     * Populated in ServiceBookingService.createBooking() before persisting.
     */
    @Column(name = "booking_date", nullable = false)
    private LocalDate bookingDate;

    /**
     * Denormalized from ServiceSchedule.startTime.
     */
    @Column(name = "booking_time", nullable = false)
    private LocalTime bookingTime;

    @Column(name = "description", length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "booking_status", nullable = false, length = 20)
    private BookingStatus bookingStatus;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public ServiceBooking() {
    }

    public ServiceBooking(
            Long customerId,
            Long vehicleId,
            ServiceSchedule schedule,
            ServiceType serviceType,
            LocalDate bookingDate,
            LocalTime bookingTime,
            String description
    ) {
        if (customerId != null && customerId <= 0) {
            throw new IllegalArgumentException("Customer ID must be a positive number");
        }
        if (vehicleId != null && vehicleId <= 0) {
            throw new IllegalArgumentException("Vehicle ID must be a positive number");
        }
        if (schedule != null && schedule.getScheduleId() != null && schedule.getScheduleId() <= 0) {
            throw new IllegalArgumentException("Schedule ID must be a positive number");
        }
        this.customerId = customerId;
        this.vehicleId = vehicleId;
        this.schedule = schedule;
        this.serviceType = serviceType;
        this.bookingDate = bookingDate;
        this.bookingTime = bookingTime;
        this.description = description;
    }

    @PrePersist
    public void beforeInsert() {
        if (customerId != null && customerId <= 0) {
            throw new IllegalStateException("Customer ID must be a positive number");
        }
        if (vehicleId != null && vehicleId <= 0) {
            throw new IllegalStateException("Vehicle ID must be a positive number");
        }
        if (schedule != null && schedule.getScheduleId() != null && schedule.getScheduleId() <= 0) {
            throw new IllegalStateException("Schedule ID must be a positive number");
        }
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;

        if (bookingStatus == null) {
            bookingStatus = BookingStatus.CONFIRMED;
        }
    }

    @PreUpdate
    public void beforeUpdate() {
        if (customerId != null && customerId <= 0) {
            throw new IllegalStateException("Customer ID must be a positive number");
        }
        if (vehicleId != null && vehicleId <= 0) {
            throw new IllegalStateException("Vehicle ID must be a positive number");
        }
        if (schedule != null && schedule.getScheduleId() != null && schedule.getScheduleId() <= 0) {
            throw new IllegalStateException("Schedule ID must be a positive number");
        }
        updatedAt = LocalDateTime.now();
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public ServiceSchedule getSchedule() {
        return schedule;
    }

    public void setSchedule(ServiceSchedule schedule) {
        this.schedule = schedule;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate;
    }

    public LocalTime getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(LocalTime bookingTime) {
        this.bookingTime = bookingTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BookingStatus getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(BookingStatus bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
