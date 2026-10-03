package com.vsfsms.staffmodule.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "salary", uniqueConstraints = @UniqueConstraint(columnNames = {"staff_id", "month"}))
public class Salary {

    @Id
    @Size(max = 20, message = "Salary ID must be at most 20 characters")
    @Pattern(regexp = "^[A-Za-z0-9_-]*$", message = "Salary ID may contain only letters, digits, '-' and '_'")
    @Column(name = "salary_id", length = 20)
    private String salaryId;

    @NotBlank(message = "Staff member is required")
    @Size(max = 20, message = "Staff ID must be at most 20 characters")
    @Column(name = "staff_id", nullable = false, length = 20)
    private String staffId;

    @NotBlank(message = "Month is required")
    @Pattern(regexp = "^\\d{4}-(0[1-9]|1[0-2])$", message = "Month must be in YYYY-MM format, e.g. 2026-09")
    @Column(nullable = false, length = 7)
    private String month;

    @NotNull(message = "Basic salary is required")
    @DecimalMin(value = "0.01", message = "Basic salary must be greater than 0")
    @DecimalMax(value = "10000000.00", message = "Basic salary cannot exceed 10,000,000")
    @Digits(integer = 10, fraction = 2, message = "Basic salary can have at most 2 decimal places")
    @Column(name = "basic_salary", nullable = false, precision = 12, scale = 2)
    private BigDecimal basicSalary;

    // Overtime is calculated from the staff member's attendance for the month
    // (see SalaryService / payroll.* in application.properties). Values sent
    // by the client are ignored.
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(name = "overtime_hours", precision = 6, scale = 2)
    private BigDecimal overtimeHours = BigDecimal.ZERO;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(name = "overtime_pay", nullable = false, precision = 12, scale = 2)
    private BigDecimal overtimePay = BigDecimal.ZERO;

    @DecimalMin(value = "0.00", message = "Deductions cannot be negative")
    @Digits(integer = 10, fraction = 2, message = "Deductions can have at most 2 decimal places")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal deductions = BigDecimal.ZERO;

    // Always calculated server-side (basic + overtime - deductions); any value
    // sent by the client is ignored. (Previously @NotNull here made every
    // create from the admin panel fail, because the form never sends it.)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(name = "net_salary", nullable = false, precision = 12, scale = 2)
    private BigDecimal netSalary;

    @Column(name = "payment_date")
    private LocalDate paymentDate;

    @NotNull(message = "Payment status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 20)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    public enum PaymentStatus {
        PENDING, PAID, FAILED
    }

    // ---- Getters & Setters ----
    public String getSalaryId() { return salaryId; }
    public void setSalaryId(String salaryId) { this.salaryId = salaryId; }

    public String getStaffId() { return staffId; }
    public void setStaffId(String staffId) { this.staffId = staffId; }

    public String getMonth() { return month; }
    public void setMonth(String month) { this.month = month; }

    public BigDecimal getBasicSalary() { return basicSalary; }
    public void setBasicSalary(BigDecimal basicSalary) { this.basicSalary = basicSalary; }

    public BigDecimal getOvertimeHours() { return overtimeHours; }
    public void setOvertimeHours(BigDecimal overtimeHours) { this.overtimeHours = overtimeHours; }

    public BigDecimal getOvertimePay() { return overtimePay; }
    public void setOvertimePay(BigDecimal overtimePay) { this.overtimePay = overtimePay; }

    public BigDecimal getDeductions() { return deductions; }
    public void setDeductions(BigDecimal deductions) { this.deductions = deductions; }

    public BigDecimal getNetSalary() { return netSalary; }
    public void setNetSalary(BigDecimal netSalary) { this.netSalary = netSalary; }

    public LocalDate getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDate paymentDate) { this.paymentDate = paymentDate; }

    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }
}
