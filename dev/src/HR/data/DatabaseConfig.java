package HR.data;

import java.nio.file.Path;

public class DatabaseConfig {

    private static final String DEFAULT_DATABASE_PATH = Path.of(
            System.getProperty("java.io.tmpdir"),
            "superli",
            "superli-main.db").toString();

    public static String getDatabasePath() {
        return System.getProperty("superli.db.path", DEFAULT_DATABASE_PATH);
    }

    public static String getDatabaseUrl() {
        return "jdbc:sqlite:" + getDatabasePath() + "?foreign_keys=on&journal_mode=MEMORY&synchronous=OFF";
    }
}
