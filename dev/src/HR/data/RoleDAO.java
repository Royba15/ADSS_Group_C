package HR.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.Set;

public class RoleDAO {

    public Set<String> findAll() {
        DatabaseInitializer.initialize();

        String sql = "SELECT name FROM roles ORDER BY name";
        Set<String> roles = new LinkedHashSet<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                roles.add(resultSet.getString("name"));
            }

            return roles;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load roles", e);
        }
    }

    public void save(String role) {
        DatabaseInitializer.initialize();

        String sql = "INSERT OR IGNORE INTO roles(name) VALUES (?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, role.toUpperCase());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save role", e);
        }
    }
}
