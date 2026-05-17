package service;

import data.RoleDAO;

import java.util.LinkedHashSet;
import java.util.Set;

public class RoleRegistry {

    private static Set<String> roles = new LinkedHashSet<>();
    private static RoleDAO roleDAO = new RoleDAO();

    static {
        roles.add("CASHIER");
        roles.add("STOCKER");
        roles.add("DRIVER");
        roles.add("SHIFT_MANAGER");
        roles.addAll(roleDAO.findAll());
    }

    // Add new role
    public static void addRole(String role) {
        String normalizedRole = role.toUpperCase();

        roles.add(normalizedRole);
        roleDAO.save(normalizedRole);
    }

    // Get all roles
    public static Set<String> getRoles() {
        return roles;
    }
}
