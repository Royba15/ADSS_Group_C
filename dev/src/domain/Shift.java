package domain;

import java.util.*;

public class Shift {

    // Represents the time of the shift (day + type: morning/evening)
    private ShiftSlot slot;

    // Maps each employee to the role they perform in this shift
    private Map<Employee, String> assignedEmployees;

    // Defines how many employees are required for each role (excluding manager)
    private Map<String, Integer> requiredRoles;

    // The shift manager (only one allowed)
    private Employee manager;

    //if the super closed in this shift
    private boolean blocked = false;

    public Shift(ShiftSlot slot) {
        this.slot = slot;
        this.assignedEmployees = new LinkedHashMap<>();
        this.requiredRoles = new LinkedHashMap<>();
        this.manager = null;

        // Default requirement: 1 employee per role (excluding manager)
        requiredRoles.put("CASHIER", 1);
        requiredRoles.put("STOCKER", 1);
    }

    // Allows HR manager to set required number of employees per role
    public void setRequiredRole(String role, int amount) {
        requiredRoles.put(role, amount);
    }

    // Assign a shift manager (only one allowed)
    public boolean setManager(Employee e) {

        //closed
        if (blocked)
            return false;

        // Check if a manager is already assigned
        if (manager != null) {
            return false;
        }

        // Check if employee is qualified to be a manager
        if (!e.getRoles().contains("SHIFT_MANAGER")) {
            return false;
        }

        // Check if employee is available for this shift
        if (e.hasSubmittedAvailability() && !e.getAvailableShifts().contains(slot)) {
            return false;
        }
        if (!e.isActive()) {
            return false;
        }

        // Assign manager
        manager = e;
        return true;
    }


    // Assign a regular employee to the shift
    public boolean addEmployee(Employee e, String role) {

        // Manager should not be added as a regular employee
        if (Objects.equals(role, "SHIFT_MANAGER")) {
            return false;
        }

        //branch close
        if (blocked) {
            return false;
        }

        // Check if employee is already assigned
        if (assignedEmployees.containsKey(e)) {
            return false;
        }

        // Check if employee has the required role
        if (!e.getRoles().contains(role)) {
            return false;
        }

        // Check if employee is available for this shift
        if (e.hasSubmittedAvailability() && !e.getAvailableShifts().contains(slot)) {
            return false;
        }

        // Check if the required number of employees for this role is reached
        int currentCount = countRole(role);
        int requiredCount = requiredRoles.getOrDefault(role, 0);

        if (currentCount >= requiredCount) {
            return false;
        }
        // Check if employee is active
        if (!e.isActive()) {
            return false;
        }

        // Assign employee
        assignedEmployees.put(e, role);
        return true;
    }
    // count how many times a role appears in the assignedEmployees map
    private int countRole(String role) {
        int count = 0;

        for (String r : assignedEmployees.values()) {
            if (r.equals(role)) {
                count++;
            }
        }

        return count;
    }
    public Employee getManager() {
        return manager;
    }

    public Map<Employee, String> getAssignedEmployees() {
        return assignedEmployees;
    }
    // Check if shift is fully staffed
    public boolean isFullyStaffed() {

        if (blocked)
            return true;

        // Check manager
        if (manager == null) {
            return false;
        }

        // Count employees per role
        Map<String, Integer> count = new LinkedHashMap<>();

        for (String r : requiredRoles.keySet()) {
            count.put(r, 0);
        }

        for (Employee e : assignedEmployees.keySet()) {
            String r = assignedEmployees.get(e);
            count.put(r, count.get(r) + 1);
        }

        // Validate requirements
        for (String r : requiredRoles.keySet()) {
            if (count.get(r) < requiredRoles.get(r)) {
                return false;
            }
        }

        return true;
    }

    // Returns how many employees are still needed for each role
    public Map<String, Integer> getRemainingRoles() {

        Map<String, Integer> remaining = new LinkedHashMap<>();

        // Count current assignments
        Map<String, Integer> current = new LinkedHashMap<>();

        for (String r : requiredRoles.keySet()) {
            current.put(r, 0);
        }

        for (Employee e : assignedEmployees.keySet()) {
            String r = assignedEmployees.get(e);
            current.put(r, current.get(r) + 1);
        }

        // Calculate remaining
        for (String r : requiredRoles.keySet()) {
            int need = requiredRoles.get(r);
            int have = current.get(r);
            remaining.put(r, Math.max(0, need - have));
        }

        return remaining;
    }

    // Swap roles between two employees in the same shift
    public boolean swapEmployees(Employee e1, Employee e2) {

        // Both must exist in the shift
        if (!assignedEmployees.containsKey(e1) || !assignedEmployees.containsKey(e2)) {
            return false;
        }

        String r1 = assignedEmployees.get(e1);
        String r2 = assignedEmployees.get(e2);

        // Swap roles
        assignedEmployees.put(e1, r2);
        assignedEmployees.put(e2, r1);

        return true;
    }

    // Check if employee is assigned to this shift
    public boolean containsEmployee(Employee e) {
        return assignedEmployees.containsKey(e);
    }

    public void removeEmployee(Employee e) {
        assignedEmployees.remove(e);
    }

    // Returns the slot (day + type) of this shift
    public ShiftSlot getSlot() {
        return this.slot;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }
}