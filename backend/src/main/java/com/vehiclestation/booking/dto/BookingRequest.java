package com.vehiclestation.booking.dto;

import com.vehiclestation.booking.enums.ServiceType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class BookingRequest {

    @NotNull(message = "Vehicle ID is required")
    @Positive(message = "Vehicle ID must be a positive number")
    private Long vehicleId;

    @NotNull(message = "Schedule ID is required")
    @Positive(message = "Schedule ID must be a positive number")
    private Long scheduleId;

    @NotNull(message = "Service type is required")
    private ServiceType serviceType;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    public BookingRequest() {
    }

    public BookingRequest(Long vehicleId, Long scheduleId, ServiceType serviceType, String description) {
        this.vehicleId = vehicleId;
        this.scheduleId = scheduleId;
        this.serviceType = serviceType;
        this.description = description;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
