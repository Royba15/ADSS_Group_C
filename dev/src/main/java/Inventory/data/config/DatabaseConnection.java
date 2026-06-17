package Inventory.data.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Singleton class to manage the SQLite database connection.
 */
public class DatabaseConnection {

    private static final String URL = "jdbc:sqlite:inventory.db";
    private static Connection connection = null;

    private DatabaseConnection() {}

    // Returns a single active connection, creating it if necessary
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL);
            try (Statement st = connection.createStatement()) {
                st.execute("PRAGMA foreign_keys = ON");
            }
        }
        return connection;
    }

    // Safely closes the current connection
    public static void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                connection = null;
            }
        } catch (SQLException ignored) {}
    }
}
