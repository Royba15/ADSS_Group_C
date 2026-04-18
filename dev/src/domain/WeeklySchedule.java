package domain;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;


import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.HashMap;

public class WeeklySchedule {

    // Maps each shift slot to its corresponding shift
    private Map<ShiftSlot, Shift> shifts = new LinkedHashMap<>();
    private DayOfWeek submissionDay;
    private LocalTime submissionTime;

    public WeeklySchedule() {
        this.shifts = new LinkedHashMap<>();

        //Default time of submission Thursday 18 pm
        this.submissionDay = DayOfWeek.THURSDAY;
        this.submissionTime = LocalTime.of(18, 0);

        // Initialize all shifts (Sunday–Friday, Morning & Evening)
        for (DayOfWeek day : DayOfWeek.values()) {

            // Skip Saturday if you don't need it
            if (day == DayOfWeek.SATURDAY) continue;

            ShiftSlot morning = new ShiftSlot(day, ShiftType.MORNING);
            ShiftSlot evening = new ShiftSlot(day, ShiftType.EVENING);

            shifts.put(morning, new Shift(morning));
            shifts.put(evening, new Shift(evening));
        }
    }
    // Allows HR manager to change submission deadline
    public void setSubmissionDeadline(DayOfWeek day, LocalTime time) {
        this.submissionDay = day;
        this.submissionTime = time;
    }

    public boolean canSubmit(LocalDateTime currentTime) {
        LocalDateTime now = currentTime;

        int currentDayIndex = now.getDayOfWeek().getValue() % 7;      // Sunday=0 ... Saturday=6
        int submissionDayIndex = submissionDay.getValue() % 7;        // Sunday=0 ... Saturday=6

        LocalDate startOfWeek = now.toLocalDate().minusDays(currentDayIndex);
        LocalDate deadlineDate = startOfWeek.plusDays(submissionDayIndex);
        LocalDateTime deadlineDateTime = LocalDateTime.of(deadlineDate, submissionTime);

        return now.isBefore(deadlineDateTime);
    }

    // Prints all shifts in the week in correct order (Sunday → Friday)
    public void printSchedule() {

        // Custom order of days (Sunday first)
        DayOfWeek[] orderedDays = {
                DayOfWeek.SUNDAY,
                DayOfWeek.MONDAY,
                DayOfWeek.TUESDAY,
                DayOfWeek.WEDNESDAY,
                DayOfWeek.THURSDAY,
                DayOfWeek.FRIDAY
        };

        // Loop over days in correct order
        for (DayOfWeek day : orderedDays) {

            // Loop over shift types (Morning, Evening)
            for (ShiftType type : ShiftType.values()) {

                ShiftSlot slot = new ShiftSlot(day, type);
                Shift shift = shifts.get(slot);

                System.out.println(day + " - " + type);
                if (shift.isBlocked()) {
                    System.out.println("  *** BLOCKED ***");
                    continue;
                }

                // Print manager
                if (shift.getManager() != null) {
                    System.out.println("  Manager: " + shift.getManager().getName());
                } else {
                    System.out.println("  Manager: Not assigned");
                }

                // Print employees
                if (shift.getAssignedEmployees().isEmpty()) {
                    System.out.println("  No employees assigned");
                } else {
                    for (Employee e : shift.getAssignedEmployees().keySet()) {
                        String role = shift.getAssignedEmployees().get(e);
                        System.out.println("  " + role + ": " + e.getName());
                    }
                }

                System.out.println(); // spacing between shifts
            }
        }
    }

    public Shift getShift(ShiftSlot slot) {
        return shifts.get(slot);
    }
    // Check if all shifts are fully staffed
    public boolean isWeekFullyStaffed() {

        for (Shift shift : shifts.values()) {
            if (!shift.isFullyStaffed()) {
                return false;
            }
        }

        return true;
    }

    public Collection<Shift> getAllShifts() {
        return shifts.values();
    }
}