package com.vehiclestation.servicerecord.dto;

import com.vehiclestation.servicerecord.enums.AssignmentStatus;
import com.vehiclestation.servicerecord.enums.ServiceStatus;

import java.time.LocalDateTime;
import java.util.List;

public class ServiceRecordResponse {

    private final Long serviceRecordId;
    private final Long bookingId;
    private final LocalDateTime serviceDate;
    private final String serviceDescription;
    private final String customerComplaint;
    private final Integer mileage;
    private final String workPerformed;
    private final String remarks;
    private final ServiceStatus serviceStatus;
    private final LocalDateTime startDate;
    private final LocalDateTime completionDate;
    private final Long technicianId;
    private final String technicianName;
    private final AssignmentStatus assignmentStatus;
    private final String taskDescription;
    private final List<PartUsageResponse> parts;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public ServiceRecordResponse(
            Long serviceRecordId,
            Long bookingId,
            LocalDateTime serviceDate,
            String serviceDescription,
            String customerComplaint,
            Integer mileage,
            String workPerformed,
            String remarks,
            ServiceStatus serviceStatus,
            LocalDateTime startDate,
            LocalDateTime completionDate,
            Long technicianId,
            String technicianName,
            AssignmentStatus assignmentStatus,
            String taskDescription,
            List<PartUsageResponse> parts,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.serviceRecordId = serviceRecordId;
        this.bookingId = bookingId;
        this.serviceDate = serviceDate;
        this.serviceDescription = serviceDescription;
        this.customerComplaint = customerComplaint;
        this.mileage = mileage;
        this.workPerformed = workPerformed;
        this.remarks = remarks;
        this.serviceStatus = serviceStatus;
        this.startDate = startDate;
        this.completionDate = completionDate;
        this.technicianId = technicianId;
        this.technicianName = technicianName;
        this.assignmentStatus = assignmentStatus;
        this.taskDescription = taskDescription;
        this.parts = parts;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getServiceRecordId() { return serviceRecordId; }
    public Long getBookingId() { return bookingId; }
    public LocalDateTime getServiceDate() { return serviceDate; }
    public String getServiceDescription() { return serviceDescription; }
    public String getCustomerComplaint() { return customerComplaint; }
    public Integer getMileage() { return mileage; }
    public String getWorkPerformed() { return workPerformed; }
    public String getRemarks() { return remarks; }
    public ServiceStatus getServiceStatus() { return serviceStatus; }
    public LocalDateTime getStartDate() { return startDate; }
    public LocalDateTime getCompletionDate() { return completionDate; }
    public Long getTechnicianId() { return technicianId; }
    public String getTechnicianName() { return technicianName; }
    public AssignmentStatus getAssignmentStatus() { return assignmentStatus; }
    public String getTaskDescription() { return taskDescription; }
    public List<PartUsageResponse> getParts() { return parts; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
