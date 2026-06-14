package Inventory.DB.dao;

import Inventory.dto.ProductDTO;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ProductDAO {
    void save(ProductDTO dto) throws SQLException;
    void update(ProductDTO dto) throws SQLException;
    void delete(int productId) throws SQLException;
    Optional<ProductDTO> findById(int productId) throws SQLException;
    List<ProductDTO> findAll() throws SQLException;
    List<ProductDTO> findBelowThreshold() throws SQLException;
    List<ProductDTO> findByCategory(String categoryName) throws SQLException;
    List<ProductDTO> findBySupplier(int supplierId) throws SQLException;
    void updateInventoryQuantity(int productId, int shelf, int warehouse) throws SQLException;
    void updateSellingPrice(int productId, double price) throws SQLException;
    boolean existsById(int productId) throws SQLException;
}
