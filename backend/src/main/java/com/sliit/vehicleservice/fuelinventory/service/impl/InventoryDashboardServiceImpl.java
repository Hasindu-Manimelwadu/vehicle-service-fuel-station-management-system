package com.sliit.vehicleservice.fuelinventory.service.impl;

import com.sliit.vehicleservice.fuelinventory.dto.DashboardSummaryDTO;
import com.sliit.vehicleservice.fuelinventory.repository.FuelStockRepository;
import com.sliit.vehicleservice.fuelinventory.repository.SparePartRepository;
import com.sliit.vehicleservice.fuelinventory.service.InventoryDashboardService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class InventoryDashboardServiceImpl implements InventoryDashboardService {

    private final FuelStockRepository fuelStockRepository;
    private final SparePartRepository sparePartRepository;

    public InventoryDashboardServiceImpl(FuelStockRepository fuelStockRepository,
                                         SparePartRepository sparePartRepository) {
        this.fuelStockRepository = fuelStockRepository;
        this.sparePartRepository = sparePartRepository;
    }

    @Override
    public DashboardSummaryDTO getDashboardSummary() {
        long totalFuel = fuelStockRepository.count();
        long totalParts = sparePartRepository.count();
        long lowStockFuel = fuelStockRepository.countLowStockFuel();
        long lowStockParts = sparePartRepository.countLowStockSpareParts();

        Double totalCap = fuelStockRepository.sumTotalCapacity();
        Double totalCurrent = fuelStockRepository.sumTotalCurrentQuantity();
        Integer totalPartUnits = sparePartRepository.sumTotalQuantity();

        return new DashboardSummaryDTO(
                totalFuel,
                totalParts,
                lowStockFuel,
                lowStockParts,
                totalCap != null ? totalCap : 0.0,
                totalCurrent != null ? totalCurrent : 0.0,
                totalPartUnits != null ? totalPartUnits : 0
        );
    }
}