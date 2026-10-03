package com.vsfsms.staffmodule.repository;

import com.vsfsms.staffmodule.model.Salary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SalaryRepository extends JpaRepository<Salary, String> {
    List<Salary> findByStaffId(String staffId);
    boolean existsByStaffIdAndMonth(String staffId, String month);
    boolean existsByStaffIdAndMonthAndSalaryIdNot(String staffId, String month, String salaryId);
    void deleteByStaffId(String staffId);
    Optional<Salary> findByStaffIdAndMonth(String staffId, String month);
}
