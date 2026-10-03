package com.vsfsms.staffmodule.controller;

import com.vsfsms.staffmodule.model.WorkShift;
import com.vsfsms.staffmodule.service.WorkShiftService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CRUD for work shifts.
 * Errors (validation, not-found, duplicates) are turned into JSON by
 * GlobalExceptionHandler, so no try/catch is needed here.
 */
@RestController
@RequestMapping("/api/shifts")
public class WorkShiftController {

    @Autowired
    private WorkShiftService service;

    // CREATE
    @PostMapping
    public ResponseEntity<WorkShift> create(@Valid @RequestBody WorkShift body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createShift(body));
    }

    // READ - all (optional filter)
    @GetMapping
    public List<WorkShift> getAll(@RequestParam(required = false) String staffId) {
        if (staffId != null && !staffId.isBlank()) {
            return service.getShiftsByStaff(staffId);
        }
        return service.getAllShifts();
    }

    // READ - one
    @GetMapping("/{id}")
    public WorkShift getOne(@PathVariable String id) {
        return service.getShiftById(id);
    }

    // UPDATE
    @PutMapping("/{id}")
    public WorkShift update(@PathVariable String id, @Valid @RequestBody WorkShift body) {
        return service.updateShift(id, body);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.deleteShift(id);
        return ResponseEntity.noContent().build();
    }
}
