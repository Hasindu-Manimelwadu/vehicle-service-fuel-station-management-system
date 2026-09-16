package com.vehiclestation.servicerecord.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "service_part_usage")
public class ServicePartUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usage_id")
    private Long usageId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "service_record_id", nullable = false)
    private ServiceRecord serviceRecord;

    // Temporary integration reference until the Inventory module is merged.
    @Column(name = "part_id")
    private Long partId;

    @Column(name = "part_name", nullable = false, length = 120)
    private String partName;

    @Column(name = "quantity_used", nullable = false)
    private Integer quantityUsed;

    @Column(name = "recorded_at", nullable = false)
    private LocalDateTime recordedAt;

    public ServicePartUsage() {
    }

    @PrePersist
    public void beforeInsert() {
        if (recordedAt == null) {
            recordedAt = LocalDateTime.now();
        }
    }

    public Long getUsageId() { return usageId; }
    public ServiceRecord getServiceRecord() { return serviceRecord; }
    public void setServiceRecord(ServiceRecord serviceRecord) { this.serviceRecord = serviceRecord; }
    public Long getPartId() { return partId; }
    public void setPartId(Long partId) { this.partId = partId; }
    public String getPartName() { return partName; }
    public void setPartName(String partName) { this.partName = partName; }
    public Integer getQuantityUsed() { return quantityUsed; }
    public void setQuantityUsed(Integer quantityUsed) { this.quantityUsed = quantityUsed; }
    public LocalDateTime getRecordedAt() { return recordedAt; }
}
