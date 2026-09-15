package com.vehiclestation.auth.dto;

import com.vehiclestation.auth.enums.AccountStatus;
import com.vehiclestation.auth.enums.Role;

import java.time.LocalDateTime;

public class RegisterResponse {

    private Long userId;
    private String fullName;
    private String email;
    private String phone;
    private Role role;
    private AccountStatus accountStatus;
    private LocalDateTime createdAt;
    private String message;

    public RegisterResponse(
            Long userId,
            String fullName,
            String email,
            String phone,
            Role role,
            AccountStatus accountStatus,
            LocalDateTime createdAt,
            String message
    ) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.accountStatus = accountStatus;
        this.createdAt = createdAt;
        this.message = message;
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

    public String getPhone() {
        return phone;
    }

    public Role getRole() {
        return role;
    }

    public AccountStatus getAccountStatus() {
        return accountStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getMessage() {
        return message;
    }
}