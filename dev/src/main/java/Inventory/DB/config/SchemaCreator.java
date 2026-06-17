package Inventory.DB.config;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class SchemaCreator {

    public static void createTables() throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        try (Statement st = conn.createStatement()) {

            st.execute("""
                CREATE TABLE IF NOT EXISTS categories (
                    name  TEXT    PRIMARY KEY,
                    level INTEGER NOT NULL
                )
            """);

            st.execute("""
                CREATE TABLE IF NOT EXISTS products (
                    product_id             INTEGER PRIMARY KEY,
                    name                   TEXT    NOT NULL,
                    manufacturer_id        INTEGER NOT NULL,
                    cost_price             REAL    NOT NULL,
                    selling_price          REAL    NOT NULL,
                    original_selling_price REAL    NOT NULL,
                    supplier_catalog_id    TEXT,
                    main_category          TEXT REFERENCES categories(name),
                    sub_category           TEXT REFERENCES categories(name),
                    sub_sub_category       TEXT REFERENCES categories(name)
                )
            """);

            st.execute("""
                CREATE TABLE IF NOT EXISTS inventory_levels (
                    product_id             INTEGER PRIMARY KEY
                                           REFERENCES products(product_id),
                    shelf_quantity         INTEGER NOT NULL DEFAULT 0,
                    warehouse_quantity     INTEGER NOT NULL DEFAULT 0,
                    min_quantity_threshold INTEGER NOT NULL DEFAULT 0,
                    location               TEXT    NOT NULL
                )
            """);

            st.execute("""
                CREATE TABLE IF NOT EXISTS defective_items (
                    id         INTEGER PRIMARY KEY AUTOINCREMENT,
                    product_id INTEGER NOT NULL REFERENCES products(product_id),
                    quantity   INTEGER NOT NULL,
                    reason     TEXT    NOT NULL
                )
            """);

            st.execute("""
                CREATE TABLE IF NOT EXISTS discount_promotions (
                    id                  INTEGER PRIMARY KEY AUTOINCREMENT,
                    product_id          INTEGER NOT NULL REFERENCES products(product_id),
                    promo_name          TEXT    NOT NULL,
                    discount_percentage REAL    NOT NULL,
                    start_time          TEXT    NOT NULL,
                    end_time            TEXT    NOT NULL
                )
            """);

            // טבלת הזמנות ספקים — חדשה

            // החלף את CREATE TABLE של supplier_orders:
            st.execute("""
                CREATE TABLE IF NOT EXISTS supplier_orders (
                    order_id            INTEGER PRIMARY KEY AUTOINCREMENT,
                    product_id          INTEGER NOT NULL,
                    product_name        TEXT    NOT NULL,
                    supplier_id         INTEGER NOT NULL,
                    supplier_catalog_id TEXT,
                    quantity            INTEGER NOT NULL,
                    status              TEXT    NOT NULL DEFAULT 'CREATED',
                    order_type          TEXT    NOT NULL DEFAULT 'IMMEDIATE',
                    scheduled_date      TEXT,
                    frequency           TEXT,
                    created_at          TEXT    NOT NULL
                )
            """);

            System.out.println("[DB] All tables created / verified.");
        }
    }
}