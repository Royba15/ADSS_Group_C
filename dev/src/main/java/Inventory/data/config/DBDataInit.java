package Inventory.data.config;

import java.sql.*;

/**
 * Handles initial database seeding on first run.
 */
public class DBDataInit {
    // Triggers all initialization methods if the DB is empty
    public static void init() throws SQLException {
        if (isAlreadyInitialized()) {
            System.out.println("[DB] Data already initialized – skipping seed.");
            return;
        }
        System.out.println("[DB] Seeding initial data...");
        initCategories();
        initProducts();
        initInventoryLevels();
        initDefectiveItems();
        System.out.println("[DB] Seed complete.");
    }

    // Checks if the tables contain data to avoid duplicate
    private static boolean isAlreadyInitialized() throws SQLException {
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM products")) {
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    // Populates product category hierarchy
    private static void initCategories() throws SQLException {
        String sql = "INSERT OR IGNORE INTO categories(name, level) VALUES(?, ?)";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            Object[][] cats = {
                {"Dairy",0}, {"Milk",1}, {"3%",2}, {"Cheese",1}, {"Cheddar",2},
                {"Cream",1}, {"Butter",2},
                {"Toiletries",0}, {"Shampoo",1}, {"250ml",2}, {"Soap",1}, {"Body Soap",2},
                {"Beverages",0}, {"Juice",1}, {"Orange Juice",2}, {"Coffee",1}, {"Instant",2},
                {"Cola",1}, {"Carbonated",2},
                {"Snacks",0}, {"Chips",1}, {"Salted",2}, {"Popcorn",1}, {"Buttered",2},
                {"Nuts",1}, {"Roasted",2}, {"Corn",1},
                {"Bread",0}, {"White Bread",1}, {"Pita",2}, {"Brown Bread",1}, {"Wheat",2}
            };
            for (Object[] c : cats) {
                ps.setString(1, (String) c[0]);
                ps.setInt(2, (int) c[1]);
                ps.executeUpdate();
            }
        }
    }

