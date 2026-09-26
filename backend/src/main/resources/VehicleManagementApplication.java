package com.sliit.vehiclemgmt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Application Entry Point
 * Vehicle Service & Fuel Station Management System
 * Module: Customer & Vehicle Management (SLIIT SE2030)
 */
@SpringBootApplication
public class VehicleManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(VehicleManagementApplication.class, args);
        System.out.println("==================================================================");
        System.out.println(" Vehicle Service & Management System - Module Backend Started!");
        System.out.println(" Access Frontend at: http://localhost:8080/");
        System.out.println(" REST API Base URL:  http://localhost:8080/api/");
        System.out.println("==================================================================");
    }
}
