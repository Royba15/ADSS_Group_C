package Inventory.DB.impl;

import Inventory.DB.config.DatabaseConnection;
import Inventory.DB.dao.ProductDAO;
import Inventory.dto.ProductDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * כל ה-SQL של מוצרים נמצא כאן בלבד.
 */
public class JdbcProductDAO implements ProductDAO {

    @Override
    public void save(ProductDTO dto) throws SQLException {
        String sqlP = """
            INSERT OR IGNORE INTO products(
                product_id, name, manufacturer_id,
                cost_price, selling_price, original_selling_price,
                supplier_catalog_id,
                main_category, sub_category, sub_sub_category)
            VALUES(?,?,?,?,?,?,?,?,?,?)
        """;
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sqlP)) {
            ps.setInt(1,    dto.productId());
            ps.setString(2, dto.name());
            ps.setInt(3,    dto.supplierId());
            ps.setDouble(4, dto.costPrice());
            ps.setDouble(5, dto.sellingPrice());
            ps.setDouble(6, dto.originalSellingPrice());
            ps.setString(7, dto.supplierCatalogId());
            ps.setString(8, dto.mainCategory());
            ps.setString(9, dto.subCategory());
            ps.setString(10,dto.subSubCategory());
            ps.executeUpdate();
        }
        String sqlI = """
            INSERT OR IGNORE INTO inventory_levels(
                product_id, shelf_quantity, warehouse_quantity,
                min_quantity_threshold, location)
            VALUES(?,?,?,?,?)
        """;
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sqlI)) {
            ps.setInt(1,    dto.productId());
            ps.setInt(2,    dto.shelfQuantity());
            ps.setInt(3,    dto.warehouseQuantity());
            ps.setInt(4,    dto.minQuantityThreshold());
            ps.setString(5, dto.location());
            ps.executeUpdate();
        }
    }

    @Override
    public void update(ProductDTO dto) throws SQLException {
        String sql = """
            UPDATE products SET name=?, manufacturer_id=?,
                cost_price=?, selling_price=?, original_selling_price=?,
                supplier_catalog_id=?,
                main_category=?, sub_category=?, sub_sub_category=?
            WHERE product_id=?
        """;
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, dto.name());
            ps.setInt(2,    dto.supplierId());
            ps.setDouble(3, dto.costPrice());
            ps.setDouble(4, dto.sellingPrice());
            ps.setDouble(5, dto.originalSellingPrice());
            ps.setString(6, dto.supplierCatalogId());
            ps.setString(7, dto.mainCategory());
            ps.setString(8, dto.subCategory());
            ps.setString(9, dto.subSubCategory());
            ps.setInt(10,   dto.productId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(int productId) throws SQLException {
        try (PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement("DELETE FROM inventory_levels WHERE product_id=?")) {
            ps.setInt(1, productId);
            ps.executeUpdate();
        }
        try (PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement("DELETE FROM products WHERE product_id=?")) {
            ps.setInt(1, productId);
            ps.executeUpdate();
        }
    }

    @Override
    public Optional<ProductDTO> findById(int productId) throws SQLException {
        String sql = """
            SELECT p.*, il.shelf_quantity, il.warehouse_quantity,
                   il.min_quantity_threshold, il.location
            FROM products p
            LEFT JOIN inventory_levels il ON p.product_id = il.product_id
            WHERE p.product_id = ?
        """;
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        }
        return Optional.empty();
    }

    @Override
    public List<ProductDTO> findAll() throws SQLException {
        List<ProductDTO> list = new ArrayList<>();
        String sql = """
            SELECT p.*, il.shelf_quantity, il.warehouse_quantity,
                   il.min_quantity_threshold, il.location
            FROM products p
            LEFT JOIN inventory_levels il ON p.product_id = il.product_id
        """;
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    @Override
    public List<ProductDTO> findBelowThreshold() throws SQLException {
        List<ProductDTO> list = new ArrayList<>();
        String sql = """
            SELECT p.*, il.shelf_quantity, il.warehouse_quantity,
                   il.min_quantity_threshold, il.location
            FROM products p
            JOIN inventory_levels il ON p.product_id = il.product_id
            WHERE (il.shelf_quantity + il.warehouse_quantity) < il.min_quantity_threshold
        """;
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    @Override
    public List<ProductDTO> findByCategory(String categoryName) throws SQLException {
        List<ProductDTO> list = new ArrayList<>();
        String sql = """
            SELECT p.*, il.shelf_quantity, il.warehouse_quantity,
                   il.min_quantity_threshold, il.location
            FROM products p
            LEFT JOIN inventory_levels il ON p.product_id = il.product_id
            WHERE p.main_category = ?
               OR p.sub_category = ?
               OR p.sub_sub_category = ?
        """;
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, categoryName);
            ps.setString(2, categoryName);
            ps.setString(3, categoryName);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    @Override
    public List<ProductDTO> findBySupplier(int supplierId) throws SQLException {
        List<ProductDTO> list = new ArrayList<>();
        String sql = """
            SELECT p.*, il.shelf_quantity, il.warehouse_quantity,
                   il.min_quantity_threshold, il.location
            FROM products p
            LEFT JOIN inventory_levels il ON p.product_id = il.product_id
            WHERE p.manufacturer_id = ?
        """;
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, supplierId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    @Override
    public void updateInventoryQuantity(int productId, int shelf, int warehouse) throws SQLException {
        String sql = """
            UPDATE inventory_levels
            SET shelf_quantity=?, warehouse_quantity=?
            WHERE product_id=?
        """;
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, shelf);
            ps.setInt(2, warehouse);
            ps.setInt(3, productId);
            ps.executeUpdate();
        }
    }

    @Override
    public void updateSellingPrice(int productId, double price) throws SQLException {
        String sql = "UPDATE products SET selling_price=? WHERE product_id=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setDouble(1, price);
            ps.setInt(2, productId);
            ps.executeUpdate();
        }
    }

    @Override
    public boolean existsById(int productId) throws SQLException {
        String sql = "SELECT 1 FROM products WHERE product_id=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private ProductDTO mapRow(ResultSet rs) throws SQLException {
        return new ProductDTO(
                rs.getInt("product_id"),
                rs.getString("name"),
                rs.getInt("manufacturer_id"),
                rs.getDouble("cost_price"),
                rs.getDouble("selling_price"),
                rs.getDouble("original_selling_price"),
                rs.getString("supplier_catalog_id"),
                rs.getString("main_category"),
                rs.getString("sub_category"),
                rs.getString("sub_sub_category"),
                rs.getInt("shelf_quantity"),
                rs.getInt("warehouse_quantity"),
                rs.getInt("min_quantity_threshold"),
                rs.getString("location")
        );
    }
}
