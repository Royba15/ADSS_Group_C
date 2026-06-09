package data;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    private static final Path SCHEMA_PATH = Path.of("dev", "db", "schema.sql");
    private static final String[] DATA_TABLES = {
            "shift_history",
            "deliveries",
            "shift_assignments",
            "staffing_requirements",
            "shifts",
            "current_schedule",
            "weekly_schedules",
            "availability_shift_slots",
            "availability_submissions",
            "employee_roles",
            "roles",
            "drivers",
            "employees",
            "branches",
            "truck_types"
    };

    public static void initialize() {
        try {
            Files.createDirectories(Path.of("data"));
            String schema = Files.readString(SCHEMA_PATH, StandardCharsets.UTF_8);

            try (Connection connection = DatabaseConnection.getConnection();
                 Statement statement = connection.createStatement()) {

                for (String command : schema.split(";")) {
                    String sql = command.trim();

                    if (!sql.isEmpty()) {
                        statement.execute(sql);
                    }
                }
            }
        } catch (IOException | SQLException e) {
            throw new RuntimeException("Failed to initialize database", e);
        }
    }

    public static void clearData() {
        initialize();

        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement()) {

            connection.setAutoCommit(false);

            try {
                for (String table : DATA_TABLES) {
                    statement.executeUpdate("DELETE FROM " + table);
                }

                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to clear database data", e);
        }
    }
}

