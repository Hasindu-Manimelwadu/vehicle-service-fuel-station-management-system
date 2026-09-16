package com.vehiclestation.servicerecord.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdateServiceProgressRequest {

    @NotBlank(message = "Work performed is required")
    @Size(max = 5000, message = "Work performed cannot exceed 5000 characters")
    private String workPerformed;

    @Size(max = 1000, message = "Remarks cannot exceed 1000 characters")
    private String remarks;

    public String getWorkPerformed() { return workPerformed; }
    public void setWorkPerformed(String workPerformed) { this.workPerformed = workPerformed; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}
