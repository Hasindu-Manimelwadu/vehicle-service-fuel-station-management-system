package com.sliit.vehicleservice.fuelinventory.config;

import com.sliit.vehicleservice.fuelinventory.entity.FuelStock;
import com.sliit.vehicleservice.fuelinventory.entity.SparePart;
import com.sliit.vehicleservice.fuelinventory.repository.FuelStockRepository;
import com.sliit.vehicleservice.fuelinventory.repository.SparePartRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@Profile("!test")
public class DatabaseSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DatabaseSeeder.class);

    private final FuelStockRepository fuelStockRepository;
    private final SparePartRepository sparePartRepository;

    public DatabaseSeeder(FuelStockRepository fuelStockRepository, SparePartRepository sparePartRepository) {
        this.fuelStockRepository = fuelStockRepository;
        this.sparePartRepository = sparePartRepository;
    }

    @Override
    public void run(String... args) {
        if (fuelStockRepository.count() == 0) {
            log.info("Database empty. Seeding initial fuel stock records...");
            fuelStockRepository.saveAll(List.of(
                    new FuelStock(null, "Petrol Octane 92", "Tank-01", 30000.0, 18500.0, 5000.0, new BigDecimal("368.00")),
                    new FuelStock(null, "Petrol Octane 95", "Tank-02", 20000.0, 4200.0, 4500.0, new BigDecimal("420.00")), // Low stock
                    new FuelStock(null, "Auto Diesel", "Tank-03", 40000.0, 26000.0, 6000.0, new BigDecimal("340.00")),
                    new FuelStock(null, "Super Diesel", "Tank-04", 25000.0, 2100.0, 3500.0, new BigDecimal("395.00")),   // Low stock
                    new FuelStock(null, "Kerosene", "Tank-05", 10000.0, 7800.0, 2000.0, new BigDecimal("260.00"))
            ));
            log.info("Fuel stock seed data populated.");
        }

        if (sparePartRepository.count() == 0) {
            log.info("Database empty. Seeding initial spare parts records...");
            sparePartRepository.saveAll(List.of(
                    new SparePart(null, "Toyota Genuine Oil Filter (90915-YZZE1)", "Filters", "AVAILABLE", 45, new BigDecimal("2450.00"), 15),
                    new SparePart(null, "Brembo Front Brake Pad Set", "Brakes", "AVAILABLE", 24, new BigDecimal("14500.00"), 10),
                    new SparePart(null, "Mobil 1 Fully Synthetic 5W-30 (4L)", "Lubricants", "AVAILABLE", 35, new BigDecimal("18200.00"), 12),
                    new SparePart(null, "Denso Iridium Spark Plug (IK20)", "Electrical", "LOW_STOCK", 8, new BigDecimal("3200.00"), 10),
                    new SparePart(null, "Bosch High Flow Air Filter", "Filters", "LOW_STOCK", 5, new BigDecimal("4800.00"), 8),
                    new SparePart(null, "Michelin Premium Wiper Blade 24\"", "Wipers", "AVAILABLE", 30, new BigDecimal("2800.00"), 10),
                    new SparePart(null, "Castrol ATF Dexron VI (1L)", "Lubricants", "AVAILABLE", 18, new BigDecimal("4500.00"), 8),
                    new SparePart(null, "Monroe Front Shock Absorber", "Suspension", "OUT_OF_STOCK", 0, new BigDecimal("22500.00"), 4)
            ));
            log.info("Spare parts seed data populated.");
        }
    }
}