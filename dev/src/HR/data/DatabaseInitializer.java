package HR.data;

import java.io.InputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    private static final String SCHEMA_RESOURCE = "schema.sql";
    private static final Path SCHEMA_FALLBACK_PATH = Path.of("dev", "db", "schema.sql");
    private static String initializedDatabaseUrl = null;
    private static boolean initializing = false;
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
            "drivers",
            "employees",
            "branches"
    };

    public static void initialize() {
        ensureInitialized();
    }

    public static synchronized void ensureInitialized() {
        String databaseUrl = DatabaseConfig.getDatabaseUrl();
        Path databasePath = Path.of(DatabaseConfig.getDatabasePath());

        if (databaseUrl.equals(initializedDatabaseUrl)) {
            return;
        }

        try {
            initializing = true;
            Path parentDirectory = databasePath.getParent();
            if (parentDirectory != null) {
                Files.createDirectories(parentDirectory);
            }

            try (Connection connection = DatabaseConnection.getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement(
                        "SELECT name FROM sqlite_master WHERE type = 'table' AND name = 'employees'");
                     ResultSet resultSet = statement.executeQuery()) {

                    if (resultSet.next()) {
                        initializedDatabaseUrl = databaseUrl;
                        return;
                    }
                }
            }

            deleteIfPossible(Path.of(databasePath + "-journal"));
            deleteIfPossible(Path.of(databasePath + "-wal"));
            deleteIfPossible(Path.of(databasePath + "-shm"));

            String schema = stripSqlComments(loadSchema());

            try (Connection connection = DatabaseConnection.getConnection();
                 Statement statement = connection.createStatement()) {

                for (String command : schema.split(";")) {
                    String sql = command.trim();

                    if (!sql.isEmpty()) {
                        statement.execute(sql);
                    }
                }
            }

            initializedDatabaseUrl = databaseUrl;
        } catch (IOException | SQLException e) {
            throw new RuntimeException("Failed to initialize database", e);
        } finally {
            initializing = false;
        }
    }

    static boolean isInitializing() {
        return initializing;
    }

    private static void deleteIfPossible(Path path) throws IOException {
        try {
            Files.deleteIfExists(path);
        } catch (AccessDeniedException ignored) {
            // If another process still holds a stale journal file, SQLite can usually recover without it.
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

                reseedReferenceData(statement);

                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to clear database data", e);
        }
    }

    private static void reseedReferenceData(Statement statement) throws SQLException {
        statement.executeUpdate("INSERT OR IGNORE INTO roles(name) VALUES ('CASHIER')");
        statement.executeUpdate("INSERT OR IGNORE INTO roles(name) VALUES ('STOCKER')");
        statement.executeUpdate("INSERT OR IGNORE INTO roles(name) VALUES ('DRIVER')");
        statement.executeUpdate("INSERT OR IGNORE INTO roles(name) VALUES ('SHIFT_MANAGER')");

        statement.executeUpdate(
                "INSERT OR IGNORE INTO truck_types(name, required_license_type) VALUES ('SMALL', 'B')");
        statement.executeUpdate(
                "INSERT OR IGNORE INTO truck_types(name, required_license_type) VALUES ('HEAVY', 'C')");
    }

    private static String stripSqlComments(String schema) {
        StringBuilder builder = new StringBuilder();

        for (String line : schema.split("\\R")) {
            String trimmed = line.trim();

            if (!trimmed.startsWith("--")) {
                builder.append(line).append(System.lineSeparator());
            }
        }

        return builder.toString();
    }

    private static String loadSchema() throws IOException {
        try (InputStream inputStream = DatabaseInitializer.class.getResourceAsStream(SCHEMA_RESOURCE)) {
            if (inputStream != null) {
                return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            }
        }

        return Files.readString(SCHEMA_FALLBACK_PATH, StandardCharsets.UTF_8);
    }
}