    // Inserts base product catalog
    private static void initProducts() throws SQLException {
        String sql = """
            INSERT OR IGNORE INTO products(
                product_id, name, manufacturer_id,
                cost_price, selling_price, original_selling_price,
                supplier_catalog_id,
                main_category, sub_category, sub_sub_category)
            VALUES(?,?,?,?,?,?,?,?,?,?)
        """;
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            Object[][] prods = {
                {1,  "Tnuva 3% Milk",              101, 4.50,  6.90,  "CAT-001", "Dairy",      "Milk",        "3%"          },
                {2,  "Pinuk Shampoo 250ml",         202, 8.00,  12.90, "CAT-002", "Toiletries", "Shampoo",     "250ml"       },
                {3,  "Tnuva Cream Cheese 500ml",    101, 20.50, 30.90, "CAT-003", "Dairy",      "Cream",       "Butter"      },
                {4,  "Tara Cheddar Cheese 200g",    102, 18.00, 28.50, "CAT-004", "Dairy",      "Cheese",      "Cheddar"     },
                {5,  "Strauss Orange Juice 1L",     103, 5.50,  8.90,  "CAT-005", "Beverages",  "Juice",       "Orange Juice"},
                {6,  "Nescafe Instant Coffee 200g", 104, 35.00, 52.90, "CAT-006", "Beverages",  "Coffee",      "Instant"     },
                {7,  "Bissli Cheese Flavored 100g", 105, 3.50,  5.90,  "CAT-007", "Snacks",     "Chips",       "Salted"      },
                {8,  "Nestle Popcorn 150g",         106, 6.00,  9.90,  "CAT-008", "Snacks",     "Popcorn",     "Buttered"    },
                {9,  "Almond Nuts 500g",            107, 25.00, 39.90, "CAT-009", "Snacks",     "Nuts",        "Roasted"     },
                {10, "White Bread Pita Pack",       108, 4.00,  6.50,  "CAT-010", "Bread",      "White Bread", "Pita"        },
                {11, "Brown Bread Whole Wheat",     109, 5.00,  7.90,  "CAT-011", "Bread",      "Brown Bread", "Wheat"       },
                {12, "Palmolive Liquid Soap 500ml", 110, 7.50,  11.90, "CAT-012", "Toiletries", "Soap",        "Body Soap"   },
                {13, "Danone Yogurt 200g",          111, 3.00,  4.90,  "CAT-013", "Dairy",      "Milk",        "3%"          },
                {14, "Sabra Hummus 250g",           112, 8.00,  12.90, "CAT-014", "Dairy",      "Cream",       "Butter"      },
                {15, "Coca-Cola 1.5L",              113, 5.00,  7.90,  "CAT-015", "Beverages",  "Cola",        "Carbonated"  },
                {16, "Lay's Classic Chips 150g",    114, 4.50,  7.50,  "CAT-016", "Snacks",     "Chips",       "Salted"      },
                {17, "Cashew Nuts 400g",            115, 30.00, 45.90, "CAT-017", "Snacks",     "Nuts",        "Roasted"     },
                {18, "Bamba Corn Snack 125g",       116, 2.50,  3.99,  "CAT-018", "Snacks",     "Corn",        "Salted"      },
            };
            for (Object[] p : prods) {
                ps.setInt(1,    (int)    p[0]);
                ps.setString(2, (String) p[1]);
                ps.setInt(3,    (int)    p[2]);
                ps.setDouble(4, (double) p[3]);
                ps.setDouble(5, (double) p[4]);
                ps.setDouble(6, (double) p[4]);
                ps.setString(7, (String) p[5]);
                ps.setString(8, (String) p[6]);
                ps.setString(9, (String) p[7]);
                ps.setString(10,(String) p[8]);
                ps.executeUpdate();
            }
        }
    }

    // Sets initial stock levels and warehouse locations
    private static void initInventoryLevels() throws SQLException {
        String sql = """
            INSERT OR IGNORE INTO inventory_levels(
                product_id, shelf_quantity, warehouse_quantity,
                min_quantity_threshold, location)
            VALUES(?,?,?,?,?)
        """;
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            // id, shelf, warehouse, minThreshold, location
            Object[][] levels = {
                {1,  3,  2,  10, "Aisle 1"},
                {2,  10, 10, 5,  "Aisle 3"},
                {3,  0,  0,  3,  "Aisle 1"},
                {4,  8,  5,  4,  "Aisle 2"},
                {5,  15, 20, 8,  "Aisle 4"},
                {6,  12, 8,  6,  "Aisle 5"},
                {7,  25, 15, 12, "Aisle 6"},
                {8,  0,  0,  8,  "Aisle 6"},
                {9,  7,  8,  16, "Aisle 7"},
                {10, 20, 10, 8,  "Aisle 8"},
                {11, 15, 12, 30, "Aisle 8"},
                {12, 0,  0,  8,  "Aisle 3"},
                {13, 20, 25, 15, "Aisle 2"},
                {14, 8,  6,  5,  "Aisle 2"},
                {15, 30, 40, 20, "Aisle 4"},
                {16, 22, 18, 10, "Aisle 6"},
                {17, 5,  7,  15, "Aisle 7"},
                {18, 28, 22, 15, "Aisle 6"},
            };
            for (Object[] l : levels) {
                ps.setInt(1,    (int)    l[0]);
                ps.setInt(2,    (int)    l[1]);
                ps.setInt(3,    (int)    l[2]);
                ps.setInt(4,    (int)    l[3]);
                ps.setString(5, (String) l[4]);
                ps.executeUpdate();
            }
        }
    }

    // Records initial damaged or expired stock
    private static void initDefectiveItems() throws SQLException {
        String sql = "INSERT INTO defective_items(product_id, quantity, reason) VALUES(?,?,?)";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            Object[][] defectives = {
                {1,  2, "Expired"},
                {5,  1, "Damaged packaging"},
                {10, 1, "Moldy"},
            };
            for (Object[] d : defectives) {
                ps.setInt(1,    (int)    d[0]);
                ps.setInt(2,    (int)    d[1]);
                ps.setString(3, (String) d[2]);
                ps.executeUpdate();
            }
        }
    }
}
