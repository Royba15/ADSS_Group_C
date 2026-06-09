package repository;

import java.util.Set;

public interface RoleRepository {

    Set<String> findAll();

    void save(String role);
}
