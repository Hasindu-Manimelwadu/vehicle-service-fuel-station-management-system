package com.vehiclestation.servicerecord.entity;

import com.vehiclestation.auth.entity.User;
import com.vehiclestation.servicerecord.enums.AssignmentStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "technician_assignments",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_assignment_service_record",
                columnNames = "service_record_id"
        )
)
public class TechnicianAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "assignment_id")
    private Long assignmentId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "service_record_id", nullable = false)
    private ServiceRecord serviceRecord;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "technician_id", nullable = false)
    private User technician;

    @Column(name = "assigned_date", nullable = false)
    private LocalDateTime assignedDate;

    @Column(name = "task_description", nullable = false, length = 500)
    private String taskDescription;

    @Column(name = "completed_date")
    private LocalDateTime completedDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "assignment_status", nullable = false, length = 20)
    private AssignmentStatus assignmentStatus;

    public TechnicianAssignment() {
    }

    @PrePersist
    public void beforeInsert() {
        if (assignedDate == null) {
            assignedDate = LocalDateTime.now();
        }
        if (assignmentStatus == null) {
            assignmentStatus = AssignmentStatus.ASSIGNED;
        }
    }

    public Long getAssignmentId() { return assignmentId; }
    public ServiceRecord getServiceRecord() { return serviceRecord; }
    public void setServiceRecord(ServiceRecord serviceRecord) { this.serviceRecord = serviceRecord; }
    public User getTechnician() { return technician; }
    public void setTechnician(User technician) { this.technician = technician; }
    public LocalDateTime getAssignedDate() { return assignedDate; }
    public void setAssignedDate(LocalDateTime assignedDate) { this.assignedDate = assignedDate; }
    public String getTaskDescription() { return taskDescription; }
    public void setTaskDescription(String taskDescription) { this.taskDescription = taskDescription; }
    public LocalDateTime getCompletedDate() { return completedDate; }
    public void setCompletedDate(LocalDateTime completedDate) { this.completedDate = completedDate; }
    public AssignmentStatus getAssignmentStatus() { return assignmentStatus; }
    public void setAssignmentStatus(AssignmentStatus assignmentStatus) { this.assignmentStatus = assignmentStatus; }
}
