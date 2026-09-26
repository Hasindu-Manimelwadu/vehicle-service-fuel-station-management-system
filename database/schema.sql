CREATE DATABASE IF NOT EXISTS vehicle_service_db;
USE vehicle_service_db;


CREATE TABLE IF NOT EXISTS `fuel_stock` (
    `fuel_stock_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `fuel_type` VARCHAR(100) NOT NULL,
    `tank_no` VARCHAR(50) NOT NULL UNIQUE,
    `tank_capacity` DOUBLE NOT NULL,
    `current_quantity` DOUBLE NOT NULL,
    `reorder_level` DOUBLE NOT NULL,
    `unit_price` DECIMAL(10,2) NOT NULL,
    CONSTRAINT `chk_fuel_capacity` CHECK (`tank_capacity` > 0),
    CONSTRAINT `chk_fuel_current_qty` CHECK (`current_quantity` >= 0),
    CONSTRAINT `chk_fuel_reorder_lvl` CHECK (`reorder_level` >= 0),
    CONSTRAINT `chk_fuel_unit_price` CHECK (`unit_price` >= 0.00)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE IF NOT EXISTS `spare_part` (
    `part_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `part_name` VARCHAR(150) NOT NULL,
    `category` VARCHAR(100) NOT NULL,
    `status` VARCHAR(50) NOT NULL,
    `quantity` INT NOT NULL,
    `unit_price` DECIMAL(10,2) NOT NULL,
    `reorder_level` INT NOT NULL,
    CONSTRAINT `chk_part_qty` CHECK (`quantity` >= 0),
    CONSTRAINT `chk_part_unit_price` CHECK (`unit_price` >= 0.00),
    CONSTRAINT `chk_part_reorder_lvl` CHECK (`reorder_level` >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
