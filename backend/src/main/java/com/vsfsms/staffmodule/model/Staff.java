package com.vsfsms.staffmodule.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "staff")
public class Staff {

    @Id
    @Size(max = 20, message = "Staff ID must be at most 20 characters")
    @Pattern(regexp = "^[A-Za-z0-9_-]*$", message = "Staff ID may contain only letters, digits, '-' and '_'")
    @Column(name = "staff_id", length = 20)
    private String staffId;

    @Size(max = 20, message = "User ID must be at most 20 characters")
    @Pattern(regexp = "^[A-Za-z0-9_-]*$", message = "User ID may contain only letters, digits, '-' and '_'")
    @Column(name = "user_id", length = 20)
    private String userId;

    @NotBlank(message = "Employee number is required")
    @Pattern(regexp = "^EMP-\\d{4,6}$", message = "Employee number must look like EMP-1001")
    @Column(name = "employee_no", nullable = false, unique = true, length = 20)
    private String employeeNo;

    @NotBlank(message = "Full name is required")
    @Size(min = 3, max = 100, message = "Full name must be 3-100 characters")
    @Pattern(regexp = "^[A-Za-z][A-Za-z .'-]*$", message = "Full name may contain only letters, spaces, dots, apostrophes and hyphens")
    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^(0|\\+94)\\d{9}$", message = "Phone must be a valid Sri Lankan number, e.g. 0771234567 or +94771234567")
    @Column(nullable = false, length = 20)
    private String phone;

    @NotBlank(message = "Email is required")
    @Email(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "Email must be a valid email address")
    @Size(max = 100, message = "Email must be at most 100 characters")
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @NotBlank(message = "Designation is required")
    @Size(min = 2, max = 50, message = "Designation must be 2-50 characters")
    @Column(nullable = false, length = 50)
    private String designation;

    @NotNull(message = "Date joined is required")
    @PastOrPresent(message = "Date joined cannot be in the future")
    @Column(name = "date_joined", nullable = false)
    private LocalDate dateJoined;

    @NotNull(message = "Employment status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "employment_status", nullable = false, length = 20)
    private EmploymentStatus employmentStatus = EmploymentStatus.ACTIVE;

    @NotNull(message = "Basic salary is required")
    @DecimalMin(value = "0.01", message = "Basic salary must be greater than 0")
    @DecimalMax(value = "10000000.00", message = "Basic salary cannot exceed 10,000,000")
    @Digits(integer = 10, fraction = 2, message = "Basic salary can have at most 2 decimal places")
    @Column(name = "basic_salary", nullable = false, precision = 12, scale = 2)
    private BigDecimal basicSalary;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum EmploymentStatus {
        ACTIVE, ON_LEAVE, SUSPENDED, TERMINATED
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // ---- Getters & Setters ----
    public String getStaffId() { return staffId; }
    public void setStaffId(String staffId) { this.staffId = staffId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getEmployeeNo() { return employeeNo; }
    public void setEmployeeNo(String employeeNo) { this.employeeNo = employeeNo; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

    public LocalDate getDateJoined() { return dateJoined; }
    public void setDateJoined(LocalDate dateJoined) { this.dateJoined = dateJoined; }

    public EmploymentStatus getEmploymentStatus() { return employmentStatus; }
    public void setEmploymentStatus(EmploymentStatus employmentStatus) { this.employmentStatus = employmentStatus; }

    public BigDecimal getBasicSalary() { return basicSalary; }
    public void setBasicSalary(BigDecimal basicSalary) { this.basicSalary = basicSalary; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
