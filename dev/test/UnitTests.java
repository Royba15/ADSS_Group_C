import HR.data.DatabaseInitializer;
import HR.data.EmployeeDAO;
import HR.data.RoleDAO;
import HR.data.ScheduleDAO;
import HR.domain.AvailabilitySubmission;
import HR.domain.Branch;
import HR.domain.Delivery;
import HR.domain.DeliveryMock;
import HR.domain.Driver;
import HR.domain.Employee;
import HR.domain.Shift;
import HR.domain.ShiftSlot;
import HR.domain.ShiftType;
import HR.domain.StaffingRequirement;
import HR.domain.TruckType;
import HR.domain.WeeklySchedule;
import HR.presentation.DataInitializer;
import HR.service.EmployeeManager;
import HR.service.ShiftHistory;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class UnitTests {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        run("old_01_employeeStartsActive", UnitTests::old_01_employeeStartsActive);
        run("old_02_submissionDeadlineCanBeUpdated", UnitTests::old_02_submissionDeadlineCanBeUpdated);
        run("old_03_employeeWithoutSubmissionIsAvailable", UnitTests::old_03_employeeWithoutSubmissionIsAvailable);
        run("old_04_availabilitySubmissionStoresShift", UnitTests::old_04_availabilitySubmissionStoresShift);
        run("old_05_employeeSubmittedAvailabilityRestrictsShift", UnitTests::old_05_employeeSubmittedAvailabilityRestrictsShift);
        run("old_06_shiftManagerMustHaveManagerRole", UnitTests::old_06_shiftManagerMustHaveManagerRole);
        run("old_07_shiftRejectsDuplicateEmployee", UnitTests::old_07_shiftRejectsDuplicateEmployee);
        run("old_08_shiftRejectsAssignmentAboveCapacity", UnitTests::old_08_shiftRejectsAssignmentAboveCapacity);
        run("old_09_swapEmployeesSwitchesRoles", UnitTests::old_09_swapEmployeesSwitchesRoles);
        run("old_10_shiftHistoryReturnsLatestWeek", UnitTests::old_10_shiftHistoryReturnsLatestWeek);

        run("new_01_employeeHasBranch", UnitTests::new_01_employeeHasBranch);
        run("new_02_driverExtendsEmployeeAndHasDriverRole", UnitTests::new_02_driverExtendsEmployeeAndHasDriverRole);
        run("new_03_staffingRequirementDefaultRoles", UnitTests::new_03_staffingRequirementDefaultRoles);
        run("new_04_deliveryMockContainsThreeDeliveries", UnitTests::new_04_deliveryMockContainsThreeDeliveries);
        run("new_05_weeklyScheduleAddsDriverRequirementForDeliveryShift", UnitTests::new_05_weeklyScheduleAddsDriverRequirementForDeliveryShift);
        run("new_06_weeklyScheduleDoesNotAddDriverRequirementWithoutDelivery", UnitTests::new_06_weeklyScheduleDoesNotAddDriverRequirementWithoutDelivery);
        run("new_07_driverWithMatchingLicenseCanHandleDelivery", UnitTests::new_07_driverWithMatchingLicenseCanHandleDelivery);
        run("new_08_driverWithWrongLicenseCannotHandleDelivery", UnitTests::new_08_driverWithWrongLicenseCannotHandleDelivery);
        run("new_09_deliveryShiftCannotSetStockerRequirementToZero", UnitTests::new_09_deliveryShiftCannotSetStockerRequirementToZero);
        run("new_10_nonDeliveryShiftCanSetStockerRequirementToZero", UnitTests::new_10_nonDeliveryShiftCanSetStockerRequirementToZero);

        System.setProperty("superli.db.path", Path.of(
                System.getProperty("java.io.tmpdir"),
                "superli",
                "superli-test.db").toString());
        runDb("db_01_employeeCanBeSavedAndLoaded", UnitTests::db_01_employeeCanBeSavedAndLoaded);
        runDb("db_02_driverLicenseCanBeSavedAndLoaded", UnitTests::db_02_driverLicenseCanBeSavedAndLoaded);
        runDb("db_03_availabilityCanBeSavedAndLoaded", UnitTests::db_03_availabilityCanBeSavedAndLoaded);
        runDb("db_04_roleCanBeSavedAndLoaded", UnitTests::db_04_roleCanBeSavedAndLoaded);
        runDb("db_05_currentScheduleCanBeSavedAndLoaded", UnitTests::db_05_currentScheduleCanBeSavedAndLoaded);

        if (failed == 0) {
            restoreTestDatabaseToDataInitializerState();
        }

        System.out.println();
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);

        if (failed > 0) {
            System.exit(1);
        }
    }

    private static void run(String testName, TestCase testCase) {
        try {
            testCase.run();
            passed++;
            System.out.println("[PASS] " + testName);
        } catch (AssertionError e) {
            failed++;
            System.out.println("[FAIL] " + testName + " - " + e.getMessage());
        } catch (Exception e) {
            failed++;
            System.out.println("[ERROR] " + testName + " - " + e.getMessage());
        }
    }

    private static void runDb(String testName, TestCase testCase) {
        try {
            DatabaseInitializer.clearData();
            testCase.run();
            passed++;
            System.out.println("[PASS] " + testName);
        } catch (AssertionError e) {
            failed++;
            System.out.println("[FAIL] " + testName + " - " + e.getMessage());
        } catch (Exception e) {
            failed++;
            System.out.println("[ERROR] " + testName + " - " + e.getMessage());
        }
    }

    private static void restoreTestDatabaseToDataInitializerState() {
        DatabaseInitializer.clearData();

        EmployeeManager manager = new EmployeeManager();
        ShiftHistory history = new ShiftHistory();

        DataInitializer.initialize(manager, history);
    }

    private static Employee employee(String id) {
        return new Employee(id, "Employee" + id, LocalDate.now(), 0, "Default", "000", new Branch("1"));
    }

    private static Driver driver(String id, String licenseType) {
        return new Driver(id, "Driver" + id, LocalDate.now(), 0, "Default", "000", new Branch("1"), licenseType);
    }

    private static ShiftSlot sundayMorning() {
        return new ShiftSlot(DayOfWeek.SUNDAY, ShiftType.MORNING);
    }

    private static ShiftSlot mondayMorning() {
        return new ShiftSlot(DayOfWeek.MONDAY, ShiftType.MORNING);
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void assertFalse(boolean condition, String message) {
        assertTrue(!condition, message);
    }

    private static void assertEquals(Object expected, Object actual, String message) {
        if (expected == null && actual == null) {
            return;
        }

        if (expected != null && expected.equals(actual)) {
            return;
        }

        throw new AssertionError(message + " Expected: " + expected + ", actual: " + actual);
    }

    private static void old_01_employeeStartsActive() {
        Employee employee = employee("1");

        assertTrue(employee.isActive(), "New employee should start active");
    }

    private static void old_02_submissionDeadlineCanBeUpdated() {
        WeeklySchedule schedule = new WeeklySchedule();

        assertEquals(DayOfWeek.THURSDAY, schedule.getSubmissionDay(),
                "Default submission day should be Thursday");
        assertEquals(java.time.LocalTime.of(18, 0), schedule.getSubmissionTime(),
                "Default submission time should be 18:00");

        schedule.setSubmissionDeadline(DayOfWeek.SUNDAY, java.time.LocalTime.of(9, 0));

        assertEquals(DayOfWeek.SUNDAY, schedule.getSubmissionDay(),
                "Submission day should be updated to Sunday");
        assertEquals(java.time.LocalTime.of(9, 0), schedule.getSubmissionTime(),
                "Submission time should be updated to 09:00");
    }

    private static void old_03_employeeWithoutSubmissionIsAvailable() {
        Employee employee = employee("1");

        assertTrue(employee.isAvailableFor(sundayMorning()), "Employee without submission should be available");
    }

    private static void old_04_availabilitySubmissionStoresShift() {
        AvailabilitySubmission submission = new AvailabilitySubmission();

        submission.addAvailableShift(sundayMorning());

        assertTrue(submission.isAvailableFor(sundayMorning()), "Submission should include added shift");
    }

    private static void old_05_employeeSubmittedAvailabilityRestrictsShift() {
        Employee employee = employee("1");

        employee.addAvailableShift(sundayMorning());

        assertTrue(employee.isAvailableFor(sundayMorning()), "Employee should be available for submitted shift");
        assertFalse(employee.isAvailableFor(mondayMorning()), "Employee should not be available for unsubmitted shift");
    }

    private static void old_06_shiftManagerMustHaveManagerRole() {
        Shift shift = new Shift(sundayMorning());
        Employee employee = employee("1");

        assertFalse(shift.setManager(employee), "Employee without manager role should not become manager");
    }

    private static void old_07_shiftRejectsDuplicateEmployee() {
        Shift shift = new Shift(sundayMorning());
        Employee employee = employee("1");
        employee.addRole("CASHIER");

        assertTrue(shift.addEmployee(employee, "CASHIER"), "First assignment should succeed");
        assertFalse(shift.addEmployee(employee, "CASHIER"), "Duplicate assignment should fail");
    }

    private static void old_08_shiftRejectsAssignmentAboveCapacity() {
        Shift shift = new Shift(sundayMorning());
        Employee first = employee("1");
        Employee second = employee("2");
        first.addRole("CASHIER");
        second.addRole("CASHIER");

        assertTrue(shift.addEmployee(first, "CASHIER"), "First cashier should be assigned");
        assertFalse(shift.addEmployee(second, "CASHIER"), "Second cashier should exceed default capacity");
    }

    private static void old_09_swapEmployeesSwitchesRoles() {
        Shift shift = new Shift(sundayMorning());
        Employee cashier = employee("1");
        Employee stocker = employee("2");
        cashier.addRole("CASHIER");
        cashier.addRole("STOCKER");
        stocker.addRole("CASHIER");
        stocker.addRole("STOCKER");

        shift.addEmployee(cashier, "CASHIER");
        shift.addEmployee(stocker, "STOCKER");

        assertTrue(shift.swapEmployees(cashier, stocker), "Swap should succeed");
        assertEquals("STOCKER", shift.getAssignedEmployees().get(cashier), "Cashier should now have stocker role");
        assertEquals("CASHIER", shift.getAssignedEmployees().get(stocker), "Stocker should now have cashier role");
    }

    private static void old_10_shiftHistoryReturnsLatestWeek() {
        Map<LocalDate, WeeklySchedule> historyMap = new LinkedHashMap<>();
        WeeklySchedule oldWeek = new WeeklySchedule();
        WeeklySchedule newWeek = new WeeklySchedule();
        historyMap.put(LocalDate.of(2026, 1, 1), oldWeek);
        historyMap.put(LocalDate.of(2026, 1, 8), newWeek);
        ShiftHistory history = new ShiftHistory(historyMap);

        assertEquals(newWeek, history.getLastWeek(), "Latest week should be returned");
    }

    private static void new_01_employeeHasBranch() {
        Employee employee = employee("1");

        assertEquals("1", employee.getBranch().getId(), "Employee should belong to branch 1");
    }

    private static void new_02_driverExtendsEmployeeAndHasDriverRole() {
        Driver driver = driver("1", "B");

        assertTrue(driver instanceof Employee, "Driver should be an Employee");
        assertTrue(driver.getRoles().contains("DRIVER"), "Driver should have DRIVER role");
        assertEquals("B", driver.getLicenseType(), "Driver license should be saved");
    }

    private static void new_03_staffingRequirementDefaultRoles() {
        StaffingRequirement requirement = new StaffingRequirement();

        assertEquals(1, requirement.getRequiredAmount("CASHIER"), "Default cashier amount should be 1");
        assertEquals(1, requirement.getRequiredAmount("STOCKER"), "Default stocker amount should be 1");
        assertEquals(0, requirement.getRequiredAmount("DRIVER"), "Driver should not be default requirement");
    }

    private static void new_04_deliveryMockContainsThreeDeliveries() {
        assertEquals(3, DeliveryMock.getDeliveries().size(), "Delivery mock should contain three deliveries");
    }

    private static void new_05_weeklyScheduleAddsDriverRequirementForDeliveryShift() {
        WeeklySchedule schedule = new WeeklySchedule();
        Shift shift = schedule.getShift(sundayMorning());

        assertTrue(shift.hasDelivery(), "Sunday morning should have delivery");
        assertEquals(1, shift.getRequiredRoleAmount("DRIVER"), "Delivery shift should require one driver");
    }

    private static void new_06_weeklyScheduleDoesNotAddDriverRequirementWithoutDelivery() {
        WeeklySchedule schedule = new WeeklySchedule();
        Shift shift = schedule.getShift(mondayMorning());

        assertFalse(shift.hasDelivery(), "Monday morning should not have delivery");
        assertEquals(0, shift.getRequiredRoleAmount("DRIVER"), "Non-delivery shift should not require driver");
    }

    private static void new_07_driverWithMatchingLicenseCanHandleDelivery() {
        Shift shift = new Shift(sundayMorning());
        shift.setDelivery(new Delivery(sundayMorning(), TruckType.SMALL));

        assertTrue(shift.canDriverHandleDelivery(driver("1", "B")),
                "Driver with B license should handle SMALL truck");
    }

    private static void new_08_driverWithWrongLicenseCannotHandleDelivery() {
        Shift shift = new Shift(sundayMorning());
        shift.setDelivery(new Delivery(sundayMorning(), TruckType.HEAVY));

        assertFalse(shift.canDriverHandleDelivery(driver("1", "B")),
                "Driver with B license should not handle HEAVY truck");
    }

    private static void new_09_deliveryShiftCannotSetStockerRequirementToZero() {
        Shift shift = new Shift(sundayMorning());
        shift.setDelivery(new Delivery(sundayMorning(), TruckType.SMALL));

        assertFalse(shift.setRequiredRole("STOCKER", 0),
                "Delivery shift must keep at least one stocker");
    }

    private static void new_10_nonDeliveryShiftCanSetStockerRequirementToZero() {
        Shift shift = new Shift(mondayMorning());

        assertTrue(shift.setRequiredRole("STOCKER", 0),
                "Non-delivery shift may set stocker requirement to zero");
        assertEquals(0, shift.getRequiredRoleAmount("STOCKER"),
                "Stocker amount should be updated to zero");
    }

    private static void db_01_employeeCanBeSavedAndLoaded() {
        EmployeeDAO dao = new EmployeeDAO();
        Employee employee = employee("db-1");
        employee.addRole("CASHIER");

        dao.save(employee);
        Employee loaded = dao.findById("db-1");

        assertTrue(loaded != null, "Employee should be loaded from database");
        assertEquals("Employeedb-1", loaded.getName(), "Employee name should be saved");
        assertEquals("1", loaded.getBranch().getId(), "Employee branch should be saved");
        assertTrue(loaded.getRoles().contains("CASHIER"), "Employee role should be saved");
    }

    private static void db_02_driverLicenseCanBeSavedAndLoaded() {
        EmployeeDAO dao = new EmployeeDAO();
        Driver driver = new Driver(
                "db-driver",
                "Driver",
                LocalDate.of(2026, 1, 1),
                0,
                "Default",
                "000",
                new Branch("1"),
                "C");

        dao.save(driver);
        Employee loaded = dao.findById("db-driver");

        assertTrue(loaded instanceof Driver, "Loaded employee should be Driver");
        assertEquals("C", ((Driver) loaded).getLicenseType(), "Driver license should be saved");
    }

    private static void db_03_availabilityCanBeSavedAndLoaded() {
        EmployeeDAO dao = new EmployeeDAO();
        Employee employee = employee("db-availability");
        ShiftSlot slot = new ShiftSlot(DayOfWeek.SUNDAY, ShiftType.MORNING);

        employee.addAvailableShift(slot);
        dao.save(employee);

        Employee loaded = dao.findById("db-availability");

        assertTrue(loaded.hasSubmittedAvailability(), "Availability submission should be loaded");
        assertTrue(loaded.isAvailableFor(slot), "Available shift should be loaded");
    }

    private static void db_04_roleCanBeSavedAndLoaded() {
        RoleDAO dao = new RoleDAO();

        dao.save("BAKER");
        Set<String> roles = dao.findAll();

        assertTrue(roles.contains("BAKER"), "Saved role should be loaded");
    }

    private static void db_05_currentScheduleCanBeSavedAndLoaded() {
        EmployeeDAO employeeDAO = new EmployeeDAO();
        ScheduleDAO scheduleDAO = new ScheduleDAO();
        Employee employee = employee("db-schedule");
        employee.addRole("CASHIER");
        employeeDAO.save(employee);

        WeeklySchedule schedule = new WeeklySchedule();
        Shift shift = schedule.getShift(new ShiftSlot(DayOfWeek.MONDAY, ShiftType.MORNING));
        shift.addEmployee(employee, "CASHIER");

        scheduleDAO.saveCurrentSchedule(schedule);

        List<Employee> employees = employeeDAO.findAll();
        WeeklySchedule loaded = scheduleDAO.loadCurrentSchedule(employees);
        Shift loadedShift = loaded.getShift(new ShiftSlot(DayOfWeek.MONDAY, ShiftType.MORNING));

        assertEquals(1, loadedShift.getAssignedEmployees().size(),
                "Schedule assignment should be loaded from database");
    }

    private interface TestCase {
        void run();
    }
}
