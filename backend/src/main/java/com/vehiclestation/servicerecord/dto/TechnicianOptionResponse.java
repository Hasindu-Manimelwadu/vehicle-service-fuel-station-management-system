package com.vehiclestation.servicerecord.dto;

public class TechnicianOptionResponse {

    private final Long userId;
    private final String fullName;
    private final String email;

    public TechnicianOptionResponse(Long userId, String fullName, String email) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
    }

    public Long getUserId() {
        return userId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }
}
