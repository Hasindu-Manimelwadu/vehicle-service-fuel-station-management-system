package com.vsfsms.staffmodule.controller;

import com.vsfsms.staffmodule.model.Attendance;
import com.vsfsms.staffmodule.service.AttendanceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CRUD for attendance records.
 * Errors (validation, not-found, duplicates) are turned into JSON by
 * GlobalExceptionHandler, so no try/catch is needed here.
 */
@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService service;

    // CREATE
    @PostMapping
    public ResponseEntity<Attendance> create(@Valid @RequestBody Attendance body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createAttendance(body));
    }

    // READ - all (optional filter)
    @GetMapping
    public List<Attendance> getAll(@RequestParam(required = false) String staffId) {
        if (staffId != null && !staffId.isBlank()) {
            return service.getAttendanceByStaff(staffId);
        }
        return service.getAllAttendance();
    }

    // READ - one
    @GetMapping("/{id}")
    public Attendance getOne(@PathVariable String id) {
        return service.getAttendanceById(id);
    }

    // UPDATE
    @PutMapping("/{id}")
    public Attendance update(@PathVariable String id, @Valid @RequestBody Attendance body) {
        return service.updateAttendance(id, body);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.deleteAttendance(id);
        return ResponseEntity.noContent().build();
    }
}
