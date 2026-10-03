package com.vsfsms.staffmodule.repository;

import com.vsfsms.staffmodule.model.WorkShift;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface WorkShiftRepository extends JpaRepository<WorkShift, String> {
    List<WorkShift> findByStaffId(String staffId);
    List<WorkShift> findByStaffIdAndShiftDate(String staffId, LocalDate shiftDate);
    void deleteByStaffId(String staffId);
}
