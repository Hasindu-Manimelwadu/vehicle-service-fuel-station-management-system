-- =====================================================================
-- Vehicle Service and Fuel Station Management System
-- Module: Staff Management  (Owner: Abeykoon A.M.H.S. - IT25100971)
-- Database: MySQL 8.x
-- =====================================================================

CREATE DATABASE IF NOT EXISTS vsfsms_staff_module;
USE vsfsms_staff_module;

-- ---------------------------------------------------------------------
-- Table: staff
-- Note: userId links to the shared "User" table owned by the
-- Authentication & User Role Management module. No FK constraint is
-- enforced here since that table belongs to another module/service.
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS staff (
    staff_id            VARCHAR(20)     NOT NULL PRIMARY KEY,
    user_id             VARCHAR(20)     NULL,
    employee_no         VARCHAR(20)     NOT NULL UNIQUE,
    full_name           VARCHAR(100)    NOT NULL,
    phone               VARCHAR(20)     NOT NULL,
    email               VARCHAR(100)    NOT NULL UNIQUE,
    designation         VARCHAR(50)     NOT NULL,
    date_joined         DATE            NOT NULL,
    employment_status   ENUM('ACTIVE','ON_LEAVE','SUSPENDED','TERMINATED') NOT NULL DEFAULT 'ACTIVE',
    basic_salary        DECIMAL(12,2)   NOT NULL,
    created_at          TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ---------------------------------------------------------------------
-- Table: work_shift
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS work_shift (
    shift_id     VARCHAR(20)  NOT NULL PRIMARY KEY,
    staff_id     VARCHAR(20)  NOT NULL,
    shift_date   DATE         NOT NULL,
    start_time   TIME         NOT NULL,
    end_time     TIME         NOT NULL,
    shift_status ENUM('SCHEDULED','ONGOING','COMPLETED','CANCELLED') NOT NULL DEFAULT 'SCHEDULED',
    CONSTRAINT fk_workshift_staff FOREIGN KEY (staff_id)
        REFERENCES staff (staff_id) ON DELETE CASCADE
);

-- ---------------------------------------------------------------------
-- Table: attendance
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS attendance (
    attendance_id     VARCHAR(20)  NOT NULL PRIMARY KEY,
    staff_id          VARCHAR(20)  NOT NULL,
    date              DATE         NOT NULL,
    check_in_time     TIME         NULL,
    check_out_time    TIME         NULL,
    working_hours     DECIMAL(5,2) NULL,
    attendance_status ENUM('PRESENT','ABSENT','LATE','HALF_DAY','ON_LEAVE') NOT NULL DEFAULT 'PRESENT',
    CONSTRAINT fk_attendance_staff FOREIGN KEY (staff_id)
        REFERENCES staff (staff_id) ON DELETE CASCADE,
    CONSTRAINT uq_attendance_staff_date UNIQUE (staff_id, date)
);

-- ---------------------------------------------------------------------
-- Table: salary
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS salary (
    salary_id      VARCHAR(20)   NOT NULL PRIMARY KEY,
    staff_id       VARCHAR(20)   NOT NULL,
    month          VARCHAR(7)    NOT NULL,   -- format: YYYY-MM
    basic_salary   DECIMAL(12,2) NOT NULL,
    overtime_pay   DECIMAL(12,2) NOT NULL DEFAULT 0,
    deductions     DECIMAL(12,2) NOT NULL DEFAULT 0,
    net_salary     DECIMAL(12,2) NOT NULL,
    payment_date   DATE          NULL,
    payment_status ENUM('PENDING','PAID','FAILED') NOT NULL DEFAULT 'PENDING',
    CONSTRAINT fk_salary_staff FOREIGN KEY (staff_id)
        REFERENCES staff (staff_id) ON DELETE CASCADE,
    CONSTRAINT uq_salary_staff_month UNIQUE (staff_id, month)
);

-- ---------------------------------------------------------------------
-- Sample seed data (optional - comment out if not needed)
-- ---------------------------------------------------------------------
INSERT INTO staff (staff_id, user_id, employee_no, full_name, phone, email, designation, date_joined, employment_status, basic_salary)
VALUES
('STF001', 'USR010', 'EMP-1001', 'Kasun Perera', '0771234567', 'kasun.perera@vsfsms.lk', 'Senior Technician', '2023-03-15', 'ACTIVE', 65000.00),
('STF002', 'USR011', 'EMP-1002', 'Nadeesha Silva', '0779876543', 'nadeesha.silva@vsfsms.lk', 'Fuel Station Attendant', '2024-01-10', 'ACTIVE', 45000.00)
ON DUPLICATE KEY UPDATE staff_id = staff_id;

INSERT INTO work_shift (shift_id, staff_id, shift_date, start_time, end_time, shift_status)
VALUES
('SHF001', 'STF001', CURDATE(), '08:00:00', '17:00:00', 'SCHEDULED'),
('SHF002', 'STF002', CURDATE(), '06:00:00', '14:00:00', 'SCHEDULED')
ON DUPLICATE KEY UPDATE shift_id = shift_id;
