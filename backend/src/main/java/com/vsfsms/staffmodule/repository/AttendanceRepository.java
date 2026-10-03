package com.vsfsms.staffmodule.repository;

import com.vsfsms.staffmodule.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, String> {
    List<Attendance> findByStaffId(String staffId);
    boolean existsByStaffIdAndDate(String staffId, LocalDate date);
    boolean existsByStaffIdAndDateAndAttendanceIdNot(String staffId, LocalDate date, String attendanceId);
    void deleteByStaffId(String staffId);
    List<Attendance> findByStaffIdAndDateBetween(String staffId, LocalDate from, LocalDate to);
}
