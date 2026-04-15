package domain;

import service.EmployeeManager;
import service.RoleRegistry;
import service.ShiftHistory;

import java.time.*;

public class DataInitializer {

    public static void initialize(EmployeeManager manager, ShiftHistory history) {

        RoleRegistry.addRole("CASHIER");
        RoleRegistry.addRole("STOCKER");
        RoleRegistry.addRole("SHIFT_MANAGER");

        // ===== Employees =====
        Employee e1 = new Employee("1", "Omer", LocalDate.now(), 0, "Full", "123");
        Employee e2 = new Employee("2", "Dana", LocalDate.now(), 0, "Part", "456");
        Employee e3 = new Employee("3", "Yossi", LocalDate.now(), 0, "Full", "789");
        Employee e4 = new Employee("4", "Noa", LocalDate.now(), 0, "Student", "321");
        Employee e5 = new Employee("5", "Amit", LocalDate.now(), 0, "Senior", "654");

        // ===== Roles =====
        e1.addRole("SHIFT_MANAGER");
        e1.addRole("CASHIER");

        e2.addRole("STOCKER");

        e3.addRole("CASHIER");

        e4.addRole("STOCKER");

        e5.addRole("SHIFT_MANAGER");

        // ===== Add employees =====
        manager.addEmployee(e1);
        manager.addEmployee(e2);
        manager.addEmployee(e3);
        manager.addEmployee(e4);
        manager.addEmployee(e5);

        // ===== Make everyone available for all shifts =====
        for (DayOfWeek day : new DayOfWeek[]{
                DayOfWeek.SUNDAY,
                DayOfWeek.MONDAY,
                DayOfWeek.TUESDAY,
                DayOfWeek.WEDNESDAY,
                DayOfWeek.THURSDAY,
                DayOfWeek.FRIDAY}) {

            for (ShiftType type : ShiftType.values()) {

                ShiftSlot slot = new ShiftSlot(day, type);

                e1.addAvailableShift(slot);
                e2.addAvailableShift(slot);
                e3.addAvailableShift(slot);
                e4.addAvailableShift(slot);
                e5.addAvailableShift(slot);
            }
        }

        // ===== Create ONE full week =====
        WeeklySchedule week = new WeeklySchedule();

        for (Shift shift : week.getAllShifts()) {

            // Required roles
            shift.setRequiredRole("CASHIER", 1);
            shift.setRequiredRole("STOCKER", 1);

            // Manager
            shift.setManager(e5);

            // Employees
            shift.addEmployee(e1,"CASHIER");
            shift.addEmployee(e2, "STOCKER");
        }

        // ===== Add to history =====
        history.addWeek(LocalDate.now().minusWeeks(1), week);
        manager.resetAllEmployeesAvailability();

    }

}