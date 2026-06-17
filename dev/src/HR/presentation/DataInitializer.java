package HR.presentation;

import HR.domain.*;
import HR.domain.*;
import HR.service.EmployeeManager;
import HR.service.ShiftHistory;

import java.time.*;

public class DataInitializer {

    public static void initialize(EmployeeManager manager, ShiftHistory history) {

        Branch mainBranch = new Branch("1");

        // ===== Employees =====
        Employee e1 = new Employee("1", "Omer", LocalDate.now(), 0, "Full", "123", mainBranch);
        Employee e2 = new Employee("2", "Dana", LocalDate.now(), 0, "Part", "456", mainBranch);
        Employee e3 = new Employee("3", "Yossi", LocalDate.now(), 0, "Full", "789", mainBranch);
        Employee e4 = new Employee("4", "Noa", LocalDate.now(), 0, "Student", "321", mainBranch);
        Employee e5 = new Employee("5", "Amit", LocalDate.now(), 0, "Senior", "654", mainBranch);
        Driver e6 = new Driver("6", "Lior", LocalDate.now(), 0, "Full", "987", mainBranch, "B");
        Driver e7 = new Driver("7", "Roni", LocalDate.now(), 0, "Full", "654", mainBranch, "C");

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
        manager.addEmployee(e6);
        manager.addEmployee(e7);

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
                e6.addAvailableShift(slot);
                e7.addAvailableShift(slot);
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

            if (shift.getRequiredRoleAmount("DRIVER") > 0) {
                if (shift.canDriverHandleDelivery(e6)) {
                    shift.addEmployee(e6, "DRIVER");
                } else {
                    shift.addEmployee(e7, "DRIVER");
                }
            }
        }

        // ===== Add to history =====
        history.addWeek(LocalDate.now().minusWeeks(1), week);
        manager.resetAllEmployeesAvailability();

    }

}
