package HR.presentation;

import HR.domain.*;
import HR.service.EmployeeManager;
import HR.service.RoleRegistry;
import HR.service.ShiftHistory;
import HR.domain.*;
import HR.repository.DaoScheduleRepository;
import HR.repository.DaoSystemStateRepository;
import HR.repository.ScheduleRepository;
import HR.repository.SystemStateRepository;
import HR.service.*;

import java.util.*;
import java.time.*;

public class Main {

    private static int readInt(Scanner scanner) {
        while (!scanner.hasNextInt()) {
            System.out.println("Invalid input. Please enter a number:");
            scanner.next();
        }

        return scanner.nextInt();
    }

    public static void main(String[] args) {

        // Create scanner once (not inside the loop)
        Scanner scanner = new Scanner(System.in);

        EmployeeManager manager = new EmployeeManager();
        ScheduleRepository scheduleRepository = new DaoScheduleRepository();
        SystemStateRepository systemStateRepository = new DaoSystemStateRepository();
        WeeklySchedule schedule = scheduleRepository.loadCurrentSchedule(manager.getAllEmployees());
        ShiftHistory history = new ShiftHistory(scheduleRepository.loadHistory(manager.getAllEmployees()));

        // Welcome message
        System.out.println("=================================");
        System.out.println("   Welcome to SuperLi System");
        System.out.println("=================================");

        boolean dataLoaded = false;

        // Ask user if to load sample data
        System.out.println("Start system with sample data?");
        System.out.println("1 - Yes");
        System.out.println("2 - No");

        int initChoice = readInt(scanner);

        if (initChoice == 1) {
            DataInitializer.initialize(manager, history);
            dataLoaded = true;
            System.out.println("System loaded with sample data!");
        }
        else if (initChoice == 2) {
            systemStateRepository.clearData();
            manager = new EmployeeManager();
            schedule = new WeeklySchedule();
            history = new ShiftHistory();
            scheduleRepository.saveCurrentSchedule(schedule);
            System.out.println("Database cleared. System started without data.");
        }
        else {
            systemStateRepository.clearData();
            manager = new EmployeeManager();
            schedule = new WeeklySchedule();
            history = new ShiftHistory();
            scheduleRepository.saveCurrentSchedule(schedule);
            System.out.println("Invalid choice. Database cleared and system started without data.");
        }

        while (true) {

            // Display main menu
            System.out.println("Which menu would you like to use?");
            System.out.println("1 - HR Manager");
            System.out.println("2 - Employee");
            System.out.println("3 - Exit System");
            System.out.println("Please enter your choice");


            int choice = readInt(scanner);

            if (choice == 3) {
                System.out.println("Goodbye!");
                break;
            }

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
                    System.out.println("10 - Block shift");
                    System.out.println("11 - Exit");

                    int hrChoice = readInt(scanner);

                    // Change submission deadline
                    if (hrChoice == 1) {

                        System.out.println("Enter day (1=Sunday ... 7=Saturday):");
                        int dayInput = readInt(scanner);

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
                        int hour = readInt(scanner);

                        if (hour < 0 || hour > 23) {
                            System.out.println("Invalid hour");
                            continue;
                        }

                        LocalTime time = LocalTime.of(hour, 0);

                        schedule.setSubmissionDeadline(day, time);
                        scheduleRepository.saveCurrentSchedule(schedule);
                        System.out.println("Deadline updated to: " + day + " at " + time);
                    }

                    // Add employee
                    else if (hrChoice == 2) {

                        System.out.print("Enter ID: ");
                        String id = scanner.next();

                        System.out.print("Enter name: ");
                        String name = scanner.next();

                        System.out.print("Enter branch ID: ");
                        String branchId = scanner.next();

                        Branch branch = new Branch(branchId);

                        System.out.println("Is this employee a driver?");
                        System.out.println("1 - Yes");
                        System.out.println("2 - No");
                        int driverChoice = readInt(scanner);

                        Employee newEmp;

                        if (driverChoice == 1) {
                            System.out.print("Enter license type: ");
                            String licenseType = scanner.next();

                            newEmp = new Driver(id, name,
                                    LocalDate.now(), 0, "Default", "000", branch, licenseType);
                        } else {
                            newEmp = new Employee(id, name,
                                    LocalDate.now(), 0, "Default", "000", branch);
                        }

                        // Ask for roles
                        System.out.println("Assign roles:");

                        List<String> rolesList = new ArrayList<>(RoleRegistry.getRoles());
                        Collections.sort(rolesList);

                        for (int i = 0; i < rolesList.size(); i++) {
                            System.out.println((i + 1) + " - " + rolesList.get(i));
                        }

                        System.out.print("Choose role: ");
                        choice = readInt(scanner);

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

                            String branchId = e.getBranch() == null ? "No branch" : e.getBranch().getId();

                            String driverInfo = "";

                            if (e instanceof Driver) {
                                Driver driver = (Driver) e;
                                driverInfo = " - License: " + driver.getLicenseType();
                            }

                            System.out.println(e.getId() + " - " + e.getName()
                                    + " - Branch: " + branchId + driverInfo + status);
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

                            int histChoice = readInt(scanner);

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
                                int year = readInt(scanner);
                                int month = readInt(scanner);
                                int day = readInt(scanner);

                                LocalDate date;

                                try {
                                    date = LocalDate.of(year, month, day);
                                } catch (Exception e) {
                                    System.out.println("Invalid date!");
                                    continue;
                                }

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
                        int dayInput = readInt(scanner);

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
                                System.out.println("The branch is closed on Saturday!");
                                continue;
                            default:
                                System.out.println("Invalid day");
                                continue;
                        }

                        System.out.println("Enter shift type (1=Morning, 2=Evening):");
                        int typeInput = readInt(scanner);

                        ShiftType type;

                        if (typeInput == 1) {
                            type = ShiftType.MORNING;
                        } else if (typeInput == 2) {
                            type = ShiftType.EVENING;
                        } else {
                            System.out.println("Invalid shift type");
                            continue;
                        }

                        ShiftSlot slot = new ShiftSlot(day, type);
                        Shift shift = schedule.getShift(slot);

                        if (shift.isBlocked()) {
                            System.out.println("Branch is closed in this shift. Cannot assign employees.");
                            continue;
                        }

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

                            System.out.println("Required employees that still missing:");

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
                            System.out.println("5 - Exceptional assignment");
                            System.out.println("6 - Back");

                            int assignChoice = readInt(scanner);

                            if (assignChoice == 2) {

                                // Get all employees
                                Map<String, List<Employee>> availableEmployees = manager.getAvailableEmployees(slot);
                                List<Employee> managers = availableEmployees.getOrDefault("SHIFT_MANAGER", new ArrayList<>());

                                // If none found
                                if (managers.isEmpty()) {
                                    System.out.println("No available shift managers!");
                                    continue;
                                }

                                //Print managers
                                System.out.println("Available Shift Managers:");
                                for (Employee e : managers) {
                                    System.out.println("  - " + e.getId() + " " + e.getName());
                                }

                                //Choose manager
                                System.out.print("Enter manager ID: ");
                                String managerId = scanner.next();

                                Employee m = manager.getEmployee(managerId);

                                //Validate choice
                                if (m == null || !managers.contains(m)) {
                                    System.out.println("Invalid manager!");
                                    continue;
                                }

                                // Assign manager
                                if (shift.setManager(m)) {
                                    scheduleRepository.saveCurrentSchedule(schedule);
                                    System.out.println("Manager assigned!");
                                } else {
                                    System.out.println("Manager assignment failed!");
                                }
                                continue;
                            }

                            if (assignChoice == 6) {
                                break;
                            }

                            if (assignChoice == 3) {

                                System.out.println("Choose role:");

                                List<String> rolesList = new ArrayList<>();

                                for (String r : RoleRegistry.getRoles()) {
                                    if (!r.equals("SHIFT_MANAGER")) {
                                        rolesList.add(r);
                                    }
                                }

                                Collections.sort(rolesList);

                                for (int i = 0; i < rolesList.size(); i++) {
                                    System.out.println((i + 1) + " - " + rolesList.get(i));
                                }

                                System.out.println((rolesList.size() + 1) + " - Back");

                                int roleChoice = readInt(scanner);

                                if (roleChoice == rolesList.size() + 1) {
                                    continue;
                                }

                                if (roleChoice < 1 || roleChoice > rolesList.size()) {
                                    System.out.println("Invalid role");
                                    continue;
                                }

                                String role = rolesList.get(roleChoice - 1);


                                System.out.print("Enter required amount: ");
                                int amount = readInt(scanner);

                                if (amount < 0) {
                                    System.out.println("Invalid amount");
                                    continue;
                                }

                                if (!shift.setRequiredRole(role, amount)) {
                                    System.out.println("Cannot set STOCKER requirement to 0 when this shift has a delivery!");
                                    continue;
                                }

                                System.out.println("Shift requirement updated!");
                                scheduleRepository.saveCurrentSchedule(schedule);
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

                                if (role.equals("DRIVER")) {
                                    if (!(e2 instanceof Driver)) {
                                        System.out.println("Employee " + e2.getName() + " is not a driver!");
                                        continue;
                                    }

                                    Driver driver = (Driver) e2;

                                    if (!shift.canDriverHandleDelivery(driver)) {
                                        System.out.println("Driver license does not match the delivery truck type!");
                                        System.out.println("Required license: "
                                                + shift.getDelivery().getTruckType().getRequiredLicenseType());
                                        continue;
                                    }
                                }

                                // Replace
                                shift.removeEmployee(e1);
                                shift.addEmployee(e2, role);
                                scheduleRepository.saveCurrentSchedule(schedule);

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

                                List<String> rolesList = new ArrayList<>();

                                for (String r : RoleRegistry.getRoles()) {
                                    if (!r.equals("SHIFT_MANAGER")) {
                                        rolesList.add(r);
                                    }
                                }

                                Collections.sort(rolesList);

                                for (int i = 0; i < rolesList.size(); i++) {
                                    System.out.println((i + 1) + " - " + rolesList.get(i));
                                }

                                System.out.println((rolesList.size() + 1) + " - Back");

                                int roleChoice = readInt(scanner);

                                if (roleChoice == rolesList.size() + 1) {
                                    continue;
                                }

                                if (roleChoice < 1 || roleChoice > rolesList.size()) {
                                    System.out.println("Invalid role");
                                    continue;
                                }

                                String role = rolesList.get(roleChoice - 1);

                                if (role.equals("DRIVER")) {
                                    if (!(emp instanceof Driver)) {
                                        System.out.println("Employee is not a driver!");
                                        continue;
                                    }

                                    Driver driver = (Driver) emp;

                                    if (!shift.canDriverHandleDelivery(driver)) {
                                        System.out.println("Driver license does not match the delivery truck type!");
                                        System.out.println("Required license: "
                                                + shift.getDelivery().getTruckType().getRequiredLicenseType());
                                        continue;
                                    }
                                }

                                // Assign employee
                                if (shift.addEmployee(emp, role)) {
                                    scheduleRepository.saveCurrentSchedule(schedule);
                                    System.out.println("Employee assigned!");
                                } else {
                                    System.out.println("Assignment failed!");
                                }
                            }

                            else if (assignChoice == 5) {

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

                                int roleChoice = readInt(scanner);

                                if (roleChoice < 1 || roleChoice > rolesList.size()) {
                                    System.out.println("Invalid choice!");
                                    continue;
                                }

                                String role = rolesList.get(roleChoice - 1);

                                if (!emp.getRoles().contains(role)) {
                                    System.out.println("Employee is not trained for this role!");
                                    continue;
                                }

                                if (role.equals("DRIVER")) {
                                    if (!(emp instanceof Driver)) {
                                        System.out.println("Employee is not a driver!");
                                        continue;
                                    }

                                    Driver driver = (Driver) emp;

                                    if (!shift.canDriverHandleDelivery(driver)) {
                                        System.out.println("Driver license does not match the delivery truck type!");
                                        System.out.println("Required license: "
                                                + shift.getDelivery().getTruckType().getRequiredLicenseType());
                                        continue;
                                    }
                                }

                                shift.getAssignedEmployees().put(emp, role);
                                scheduleRepository.saveCurrentSchedule(schedule);

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

                        for (String role : RoleRegistry.getRoles()) {
                            if (!role.equals("SHIFT_MANAGER") && !role.equals("DRIVER")) {
                                for (Shift shift : schedule.getAllShifts()) {
                                    shift.setRequiredRole(role, 1);
                                }
                            }
                        }

                        scheduleRepository.saveCurrentSchedule(schedule);
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
                        manager.saveEmployee(emp);

                        System.out.println("Employee " + emp.getName() + " is now fired");
                    }


                    else if (hrChoice == 9) {

                        System.out.println("\n------ ROLE MANAGEMENT ------");
                        System.out.println("1 - Add new role to system");
                        System.out.println("2 - Add role to employee");
                        System.out.println("3 - Back");

                        int subChoice = readInt(scanner);

                        // ===== Add role to system =====
                        if (subChoice == 1) {

                            System.out.print("Enter new role name: ");
                            String role = scanner.next().toUpperCase();

                            if (RoleRegistry.getRoles().contains(role)) {
                                System.out.println("Role already exists!");
                                continue;
                            }

                            RoleRegistry.addRole(role);

                            if (!role.equals("SHIFT_MANAGER") && !role.equals("DRIVER")) {
                                for (Shift shift : schedule.getAllShifts()) {
                                    shift.setRequiredRole(role, 1);
                                }
                            }

                            scheduleRepository.saveCurrentSchedule(schedule);
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
                             choice = readInt(scanner);

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
                            manager.saveEmployee(emp);

                            System.out.println("Role added to employee!");
                        }

                        else if (subChoice == 3) {
                            continue;
                        }

                        else {
                            System.out.println("Invalid choice");
                        }
                    }

                    else if (hrChoice == 10) {

                        System.out.println("Enter day (1=Sunday ... 7=Friday):");
                        int dayInput = readInt(scanner);

                        DayOfWeek day;

                        switch (dayInput) {
                            case 1: day = DayOfWeek.SUNDAY; break;
                            case 2: day = DayOfWeek.MONDAY; break;
                            case 3: day = DayOfWeek.TUESDAY; break;
                            case 4: day = DayOfWeek.WEDNESDAY; break;
                            case 5: day = DayOfWeek.THURSDAY; break;
                            case 6: day = DayOfWeek.FRIDAY; break;
                            default:
                                System.out.println("Invalid day");
                                continue;
                        }

                        System.out.println("Enter shift type (1=Morning, 2=Evening):");
                        int typeInput = readInt(scanner);

                        ShiftType type;

                        if (typeInput == 1) {
                            type = ShiftType.MORNING;
                        } else if (typeInput == 2) {
                            type = ShiftType.EVENING;
                        } else {
                            System.out.println("Invalid shift type");
                            continue;
                        }

                        ShiftSlot slot = new ShiftSlot(day, type);
                        Shift shift = schedule.getShift(slot);

                        if (shift == null) {
                            System.out.println("Shift not found!");
                            continue;
                        }

                        shift.setBlocked(true);
                        scheduleRepository.saveCurrentSchedule(schedule);

                        System.out.println("Shift blocked successfully!");
                    }


                    // Exit HR menu
                    else if (hrChoice == 11) {
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

                    int empChoice = readInt(scanner);

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

                            int subChoice = readInt(scanner);

                            if (subChoice == 2) {
                                break; // back to employee menu
                            }

                            // Ask for day
                            System.out.println("Enter day (1=Sunday ... 7=Saturday):");
                            int dayInput = readInt(scanner);

                            DayOfWeek day;

                            switch (dayInput) {
                                case 1: day = DayOfWeek.SUNDAY; break;
                                case 2: day = DayOfWeek.MONDAY; break;
                                case 3: day = DayOfWeek.TUESDAY; break;
                                case 4: day = DayOfWeek.WEDNESDAY; break;
                                case 5: day = DayOfWeek.THURSDAY; break;
                                case 6: day = DayOfWeek.FRIDAY; break;
                                case 7:
                                    System.out.println("The branch is closed on Saturday!");
                                    continue;
                                default:
                                    System.out.println("Invalid day");
                                    continue;
                            }

                            // Ask for shift type
                            System.out.println("Enter shift type (1=Morning, 2=Evening):");
                            int typeInput = readInt(scanner);

                            ShiftType type;

                            if (typeInput == 1) {
                                type = ShiftType.MORNING;
                            } else if (typeInput == 2) {
                                type = ShiftType.EVENING;
                            } else {
                                System.out.println("Invalid shift type");
                                continue;
                            }


                            ShiftSlot slot = new ShiftSlot(day, type);
                            Shift shift = schedule.getShift(slot);

                            if (shift == null) {
                                System.out.println("Shift not found!");
                                continue;
                            }

                            if (shift.isBlocked()) {
                                System.out.println("Branch is closed in this shift. Cannot submit availability.");
                                continue;
                            }

                            employee.setSubmittedAvailability(true);

                            // Add availability
                            employee.addAvailableShift(slot);
                            manager.saveEmployee(employee);

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

