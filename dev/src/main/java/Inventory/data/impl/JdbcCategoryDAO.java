package Inventory.data.impl;

import Inventory.data.config.DatabaseConnection;
import Inventory.data.dao.CategoryDAO;
import Inventory.dto.CategoryDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 * JDBC implementation of CategoryDAO for managing categories in the database.
 */
public class JdbcCategoryDAO implements CategoryDAO {

    @Override
    public void save(CategoryDTO dto) throws SQLException {
        String sql = "INSERT OR IGNORE INTO categories(name, level) VALUES(?,?)";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, dto.name());
            ps.setInt(2,    dto.level());
            ps.executeUpdate();
        }
    }

    @Override
    public Optional<CategoryDTO> findByName(String name) throws SQLException {
        String sql = "SELECT name, level FROM categories WHERE name=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return Optional.of(new CategoryDTO(rs.getString("name"), rs.getInt("level")));
            }
        }
        return Optional.empty();
    }

    @Override
    public List<CategoryDTO> findAll() throws SQLException {
        List<CategoryDTO> list = new ArrayList<>();
        String sql = "SELECT name, level FROM categories";
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next())
                list.add(new CategoryDTO(rs.getString("name"), rs.getInt("level")));
        }
        return list;
    }

    @Override
    public List<CategoryDTO> findByLevel(int level) throws SQLException {
        List<CategoryDTO> list = new ArrayList<>();
        String sql = "SELECT name, level FROM categories WHERE level=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, level);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next())
                    list.add(new CategoryDTO(rs.getString("name"), rs.getInt("level")));
            }
        }
        return list;
    }
}
