package Inventory.data.dao;

import Inventory.dto.CategoryDTO;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for managing category data.
 */
public interface CategoryDAO {
    void save(CategoryDTO dto) throws SQLException;
    Optional<CategoryDTO> findByName(String name) throws SQLException;
    List<CategoryDTO> findAll() throws SQLException;
    List<CategoryDTO> findByLevel(int level) throws SQLException;
}
