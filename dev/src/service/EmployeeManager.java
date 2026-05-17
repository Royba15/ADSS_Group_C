package service;

import java.util.*;

import data.EmployeeDAO;
import domain.Employee;
import domain.ShiftSlot;

public class EmployeeManager {

    // Stores all employees in the system (by ID)
    private Map<String, Employee> employees;
    private EmployeeDAO employeeDAO;

    public EmployeeManager() {
        this.employees = new LinkedHashMap<>();
        this.employeeDAO = new EmployeeDAO();

        for (Employee employee : employeeDAO.findAll()) {
            employees.put(employee.getId(), employee);
        }
    }

    // Add a new employee to the system
    public void addEmployee(Employee e) {
        employees.put(e.getId(), e);
        employeeDAO.save(e);
    }

    // Save changes made to an existing employee
    public void saveEmployee(Employee e) {
        employees.put(e.getId(), e);
        employeeDAO.save(e);
    }

    // Get employee by ID
    public Employee getEmployee(String id) {
        return employees.get(id);
    }

    // Returns all employees available for a given slot, grouped by role
    public Map<String, List<Employee>> getAvailableEmployees(ShiftSlot slot) {

        Map<String, List<Employee>> result = new LinkedHashMap<>();

        for (Employee e : employees.values()) {

            if (e.isActive() && isEmployeeAvailable(e, slot)){
                for (String role : e.getRoles()) {

                    result.putIfAbsent(role, new ArrayList<>());
                    result.get(role).add(e);
                }
            }
        }

        return result;
    }
    public Collection<Employee> getAllEmployees() {
        return employees.values();
    }
    // Check if employee is available for a slot
    private boolean isEmployeeAvailable(Employee e, ShiftSlot slot) {

        return e.isAvailableFor(slot);
    }
    public void resetAllEmployeesAvailability() {

        for (Employee e : employees.values()) {

            e.clearAvailabilitySubmission();
            employeeDAO.save(e);
        }
    }
}
