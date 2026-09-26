package com.sliit.vehicleservice.fuelinventory.dto;

public class DashboardSummaryDTO {

    private long totalFuelTypes;
    private long totalSpareParts;
    private long lowStockFuelCount;
    private long lowStockSparePartsCount;
    private double totalFuelCapacityLiters;
    private double totalFuelCurrentLiters;
    private int totalSparePartsUnits;

    public DashboardSummaryDTO() {}

    public DashboardSummaryDTO(long totalFuelTypes, long totalSpareParts,
                               long lowStockFuelCount, long lowStockSparePartsCount,
                               double totalFuelCapacityLiters, double totalFuelCurrentLiters,
                               int totalSparePartsUnits) {
        this.totalFuelTypes = totalFuelTypes;
        this.totalSpareParts = totalSpareParts;
        this.lowStockFuelCount = lowStockFuelCount;
        this.lowStockSparePartsCount = lowStockSparePartsCount;
        this.totalFuelCapacityLiters = totalFuelCapacityLiters;
        this.totalFuelCurrentLiters = totalFuelCurrentLiters;
        this.totalSparePartsUnits = totalSparePartsUnits;
    }

    public long getTotalFuelTypes() { return totalFuelTypes; }
    public void setTotalFuelTypes(long totalFuelTypes) { this.totalFuelTypes = totalFuelTypes; }

    public long getTotalSpareParts() { return totalSpareParts; }
    public void setTotalSpareParts(long totalSpareParts) { this.totalSpareParts = totalSpareParts; }

    public long getLowStockFuelCount() { return lowStockFuelCount; }
    public void setLowStockFuelCount(long lowStockFuelCount) { this.lowStockFuelCount = lowStockFuelCount; }

    public long getLowStockSparePartsCount() { return lowStockSparePartsCount; }
    public void setLowStockSparePartsCount(long lowStockSparePartsCount) { this.lowStockSparePartsCount = lowStockSparePartsCount; }

    public double getTotalFuelCapacityLiters() { return totalFuelCapacityLiters; }
    public void setTotalFuelCapacityLiters(double totalFuelCapacityLiters) { this.totalFuelCapacityLiters = totalFuelCapacityLiters; }

    public double getTotalFuelCurrentLiters() { return totalFuelCurrentLiters; }
    public void setTotalFuelCurrentLiters(double totalFuelCurrentLiters) { this.totalFuelCurrentLiters = totalFuelCurrentLiters; }

    public int getTotalSparePartsUnits() { return totalSparePartsUnits; }
    public void setTotalSparePartsUnits(int totalSparePartsUnits) { this.totalSparePartsUnits = totalSparePartsUnits; }
}