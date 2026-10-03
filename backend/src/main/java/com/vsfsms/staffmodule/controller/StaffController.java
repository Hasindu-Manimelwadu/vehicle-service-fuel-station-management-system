package com.vsfsms.staffmodule.controller;

import com.vsfsms.staffmodule.model.Staff;
import com.vsfsms.staffmodule.service.StaffService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CRUD for staff members.
 * Errors (validation, not-found, duplicates) are turned into JSON by
 * GlobalExceptionHandler, so no try/catch is needed here.
 */
@RestController
@RequestMapping("/api/staff")
public class StaffController {

    @Autowired
    private StaffService service;

    // CREATE
    @PostMapping
    public ResponseEntity<Staff> create(@Valid @RequestBody Staff body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createStaff(body));
    }

    // READ - all (optional filter)
    @GetMapping
    public List<Staff> getAll(@RequestParam(required = false) String search) {
        if (search != null && !search.isBlank()) {
            return service.searchStaff(search);
        }
        return service.getAllStaff();
    }

    // READ - one
    @GetMapping("/{id}")
    public Staff getOne(@PathVariable String id) {
        return service.getStaffById(id);
    }

    // UPDATE
    @PutMapping("/{id}")
    public Staff update(@PathVariable String id, @Valid @RequestBody Staff body) {
        return service.updateStaff(id, body);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.deleteStaff(id);
        return ResponseEntity.noContent().build();
    }
}
