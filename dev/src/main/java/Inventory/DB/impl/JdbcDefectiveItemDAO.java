package Inventory.DB.impl;

import Inventory.DB.config.DatabaseConnection;
import Inventory.DB.dao.DefectiveItemDAO;
import Inventory.dto.DefectiveItemDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcDefectiveItemDAO implements DefectiveItemDAO {

    @Override
    public void save(DefectiveItemDTO dto) throws SQLException {
        String sql = "INSERT INTO defective_items(product_id, quantity, reason) VALUES(?,?,?)";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1,    dto.productId());
            ps.setInt(2,    dto.quantity());
            ps.setString(3, dto.reason());
            ps.executeUpdate();
        }
    }

    @Override
    public List<DefectiveItemDTO> findAll() throws SQLException {
        List<DefectiveItemDTO> list = new ArrayList<>();
        String sql = """
            SELECT d.id, d.product_id, p.name, d.quantity, d.reason
            FROM defective_items d
            JOIN products p ON d.product_id = p.product_id
        """;
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next())
                list.add(new DefectiveItemDTO(
                        rs.getInt("id"),
                        rs.getInt("product_id"),
                        rs.getString("name"),
                        rs.getInt("quantity"),
                        rs.getString("reason")));
        }
        return list;
    }

    @Override
    public List<DefectiveItemDTO> findByProductId(int productId) throws SQLException {
        List<DefectiveItemDTO> list = new ArrayList<>();
        String sql = """
            SELECT d.id, d.product_id, p.name, d.quantity, d.reason
            FROM defective_items d
            JOIN products p ON d.product_id = p.product_id
            WHERE d.product_id=?
        """;
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next())
                    list.add(new DefectiveItemDTO(
                            rs.getInt("id"),
                            rs.getInt("product_id"),
                            rs.getString("name"),
                            rs.getInt("quantity"),
                            rs.getString("reason")));
            }
        }
        return list;
    }
}
