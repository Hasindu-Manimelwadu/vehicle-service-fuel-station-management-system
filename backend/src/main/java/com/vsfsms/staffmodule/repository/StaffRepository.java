package com.vsfsms.staffmodule.repository;

import com.vsfsms.staffmodule.model.Staff;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StaffRepository extends JpaRepository<Staff, String> {
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByEmployeeNoIgnoreCase(String employeeNo);

    // used on update: "does anyone *else* already have this value?"
    boolean existsByEmailIgnoreCaseAndStaffIdNot(String email, String staffId);
    boolean existsByEmployeeNoIgnoreCaseAndStaffIdNot(String employeeNo, String staffId);

    // simple search for GET /api/staff?search=...
    List<Staff> findByFullNameContainingIgnoreCaseOrEmployeeNoContainingIgnoreCaseOrEmailContainingIgnoreCaseOrDesignationContainingIgnoreCase(
            String name, String employeeNo, String email, String designation);
}
