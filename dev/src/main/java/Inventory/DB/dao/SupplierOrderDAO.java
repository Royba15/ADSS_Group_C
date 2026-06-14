package Inventory.DB.dao;

import Inventory.dto.SupplierOrderDTO;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * DAO ממשק לטבלת supplier_orders.
 */
public interface SupplierOrderDAO {
    int save(SupplierOrderDTO dto) throws SQLException;           // מחזיר את ה-ID שנוצר
    void updateStatus(int orderId, String status) throws SQLException;
    Optional<SupplierOrderDTO> findById(int orderId) throws SQLException;
    List<SupplierOrderDTO> findAll() throws SQLException;
    List<SupplierOrderDTO> findByProductId(int productId) throws SQLException;
    List<SupplierOrderDTO> findActiveByProductId(int productId) throws SQLException;
}
