package domain;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class StaffingRequirement {

    // Records the required number of employees per role per shift
    private Map<String, Integer> requiredEmployeesPerRole;

    public StaffingRequirement() {
        this.requiredEmployeesPerRole = new LinkedHashMap<>();

        // Default requirement: one employee for each regular role
        requiredEmployeesPerRole.put("CASHIER", 1);
        requiredEmployeesPerRole.put("STOCKER", 1);
    }

    public void setRequiredRole(String role, int amount) {
        requiredEmployeesPerRole.put(role, amount);
    }

    public int getRequiredAmount(String role) {
        return requiredEmployeesPerRole.getOrDefault(role, 0);
    }

    public Set<String> getRequiredRoles() {
        return requiredEmployeesPerRole.keySet();
    }
}
