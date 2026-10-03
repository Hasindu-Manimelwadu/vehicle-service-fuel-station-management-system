package com.vsfsms.staffmodule.config;

import com.vsfsms.staffmodule.model.Staff;
import com.vsfsms.staffmodule.model.WorkShift;
import com.vsfsms.staffmodule.repository.StaffRepository;
import com.vsfsms.staffmodule.repository.WorkShiftRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Inserts two sample staff members and today's shifts the first time the
 * app starts with an empty database (works for both H2 and MySQL).
 * If the staff table already has rows, nothing is touched - so your own
 * edits and deletions are never overwritten on restart.
 */
@Configuration
public class SampleDataLoader {

    @Bean
    CommandLineRunner seedSampleData(StaffRepository staffRepo, WorkShiftRepository shiftRepo) {
        return args -> {
            if (staffRepo.count() > 0) return;

            staffRepo.save(staff("STF001", "USR010", "EMP-1001", "Kasun Perera", "0771234567",
                    "kasun.perera@vsfsms.lk", "Senior Technician", LocalDate.of(2023, 3, 15), "65000.00"));
            staffRepo.save(staff("STF002", "USR011", "EMP-1002", "Nadeesha Silva", "0779876543",
                    "nadeesha.silva@vsfsms.lk", "Fuel Station Attendant", LocalDate.of(2024, 1, 10), "45000.00"));

            if (shiftRepo.count() == 0) {
                shiftRepo.save(shift("SHF001", "STF001", LocalTime.of(8, 0), LocalTime.of(17, 0)));
                shiftRepo.save(shift("SHF002", "STF002", LocalTime.of(6, 0), LocalTime.of(14, 0)));
            }
            System.out.println(">>> Sample data inserted (2 staff, 2 shifts).");
        };
    }

    private static Staff staff(String id, String userId, String empNo, String name, String phone,
                               String email, String designation, LocalDate joined, String salary) {
        Staff s = new Staff();
        s.setStaffId(id);
        s.setUserId(userId);
        s.setEmployeeNo(empNo);
        s.setFullName(name);
        s.setPhone(phone);
        s.setEmail(email);
        s.setDesignation(designation);
        s.setDateJoined(joined);
        s.setEmploymentStatus(Staff.EmploymentStatus.ACTIVE);
        s.setBasicSalary(new BigDecimal(salary));
        return s;
    }

    private static WorkShift shift(String id, String staffId, LocalTime start, LocalTime end) {
        WorkShift w = new WorkShift();
        w.setShiftId(id);
        w.setStaffId(staffId);
        w.setShiftDate(LocalDate.now());
        w.setStartTime(start);
        w.setEndTime(end);
        w.setShiftStatus(WorkShift.ShiftStatus.SCHEDULED);
        return w;
    }
}
