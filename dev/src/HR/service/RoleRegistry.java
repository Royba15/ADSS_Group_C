package HR.service;

import HR.repository.DaoRoleRepository;
import HR.repository.RoleRepository;

import java.util.LinkedHashSet;
import java.util.Set;

public class RoleRegistry {

    private static Set<String> roles = new LinkedHashSet<>();
    private static RoleRepository roleRepository = new DaoRoleRepository();

    static {
        roles.add("CASHIER");
        roles.add("STOCKER");
        roles.add("DRIVER");
        roles.add("SHIFT_MANAGER");
        roles.addAll(roleRepository.findAll());
    }

    // Add new role
    public static void addRole(String role) {
        String normalizedRole = role.toUpperCase();

        roles.add(normalizedRole);
        roleRepository.save(normalizedRole);
    }

    // Get all roles
    public static Set<String> getRoles() {
        return roles;
    }
}
