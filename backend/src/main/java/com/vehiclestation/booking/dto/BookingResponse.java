package com.vehiclestation.booking.dto;

import com.vehiclestation.booking.enums.BookingStatus;
import com.vehiclestation.booking.enums.ServiceType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class BookingResponse {

    private Long bookingId;
    private Long customerId;
    private Long vehicleId;
    private Long scheduleId;
    private ServiceType serviceType;
    private LocalDate bookingDate;
    private LocalTime bookingTime;
    private String description;
    private BookingStatus bookingStatus;
    private LocalDateTime createdAt;

    public BookingResponse() {
    }

    public BookingResponse(
            Long bookingId,
            Long customerId,
            Long vehicleId,
            Long scheduleId,
            ServiceType serviceType,
            LocalDate bookingDate,
            LocalTime bookingTime,
            String description,
            BookingStatus bookingStatus,
            LocalDateTime createdAt
    ) {
        this.bookingId = bookingId;
        this.customerId = customerId;
        this.vehicleId = vehicleId;
        this.scheduleId = scheduleId;
        this.serviceType = serviceType;
        this.bookingDate = bookingDate;
        this.bookingTime = bookingTime;
        this.description = description;
        this.bookingStatus = bookingStatus;
        this.createdAt = createdAt;
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

    public Long getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Long scheduleId) {
        this.scheduleId = scheduleId;
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

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
