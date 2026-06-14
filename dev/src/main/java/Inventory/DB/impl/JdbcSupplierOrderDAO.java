package Inventory.DB.impl;

import Inventory.DB.DatabaseConnection;
import Inventory.DB.dao.SupplierOrderDAO;
import Inventory.dto.SupplierOrderDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * כל ה-SQL של הזמנות ספקים נמצא כאן בלבד.
 */
public class JdbcSupplierOrderDAO implements SupplierOrderDAO {

    public int save(SupplierOrderDTO dto) throws SQLException {
        String sql = """
        INSERT INTO supplier_orders(
            product_id, product_name, supplier_id,
            supplier_catalog_id, quantity, status,
            order_type, scheduled_date, frequency, created_at)
        VALUES(?,?,?,?,?,?,?,?,?,?)
    """;
        try (PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1,    dto.productId());
            ps.setString(2, dto.productName());
            ps.setInt(3,    dto.supplierId());
            ps.setString(4, dto.supplierCatalogId());
            ps.setInt(5,    dto.quantity());
            ps.setString(6, dto.status());
            ps.setString(7, dto.orderType());
            ps.setString(8, dto.scheduledDate());
            ps.setString(9, dto.frequency());
            ps.setString(10,dto.createdAt());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }

    @Override
    public void updateStatus(int orderId, String status) throws SQLException {
        String sql = "UPDATE supplier_orders SET status=? WHERE order_id=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, orderId);
            ps.executeUpdate();
        }
    }

    @Override
    public Optional<SupplierOrderDTO> findById(int orderId) throws SQLException {
        String sql = "SELECT * FROM supplier_orders WHERE order_id=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        }
        return Optional.empty();
    }

    @Override
    public List<SupplierOrderDTO> findAll() throws SQLException {
        List<SupplierOrderDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM supplier_orders";
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    @Override
    public List<SupplierOrderDTO> findByProductId(int productId) throws SQLException {
        List<SupplierOrderDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM supplier_orders WHERE product_id=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    @Override
    public List<SupplierOrderDTO> findActiveByProductId(int productId) throws SQLException {
        List<SupplierOrderDTO> list = new ArrayList<>();
        String sql = """
            SELECT * FROM supplier_orders
            WHERE product_id=?
              AND status IN ('CREATED','SENT')
        """;
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    private SupplierOrderDTO mapRow(ResultSet rs) throws SQLException {
        return new SupplierOrderDTO(
                rs.getInt("order_id"),
                rs.getInt("product_id"),
                rs.getString("product_name"),
                rs.getInt("supplier_id"),
                rs.getString("supplier_catalog_id"),
                rs.getInt("quantity"),
                rs.getString("status"),
                rs.getString("order_type"),
                rs.getString("scheduled_date"),
                rs.getString("frequency"),
                rs.getString("created_at")
        );
    }
    @Override
    public List<SupplierOrderDTO> findPendingByDate(String date) throws SQLException {
        List<SupplierOrderDTO> list = new ArrayList<>();
        String sql = """
        SELECT * FROM supplier_orders
        WHERE status = 'PENDING'
          AND scheduled_date <= ?
    """;
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, date);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }
}
