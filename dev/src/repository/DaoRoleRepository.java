package repository;

import data.RoleDAO;

import java.util.Set;

public class DaoRoleRepository implements RoleRepository {

    private final RoleDAO roleDAO;

    public DaoRoleRepository() {
        this.roleDAO = new RoleDAO();
    }

    @Override
    public Set<String> findAll() {
        return roleDAO.findAll();
    }

    @Override
    public void save(String role) {
        roleDAO.save(role);
    }
}
