package com.vehiclestation.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/api/test")
    public String testBackend() {
        return "Vehicle Service Backend is working!";
    }
}