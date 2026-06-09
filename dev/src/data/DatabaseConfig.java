package data;

public class DatabaseConfig {

    private static final String DEFAULT_DATABASE_PATH = "data/superli-main.db";

    public static String getDatabaseUrl() {
        String path = System.getProperty("superli.db.path", DEFAULT_DATABASE_PATH);
        return "jdbc:sqlite:" + path;
    }
}

