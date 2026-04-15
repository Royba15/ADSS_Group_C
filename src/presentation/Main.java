package presentation;

import domain.*;
import service.*;

import javax.management.relation.Role;
import java.util.*;
import java.time.*;

public class Main {

    public static void main(String[] args) {

        // Create scanner once (not inside the loop)
        Scanner scanner = new Scanner(System.in);

        EmployeeManager manager = new EmployeeManager();
        WeeklySchedule schedule = new WeeklySchedule();
        ShiftHistory history = new ShiftHistory();

        // Welcome message
        System.out.println("=================================");
        System.out.println("   Welcome to SuperLi System");
        System.out.println("=================================");

        boolean dataLoaded = false;

        // Ask user if to load sample data
        System.out.println("Start system with sample data?");
        System.out.println("1 - Yes");
        System.out.println("2 - No");

        int initChoice = scanner.nextInt();

        if (initChoice == 1) {
            DataInitializer.initialize(manager, history);
            dataLoaded = true;
            System.out.println("System loaded with sample data!");
        }
        else if (initChoice == 2) {
            System.out.println("System started without data.");
        }
        else {
            System.out.println("Invalid choice, starting without data.");
        }

        while (true) {

            // Display main menu
            System.out.println("Which menu would you like to use?");
            System.out.println("1 - HR Manager");
            System.out.println("2 - Employee");
            System.out.println("Please enter your choice");


            int choice = scanner.nextInt();

            // HR Manager flow
            if (choice == 1) {

                String password = "1234"; // default password

                System.out.print("Enter password: ");
                String input = scanner.next();

                // Validate password
                if (!input.equals(password)) {
                    System.out.println("Wrong password!");
                    continue; // go back to main menu
                }

                System.out.println("Welcome HR Manager!");
                // HR menu loop
                while (true) {

                    System.out.println("\n=========== HR MENU ===========");
                    System.out.println("1 - Change submission deadline");
                    System.out.println("2 - Add employee");
                    System.out.println("3 - View all employees");
                    System.out.println("4 - Manage shift history");
                    System.out.println("5 - Manage shifts");
                    System.out.println("6 - View Current weekly schedule");
                    System.out.println("7 - Close week and start new one");
                    System.out.println("8 - Fire an employee");
                    System.out.println("9 - Manage role");
                    System.out.println("10 - Exit");

                    int hrChoice = scanner.nextInt();

                    // Change submission deadline
                    if (hrChoice == 1) {

                        System.out.println("Enter day (1=Sunday ... 7=Saturday):");
                        int dayInput = scanner.nextInt();

                        DayOfWeek day;

                        switch (dayInput) {
                            case 1: day = DayOfWeek.SUNDAY; break;
                            case 2: day = DayOfWeek.MONDAY; break;
                            case 3: day = DayOfWeek.TUESDAY; break;
                            case 4: day = DayOfWeek.WEDNESDAY; break;
                            case 5: day = DayOfWeek.THURSDAY; break;
                            case 6: day = DayOfWeek.FRIDAY; break;
                            case 7: day = DayOfWeek.SATURDAY; break;
                            default:
                                System.out.println("Invalid day");
                                continue;
                        }

                        System.out.println("Enter hour (0-23):");
                        int hour = scanner.nextInt();

                        LocalTime time = LocalTime.of(hour, 0);

                        schedule.setSubmissionDeadline(day, time);
                        System.out.println("Deadline updated to: " + day + " at " + time);
                    }

                    // Add employee
                    else if (hrChoice == 2) {

                        System.out.print("Enter ID: ");
                        String id = scanner.next();

                        System.out.print("Enter name: ");
                        String name = scanner.next();

                        Employee newEmp = new Employee(id, name,
                                LocalDate.now(), 0, "Default", "000");

                        // Ask for roles
                        System.out.println("Assign roles:");

                        List<String> rolesList = new ArrayList<>(RoleRegistry.getRoles());
                        Collections.sort(rolesList);

                        for (int i = 0; i < rolesList.size(); i++) {
                            System.out.println((i + 1) + " - " + rolesList.get(i));
                        }

                        System.out.print("Choose role: ");
                        choice = scanner.nextInt();

                        if (choice < 1 || choice > rolesList.size()) {
                            System.out.println("Invalid choice!");
                        } else {
                            String role = rolesList.get(choice - 1);
                            newEmp.addRole(role);
                            System.out.println("Role added: " + role);
                        }

                        manager.addEmployee(newEmp);

                        System.out.println("Employee added!");
                    }
                    // View all employees
                    else if (hrChoice == 3) {

                        for (Employee e : manager.getAllEmployees()) {

                            String status = e.isActive() ? "" : " (FIRED)";

                            System.out.println(e.getId() + " - " + e.getName() + status);
                        }
                    }
                    // View history menu
                    else if (hrChoice == 4) {

                        while (true) {

                            System.out.println("\n------ HISTORY MENU ------");
                            System.out.println("1 - View all history");
                            System.out.println("2 - View specific week");
                            System.out.println("3 - change history start date");
                            System.out.println("4 - Back");

                            int histChoice = scanner.nextInt();

                            // View all history
                            if (histChoice == 1) {

                                for (LocalDate date : history.getAllHistory().keySet()) {

                                    System.out.println("\nWeek starting: " + date);

                                    WeeklySchedule week = history.getWeek(date);
                                    week.printSchedule();
                                }
                            }

                            // View specific week
                            else if (histChoice == 2) {

                                System.out.println("Enter date (year month day):");
                                int year = scanner.nextInt();
                                int month = scanner.nextInt();
                                int day = scanner.nextInt();

                                LocalDate date = LocalDate.of(year, month, day);

                                WeeklySchedule week = history.getWeek(date);

                                if (week == null) {
                                    System.out.println("No schedule found!");
                                } else {
                                    week.printSchedule();
                                }
                            }

                            else if (histChoice == 3) {

                                System.out.print("Enter cutoff date (yyyy-mm-dd): ");
                                input = scanner.next();

                                LocalDate date;

                                try {
                                    date = LocalDate.parse(input);
                                } catch (Exception e) {
                                    System.out.println("Invalid date format!");
                                    continue;
                                }

                                history.removeHistoryBefore(date);

                                System.out.println("Old history deleted successfully!");
                            }


                            else if (histChoice == 4) {
                                break;
                            }

                            else {
                                System.out.println("Invalid choice");
                            }
                        }
                    }
                    // Manage shifts
                    else if (hrChoice == 5) {
                        if (schedule.canSubmit(LocalDateTime.now())) {
                            System.out.println("Cannot assign employees before submission deadline expired!");
                            continue;
                        }

                        System.out.println("Enter day (1=Sunday ... 7=Saturday):");
                        int dayInput = scanner.nextInt();

                        DayOfWeek day;

                        switch (dayInput) {
                            case 1:
                                day = DayOfWeek.SUNDAY;
                                break;
                            case 2:
                                day = DayOfWeek.MONDAY;
                                break;
                            case 3:
                                day = DayOfWeek.TUESDAY;
                                break;
                            case 4:
                                day = DayOfWeek.WEDNESDAY;
                                break;
                            case 5:
                                day = DayOfWeek.THURSDAY;
                                break;
                            case 6:
                                day = DayOfWeek.FRIDAY;
                                break;
                            case 7:
                                day = DayOfWeek.SATURDAY;
                                break;
                            default:
                                System.out.println("Invalid day");
                                continue;
                        }

                        System.out.println("Enter shift type (1=Morning, 2=Evening):");
                        int typeInput = scanner.nextInt();

                        ShiftType type = (typeInput == 1) ? ShiftType.MORNING : ShiftType.EVENING;

                        ShiftSlot slot = new ShiftSlot(day, type);
                        Shift shift = schedule.getShift(slot);

                        // Show available employees
                        Map<String, List<Employee>> available =
                                manager.getAvailableEmployees(slot);

                        for (String role : available.keySet()) {
                            System.out.println(role + ":");

                            for (Employee e : available.get(role)) {
                                System.out.println("  - " + e.getId() + " " + e.getName());
                            }

                        }

                        // Ask if to assign or go back
                        while (true) {

                            Map<String, Integer> remaining = shift.getRemainingRoles();

                            for (String r : remaining.keySet()) {
                                System.out.println(r + ": " + remaining.get(r));
                            }

                            // Manager status
                            if (shift.getManager() == null) {
                                System.out.println("SHIFT_MANAGER: missing");
                            } else {
                                System.out.println("SHIFT_MANAGER: assigned");
                            }


                            System.out.println("1 - Assign employee");
                            System.out.println("2 - Assign shift manager");
                            System.out.println("3 - Change required roles");
                            System.out.println("4 - Swap employees");
                            System.out.println("5 - Block shift");
                            System.out.println("6 - Exceptional assignment");
                            System.out.println("7 - Back");

                            int assignChoice = scanner.nextInt();

                            if (assignChoice == 2) {

                                // Get all employees
                                Collection<Employee> allEmployees = manager.getAllEmployees();

                                // Filter shift managers
                                List<Employee> managers = new ArrayList<>();

                                for (Employee e : allEmployees) {
                                    if (e.getRoles().contains("SHIFT_MANAGER") && e.isActive()) {
                                        managers.add(e);
                                    }
                                }

                                // If none found
                                if (managers.isEmpty()) {
                                    System.out.println("No available shift managers!");
                                    continue;
                                }

                                // Print managers
                                System.out.println("Available Shift Managers:");
                                for (Employee e : managers) {
                                    System.out.println("  - " + e.getId() + " " + e.getName());
                                }

                                // Choose manager
                                System.out.print("Enter manager ID: ");
                                String managerId = scanner.next();

                                Employee m = manager.getEmployee(managerId);

                                // Validate choice
                                if (m == null || !managers.contains(m)) {
                                    System.out.println("Invalid manager!");
                                    continue;
                                }

                                // Assign manager (only here!)
                                shift.setManager(m);

                                System.out.println("Manager assigned!");
                                continue;
                            }

                            if (assignChoice == 7) {
                                break;
                            }

                            if (assignChoice == 3) {

                                System.out.println("Choose role to change:");
                                System.out.println("1 - Cashier");
                                System.out.println("2 - Stocker");

                                int roleChoice = scanner.nextInt();

                                String role;

                                if (roleChoice == 1) {
                                    role = "CASHIER";
                                } else if (roleChoice == 2) {
                                    role = "STOCKER";
                                } else {
                                    System.out.println("Invalid role");
                                    continue;
                                }

                                System.out.print("Enter required amount: ");
                                int amount = scanner.nextInt();

                                if (amount < 0) {
                                    System.out.println("Invalid amount");
                                    continue;
                                }

                                shift.setRequiredRole(role, amount);

                                System.out.println("Shift requirement updated!");
                                break;
                            }

                            if (assignChoice == 4) {

                                System.out.print("Enter assigned employee ID (to replace): ");
                                String id1 = scanner.next();

                                System.out.print("Enter new employee ID: ");
                                String id2 = scanner.next();

                                Employee e1 = manager.getEmployee(id1);
                                Employee e2 = manager.getEmployee(id2);

                                if (e1 == null || e2 == null) {
                                    System.out.println("Invalid employee ID!");
                                    continue;
                                }

                                // Check employee 1 is assigned
                                if (!shift.containsEmployee(e1)) {
                                    System.out.println("Employee " + e1.getName() + " is not assigned to this shift!");
                                    continue;
                                }

                                // Check employee 2 is available
                                Map<String, List<Employee>> avlbs = manager.getAvailableEmployees(slot);

                                boolean isAvailable = false;

                                for (List<Employee> list : avlbs.values()) {
                                    if (list.contains(e2)) {
                                        isAvailable = true;
                                        break;
                                    }
                                }

                                if (!isAvailable) {
                                    System.out.println("Employee " + e2.getName() + " is not available for this shift!");
                                    continue;
                                }

                                // Get role of e1
                                String role = shift.getAssignedEmployees().get(e1);

                                // Check e2 not already assigned
                                if (shift.containsEmployee(e2)) {
                                    System.out.println("Employee " + e2.getName() + " is already assigned to this shift!");
                                    continue;
                                }

                                // Check e2 has the role
                                if (!e2.getRoles().contains(role)) {
                                    System.out.println("Employee " + e2.getName() + " is not qualified for this role!");
                                    continue;
                                }

                                // Replace
                                shift.removeEmployee(e1);
                                shift.addEmployee(e2, role);

                                System.out.println("Employee replaced successfully!");
                            }

                            if (assignChoice == 1) {
                                // Assign employee
                                System.out.print("Enter employee ID: ");
                                String empId = scanner.next();

                                Employee emp = manager.getEmployee(empId);

                                if (emp == null) {
                                    System.out.println("Employee not found!");
                                    continue;
                                }

                                // Role selection with back option
                                System.out.println("Choose role:");
                                System.out.println("1 - Cashier");
                                System.out.println("2 - Stocker");
                                System.out.println("3 - Back");

                                int roleChoice = scanner.nextInt();

                                if (roleChoice == 3) {
                                    continue;
                                }

                                String role;

                                if (roleChoice == 1) {
                                    role = "CASHIER";
                                } else if (roleChoice == 2) {
                                    role = "STOCKER";
                                } else {
                                    System.out.println("Invalid role");
                                    continue;
                                }

                                // Assign employee
                                if (shift.addEmployee(emp, role)) {
                                    System.out.println("Employee assigned!");
                                } else {
                                    System.out.println("Assignment failed!");
                                }
                            }

                            if (assignChoice == 5) {

                                shift.setBlocked(true);

                                System.out.println("Shift blocked successfully!");
                                break;
                            }

                            else if (assignChoice == 6) {

                                System.out.print("Enter employee ID: ");
                                String id = scanner.next();

                                Employee emp = manager.getEmployee(id);

                                if (emp == null) {
                                    System.out.println("Employee not found!");
                                    continue;
                                }

                                System.out.println("Choose role:");

                                List<String> rolesList = new ArrayList<>(RoleRegistry.getRoles());
                                Collections.sort(rolesList);

                                for (int i = 0; i < rolesList.size(); i++) {
                                    System.out.println((i + 1) + " - " + rolesList.get(i));
                                }

                                int roleChoice = scanner.nextInt();

                                if (roleChoice < 1 || roleChoice > rolesList.size()) {
                                    System.out.println("Invalid choice!");
                                    continue;
                                }

                                String role = rolesList.get(roleChoice - 1);

                                if (!emp.getRoles().contains(role)) {
                                    System.out.println("Employee is not trained for this role!");
                                    continue;
                                }

                                shift.getAssignedEmployees().put(emp, role);

                                System.out.println("Exceptional assignment done!");
                            }
                        }
                    }
                    else if (hrChoice == 6) {
                        schedule.printSchedule();
                    }



                    // Close week and start new one
                    else if (hrChoice == 7) {

                        // Check if all shifts are staffed
                        if (!schedule.isWeekFullyStaffed()) {
                            System.out.println("Cannot close week - not all shifts are fully staffed!");
                            continue;
                        }

                        history.addWeek(LocalDate.now(), schedule);
                        manager.resetAllEmployeesAvailability();
                        schedule = new WeeklySchedule();

                        System.out.println("Week saved and new week started!");
                    }
                    else if (hrChoice == 8) {
                        System.out.print("Enter employee ID: ");
                        String id = scanner.next();

                        Employee emp = manager.getEmployee(id);

                        if (emp == null) {
                            System.out.println("Employee not found!");
                            continue;
                        }

                        if (!emp.isActive()) {
                            System.out.println("Employee is already INACTIVE");
                            continue;
                        }

                        emp.setActive(false);

                        System.out.println("Employee " + emp.getName() + " is now fired");
                    }


                    else if (hrChoice == 9) {

                        System.out.println("\n------ ROLE MANAGEMENT ------");
                        System.out.println("1 - Add new role to system");
                        System.out.println("2 - Add role to employee");
                        System.out.println("3 - Back");

                        int subChoice = scanner.nextInt();

                        // ===== Add role to system =====
                        if (subChoice == 1) {

                            System.out.print("Enter new role name: ");
                            String role = scanner.next().toUpperCase();

                            if (RoleRegistry.getRoles().contains(role)) {
                                System.out.println("Role already exists!");
                                continue;
                            }

                            RoleRegistry.addRole(role);

                            System.out.println("Role " + role + " added successfully!");
                        }

                        // ===== Add role to employee =====
                        else if (subChoice == 2) {

                            System.out.print("Enter employee ID: ");
                            String id = scanner.next();

                            Employee emp = manager.getEmployee(id);

                            if (emp == null) {
                                System.out.println("Employee not found!");
                                continue;
                            }

                            System.out.println("Available roles:");

                            List<String> rolesList = new ArrayList<>(RoleRegistry.getRoles());
                            Collections.sort(rolesList);

                            for (int i = 0; i < rolesList.size(); i++) {
                                System.out.println((i + 1) + " - " + rolesList.get(i));
                            }

                            System.out.print("Choose role: ");
                             choice = scanner.nextInt();

                            if (choice < 1 || choice > rolesList.size()) {
                                System.out.println("Invalid choice!");
                                continue;
                            }

                            String role = rolesList.get(choice - 1);

                            if (role.equals("CASHIER") && emp.getRoles().contains("STOCKER")) {
                                System.out.println("Stocker cannot be Cashier!");
                                continue;
                            }

                            if (emp.getRoles().contains(role)) {
                                System.out.println("Employee already has this role!");
                                continue;
                            }
                            emp.addRole(role);

                            System.out.println("Role added to employee!");
                        }

                        else if (subChoice == 3) {
                            continue;
                        }

                        else {
                            System.out.println("Invalid choice");
                        }
                    }



                    // Exit HR menu
                    else if (hrChoice == 10) {
                        break;
                    }

                    else {
                        System.out.println("Invalid choice");
                    }
                }
            }

            // Employee flow
            else if (choice == 2) {
                System.out.print("Enter your ID: ");
                String id = scanner.next();

                // Get employee from the system
                Employee employee = manager.getEmployee(id);

                // Check if employee exists
                if (employee == null) {
                    System.out.println("Employee not found!");
                    continue;
                }

                // Check if employee is active
                if (!employee.isActive()) {
                    System.out.println("Employee is not active!");
                    continue;
                }

                System.out.println("Welcome " + employee.getName() + "!");

                // Employee menu loop
                while (true) {

                    System.out.println("\nEmployee Menu:");
                    System.out.println("1 - Submit availability");
                    System.out.println("2 - View weekly schedule");
                    System.out.println("3 - Exit");

                    int empChoice = scanner.nextInt();

                    // Submit availability
                    if (empChoice == 1) {

                        // Check if submission is still allowed
                        if (!schedule.canSubmit(LocalDateTime.now())) {
                            System.out.println("Cannot submit - deadline has passed!");
                            continue;
                        }
                        while (true) {

                            System.out.println("\nSubmit Availability:");
                            System.out.println("1 - Add shift");
                            System.out.println("2 - Back");

                            int subChoice = scanner.nextInt();

                            if (subChoice == 2) {
                                break; // back to employee menu
                            }

                            // Ask for day
                            System.out.println("Enter day (1=Sunday ... 7=Saturday):");
                            int dayInput = scanner.nextInt();

                            DayOfWeek day;

                            switch (dayInput) {
                                case 1: day = DayOfWeek.SUNDAY; break;
                                case 2: day = DayOfWeek.MONDAY; break;
                                case 3: day = DayOfWeek.TUESDAY; break;
                                case 4: day = DayOfWeek.WEDNESDAY; break;
                                case 5: day = DayOfWeek.THURSDAY; break;
                                case 6: day = DayOfWeek.FRIDAY; break;
                                case 7: day = DayOfWeek.SATURDAY; break;
                                default:
                                    System.out.println("Invalid day");
                                    continue;
                            }

                            // Ask for shift type
                            System.out.println("Enter shift type (1=Morning, 2=Evening):");
                            int typeInput = scanner.nextInt();

                            ShiftType type = (typeInput == 1) ? ShiftType.MORNING : ShiftType.EVENING;


                        ShiftSlot slot = new ShiftSlot(day, type);
                        employee.setSubmittedAvailability(true);
                        // Add availability
                        employee.addAvailableShift(slot);

                        System.out.println("Availability added!");

                    }
                        }

                    // Exit employee menu
                    else if (empChoice == 2) {
                        WeeklySchedule lastWeek = history.getLastWeek();

                        if (lastWeek == null) {
                            System.out.println("No schedule available yet.");
                        } else {
                            lastWeek.printSchedule();
                        }
                    }

                    // Exit employee menu
                    else if (empChoice == 3) {
                        break;
                    }

                    else {
                        System.out.println("Invalid choice");
                    }
                }
            }
            // Invalid input
            else {
                System.out.println("Please enter a valid choice");
            }
        }
    }
}