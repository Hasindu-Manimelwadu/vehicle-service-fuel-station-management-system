package com.vehiclestation.servicerecord.dto;

import java.time.LocalDateTime;

public class PartUsageResponse {

    private final Long usageId;
    private final Long partId;
    private final String partName;
    private final Integer quantityUsed;
    private final LocalDateTime recordedAt;

    public PartUsageResponse(Long usageId, Long partId, String partName, Integer quantityUsed, LocalDateTime recordedAt) {
        this.usageId = usageId;
        this.partId = partId;
        this.partName = partName;
        this.quantityUsed = quantityUsed;
        this.recordedAt = recordedAt;
    }

    public Long getUsageId() { return usageId; }
    public Long getPartId() { return partId; }
    public String getPartName() { return partName; }
    public Integer getQuantityUsed() { return quantityUsed; }
    public LocalDateTime getRecordedAt() { return recordedAt; }
}
