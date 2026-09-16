-- =============================================================
-- Sample realistic seed data for testing
-- =============================================================
USE vehicle_service_db;

-- Clear previous test data
DELETE FROM `fuel_stock`;
DELETE FROM `spare_part`;

-- Fuel Stock Seed Records
INSERT INTO `fuel_stock` (`fuel_stock_id`, `fuel_type`, `tankNo`, `tank_capacity`, `current_quantity`, `reorder_level`, `unit_price`) VALUES
(1, 'Petrol Octane 92', 'Tank-01', 30000.0, 18500.0, 5000.0, 368.00),
(2, 'Petrol Octane 95', 'Tank-02', 20000.0, 4200.0, 4500.0, 420.00), -- Low Stock trigger
(3, 'Auto Diesel', 'Tank-03', 40000.0, 26000.0, 6000.0, 340.00),
(4, 'Super Diesel', 'Tank-04', 25000.0, 2100.0, 3500.0, 395.00),  -- Low Stock trigger
(5, 'Kerosene', 'Tank-05', 10000.0, 7800.0, 2000.0, 260.00);

-- Spare Parts Seed Records
INSERT INTO `spare_part` (`part_id`, `part_name`, `category`, `status`, `quantity`, `unit_price`, `reorder_level`) VALUES
(1, 'Toyota Genuine Oil Filter (90915-YZZE1)', 'Filters', 'AVAILABLE', 45, 2450.00, 15),
(2, 'Brembo Front Brake Pad Set', 'Brakes', 'AVAILABLE', 24, 14500.00, 10),
(3, 'Mobil 1 Fully Synthetic 5W-30 (4L)', 'Lubricants', 'AVAILABLE', 35, 18200.00, 12),
(4, 'Denso Iridium Spark Plug (IK20)', 'Electrical', 'LOW_STOCK', 8, 3200.00, 10), -- Low Stock trigger
(5, 'Bosch High Flow Air Filter', 'Filters', 'LOW_STOCK', 5, 4800.00, 8),            -- Low Stock trigger
(6, 'Michelin Premium Wiper Blade 24"', 'Wipers', 'AVAILABLE', 30, 2800.00, 10),
(7, 'Castrol ATF Dexron VI Transmission Fluid (1L)', 'Lubricants', 'AVAILABLE', 18, 4500.00, 8),
(8, 'Monroe Front Shock Absorber', 'Suspension', 'OUT_OF_STOCK', 0, 22500.00, 4);   -- Low Stock / Out of stock trigger