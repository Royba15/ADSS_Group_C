package service;

import java.util.*;

import domain.Employee;
import domain.ShiftSlot;

public class EmployeeManager {

    // Stores all employees in the system (by ID)
    private Map<String, Employee> employees;

    public EmployeeManager() {
        this.employees = new HashMap<>();
    }

    // Add a new employee to the system
    public void addEmployee(Employee e) {
        employees.put(e.getId(), e);
    }

    // Get employee by ID
    public Employee getEmployee(String id) {
        return employees.get(id);
    }

    // Returns all employees available for a given slot, grouped by role
    public Map<String, List<Employee>> getAvailableEmployees(ShiftSlot slot) {

        Map<String, List<Employee>> result = new HashMap<>();

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

        if (!e.hasSubmittedAvailability()) {
            return true;
        }

        return e.getAvailableShifts().contains(slot);
    }
    public void resetAllEmployeesAvailability() {

        for (Employee e : employees.values()) {

            e.getAvailableShifts().clear();
            e.setSubmittedAvailability(false);
        }
    }
}