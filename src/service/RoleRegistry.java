package service;

import java.util.HashSet;
import java.util.Set;

public class RoleRegistry {

    private static Set<String> roles = new HashSet<>();

    // Add new role
    public static void addRole(String role) {
        roles.add(role.toUpperCase());
    }

    // Get all roles
    public static Set<String> getRoles() {
        return roles;
    }
}