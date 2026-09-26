-- ==============================================================================
-- Vehicle Service & Fuel Station Management System (SLIIT SE2030)
-- Module: Customer & Vehicle Management
-- Database Schema & Sample Data for MySQL
-- ==============================================================================

CREATE DATABASE IF NOT EXISTS `vehicle_management_db` 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE `vehicle_management_db`;

-- ------------------------------------------------------------------------------
-- 1. Table: customers
-- Stores registered customer, staff, and admin credentials & profiles
-- ------------------------------------------------------------------------------
DROP TABLE IF EXISTS `service_records`;
DROP TABLE IF EXISTS `vehicles`;
DROP TABLE IF EXISTS `customers`;

CREATE TABLE `customers` (
    `customer_id` INT AUTO_INCREMENT PRIMARY KEY,
    `full_name` VARCHAR(100) NOT NULL,
    `email` VARCHAR(100) NOT NULL UNIQUE,
    `phone` VARCHAR(20) NOT NULL,
    `address` VARCHAR(255) NOT NULL,
    `nic_number` VARCHAR(20) NOT NULL,
    `username` VARCHAR(50) NOT NULL UNIQUE,
    `password_hash` VARCHAR(255) NOT NULL,
    `role` VARCHAR(20) NOT NULL DEFAULT 'CUSTOMER',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT `chk_role` CHECK (`role` IN ('CUSTOMER', 'STAFF', 'ADMIN'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX `idx_customers_email` ON `customers` (`email`);
CREATE INDEX `idx_customers_username` ON `customers` (`username`);

-- ------------------------------------------------------------------------------
-- 2. Table: vehicles
-- Stores customer vehicle details
-- ------------------------------------------------------------------------------
CREATE TABLE `vehicles` (
    `vehicle_id` INT AUTO_INCREMENT PRIMARY KEY,
    `customer_id` INT NOT NULL,
    `plate_number` VARCHAR(20) NOT NULL UNIQUE,
    `make` VARCHAR(50) NOT NULL,
    `model` VARCHAR(50) NOT NULL,
    `year` INT NOT NULL,
    `vehicle_type` VARCHAR(30) NOT NULL,
    `fuel_type` VARCHAR(30) NOT NULL,
    `color` VARCHAR(30) NOT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT `fk_vehicles_customer` 
        FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`) 
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX `idx_vehicles_plate` ON `vehicles` (`plate_number`);
CREATE INDEX `idx_vehicles_customer_id` ON `vehicles` (`customer_id`);

-- ------------------------------------------------------------------------------
-- 3. Table: service_records (Placeholder for Service Records Module)
-- Read-only placeholder so customer vehicle service history can be queried
-- ------------------------------------------------------------------------------
CREATE TABLE `service_records` (
    `service_id` INT AUTO_INCREMENT PRIMARY KEY,
    `vehicle_id` INT NOT NULL,
    `service_date` DATE NOT NULL,
    `description` VARCHAR(255) NOT NULL,
    `cost` DECIMAL(10, 2) NOT NULL,
    `status` VARCHAR(30) NOT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `fk_services_vehicle` 
        FOREIGN KEY (`vehicle_id`) REFERENCES `vehicles` (`vehicle_id`) 
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX `idx_service_vehicle_id` ON `service_records` (`vehicle_id`);

-- ------------------------------------------------------------------------------
-- SAMPLE SEED DATA
-- Default Passwords for all demo accounts: 'Password123!'
-- ------------------------------------------------------------------------------

-- Seed Customers, Staff, and Admin
INSERT INTO `customers` (`customer_id`, `full_name`, `email`, `phone`, `address`, `nic_number`, `username`, `password_hash`, `role`) VALUES
(1, 'Kasun Perera', 'kasun@gmail.com', '0771234567', 'No. 45, Temple Road, Colombo 03', '199512345678', 'kasun', '$2a$10$7Ltmg8UVwze5UVnLtH6.aeK9Pzjw5cvWd0JTLw09EvqmtSTiFblmq', 'CUSTOMER'),
(2, 'Nimali Fernando', 'nimali@yahoo.com', '0719876543', '12/A, Galle Road, Moratuwa', '199887654321', 'nimali', '$2a$10$7Ltmg8UVwze5UVnLtH6.aeK9Pzjw5cvWd0JTLw09EvqmtSTiFblmq', 'CUSTOMER'),
(3, 'Sunil Jayasinghe (Staff)', 'staff@autocare.lk', '0761122334', 'AutoCare Station, Kandy Road, Kadawatha', '198822334455', 'staff', '$2a$10$7Ltmg8UVwze5UVnLtH6.aeK9Pzjw5cvWd0JTLw09EvqmtSTiFblmq', 'STAFF'),
(4, 'Admin Manager', 'admin@autocare.lk', '0779988776', 'Headquarters, Nawala, Rajagiriya', '198255667788', 'admin', '$2a$10$7Ltmg8UVwze5UVnLtH6.aeK9Pzjw5cvWd0JTLw09EvqmtSTiFblmq', 'ADMIN');

-- Seed Vehicles for Customers
INSERT INTO `vehicles` (`vehicle_id`, `customer_id`, `plate_number`, `make`, `model`, `year`, `vehicle_type`, `fuel_type`, `color`) VALUES
(1, 1, 'CAB-1234', 'Toyota', 'Prius', 2018, 'Sedan', 'Hybrid (Petrol)', 'Pearl White'),
(2, 1, 'WP-CAA-5678', 'Honda', 'Vessel', 2020, 'SUV', 'Hybrid (Petrol)', 'Metallic Blue'),
(3, 2, 'SP-KQ-4321', 'Suzuki', 'Wagon R', 2017, 'Hatchback', 'Petrol 95', 'Silver Metallic'),
(4, 2, 'CP-BCG-9988', 'Nissan', 'Caravan', 2019, 'Van', 'Auto Diesel', 'Jet Black');

-- Seed Service Records (Placeholder data for demonstration)
INSERT INTO `service_records` (`service_id`, `vehicle_id`, `service_date`, `description`, `cost`, `status`) VALUES
(1, 1, '2026-01-15', 'Full Service, Engine Oil & Hybrid Filter Replacement', 18500.00, 'Completed'),
(2, 1, '2026-05-10', 'Front Brake Pads Replacement & Wheel Alignment', 12300.00, 'Completed'),
(3, 1, '2026-08-22', 'Scheduled Periodic Maintenance & AC Inspection', 8500.00, 'Completed'),
(4, 2, '2026-03-05', 'Transmission Fluid Flush & Suspension Check', 24000.00, 'Completed'),
(5, 3, '2026-02-18', 'Oil Change & Battery Check', 6500.00, 'Completed'),
(6, 4, '2026-06-12', 'Diesel Fuel Filter & Brake Shoe Overhaul', 31500.00, 'Completed');
