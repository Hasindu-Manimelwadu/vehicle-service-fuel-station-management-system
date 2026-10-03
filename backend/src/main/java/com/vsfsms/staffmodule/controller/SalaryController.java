package com.vsfsms.staffmodule.controller;

import com.vsfsms.staffmodule.model.Salary;
import com.vsfsms.staffmodule.service.SalaryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.vsfsms.staffmodule.service.OvertimeCalculator;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * CRUD for salary records.
 * Errors (validation, not-found, duplicates) are turned into JSON by
 * GlobalExceptionHandler, so no try/catch is needed here.
 */
@RestController
@RequestMapping("/api/salaries")
public class SalaryController {

    @Autowired
    private SalaryService service;

    // CREATE
    @PostMapping
    public ResponseEntity<Salary> create(@Valid @RequestBody Salary body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createSalary(body));
    }

    // READ - all (optional filter)
    @GetMapping
    public List<Salary> getAll(@RequestParam(required = false) String staffId) {
        if (staffId != null && !staffId.isBlank()) {
            return service.getSalariesByStaff(staffId);
        }
        return service.getAllSalaries();
    }

    // Overtime preview for the salary form, e.g.
    // GET /api/salaries/overtime-preview?staffId=STF001&month=2026-09&basicSalary=65000
    @GetMapping("/overtime-preview")
    public OvertimeCalculator.Result overtimePreview(@RequestParam String staffId,
                                                     @RequestParam String month,
                                                     @RequestParam(required = false) BigDecimal basicSalary) {
        return service.previewOvertime(staffId, month, basicSalary);
    }

    // The active overtime rules (daily hours, divisor, multiplier)
    @GetMapping("/overtime-rules")
    public Map<String, Object> overtimeRules() {
        return service.overtimeRules();
    }

    // READ - one
    @GetMapping("/{id}")
    public Salary getOne(@PathVariable String id) {
        return service.getSalaryById(id);
    }

    // UPDATE
    @PutMapping("/{id}")
    public Salary update(@PathVariable String id, @Valid @RequestBody Salary body) {
        return service.updateSalary(id, body);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.deleteSalary(id);
        return ResponseEntity.noContent().build();
    }
}
