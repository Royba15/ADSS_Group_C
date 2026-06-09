package data;

import org.sqlite.SQLiteConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    public static Connection getConnection() throws SQLException {
        SQLiteConfig config = new SQLiteConfig();
        config.enforceForeignKeys(true);
        config.setJournalMode(SQLiteConfig.JournalMode.OFF);
        config.setSynchronous(SQLiteConfig.SynchronousMode.OFF);

        return DriverManager.getConnection(DatabaseConfig.getDatabaseUrl(), config.toProperties());
    }
}

