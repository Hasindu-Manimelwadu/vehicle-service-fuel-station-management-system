package com.vsfsms.staffmodule.service;

import com.vsfsms.staffmodule.model.Attendance;
import com.vsfsms.staffmodule.model.WorkShift;
import com.vsfsms.staffmodule.repository.AttendanceRepository;
import com.vsfsms.staffmodule.repository.WorkShiftRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Works out a staff member's overtime for a month from their attendance.
 *
 *   worked hours    = attendance working hours (or check-out - check-in,
 *                     overnight allowed, if the stored value is missing)
 *   normal hours    = length of the staff member's scheduled (non-cancelled)
 *                     shifts that day; if no shift is scheduled, the
 *                     standard day (payroll.standard-daily-hours)
 *   daily overtime  = max(0, worked hours - normal hours)
 *   overtime hours  = sum of daily overtime for the month
 *   hourly rate     = basicSalary / monthlyHoursDivisor
 *   overtime pay    = overtimeHours x hourlyRate x overtimeMultiplier
 *
 * Rule values come from payroll.* in application.properties.
 */
@Component
public class OvertimeCalculator {

    private static final BigDecimal SIXTY = BigDecimal.valueOf(60);

    @Autowired private AttendanceRepository attendanceRepository;
    @Autowired private WorkShiftRepository workShiftRepository;

    @Value("${payroll.standard-daily-hours:8}")
    private BigDecimal standardDailyHours;

    @Value("${payroll.monthly-hours-divisor:240}")
    private BigDecimal monthlyHoursDivisor;

    @Value("${payroll.overtime-multiplier:1.5}")
    private BigDecimal overtimeMultiplier;

    @Value("${payroll.use-shift-hours:true}")
    private boolean useShiftHours;

    /**
     * @param daysMissingCheckout attendance days in the month that have a check-in
     *                            but no check-out yet, so their hours (and any
     *                            overtime) can't be counted yet
     */
    public record Result(BigDecimal overtimeHours, BigDecimal hourlyRate, BigDecimal overtimeRate,
                         BigDecimal overtimePay, int daysWithOvertime, int daysMissingCheckout,
                         int attendanceDays) {}

    public Result calculate(String staffId, String month, BigDecimal basicSalary) {
        YearMonth ym = YearMonth.parse(month);
        BigDecimal hours = BigDecimal.ZERO;
        int otDays = 0, missing = 0, total = 0;
        for (Attendance a : attendanceRepository.findByStaffIdAndDateBetween(staffId, ym.atDay(1), ym.atEndOfMonth())) {
            total++;
            BigDecimal worked = workedHours(a);
            if (worked == null) {
                if (a.getCheckInTime() != null) missing++;
                continue;
            }
            BigDecimal ot = dailyOvertime(staffId, a.getDate(), worked);
            if (ot.signum() > 0) {
                hours = hours.add(ot);
                otDays++;
            }
        }
        BigDecimal basic = basicSalary == null ? BigDecimal.ZERO : basicSalary;
        BigDecimal hourly = basic.divide(monthlyHoursDivisor, 4, RoundingMode.HALF_UP);
        BigDecimal otRate = hourly.multiply(overtimeMultiplier).setScale(2, RoundingMode.HALF_UP);
        BigDecimal pay = hours.multiply(basic).multiply(overtimeMultiplier)
                .divide(monthlyHoursDivisor, 2, RoundingMode.HALF_UP);
        return new Result(hours.setScale(2, RoundingMode.HALF_UP),
                hourly.setScale(2, RoundingMode.HALF_UP), otRate, pay, otDays, missing, total);
    }

    /** Overtime hours for one attendance record (0 if none / not known yet). */
    public BigDecimal dailyOvertime(Attendance a) {
        BigDecimal worked = workedHours(a);
        if (worked == null || a.getStaffId() == null || a.getDate() == null) return BigDecimal.ZERO;
        return dailyOvertime(a.getStaffId(), a.getDate(), worked).setScale(2, RoundingMode.HALF_UP);
    }

    /** Overtime for a day = worked hours beyond that day's normal hours. */
    public BigDecimal dailyOvertime(String staffId, LocalDate date, BigDecimal workedHours) {
        if (workedHours == null) return BigDecimal.ZERO;
        BigDecimal ot = workedHours.subtract(normalHours(staffId, date));
        return ot.signum() > 0 ? ot : BigDecimal.ZERO;
    }

    /** Scheduled shift hours for that staff member/day, or the standard day if no shift. */
    public BigDecimal normalHours(String staffId, LocalDate date) {
        if (useShiftHours && staffId != null && date != null) {
            long mins = 0;
            for (WorkShift s : workShiftRepository.findByStaffIdAndShiftDate(staffId, date)) {
                if (s.getShiftStatus() == WorkShift.ShiftStatus.CANCELLED) continue;
                mins += spanMinutes(s.getStartTime(), s.getEndTime());
            }
            if (mins > 0) return BigDecimal.valueOf(mins).divide(SIXTY, 4, RoundingMode.HALF_UP);
        }
        return standardDailyHours;
    }

    /** Stored working hours, or computed from check-in/out for older rows that lack it. */
    public static BigDecimal workedHours(Attendance a) {
        if (a.getWorkingHours() != null) return a.getWorkingHours();
        if (a.getCheckInTime() != null && a.getCheckOutTime() != null) {
            long m = spanMinutes(a.getCheckInTime(), a.getCheckOutTime());
            if (m > 0) return BigDecimal.valueOf(m).divide(SIXTY, 2, RoundingMode.HALF_UP);
        }
        return null;
    }

    /** Minutes from start to end; an end earlier than the start means it ran past midnight. */
    public static long spanMinutes(LocalTime start, LocalTime end) {
        if (start == null || end == null || start.equals(end)) return 0;
        long m = Duration.between(start, end).toMinutes();
        return m < 0 ? m + 24 * 60 : m;
    }

    /** The active rules, so the admin panel can explain the calculation. */
    public Map<String, Object> rules() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("standardDailyHours", standardDailyHours);
        m.put("monthlyHoursDivisor", monthlyHoursDivisor);
        m.put("overtimeMultiplier", overtimeMultiplier);
        m.put("useShiftHours", useShiftHours);
        return m;
    }
}
