package Inventory.data.dao;

import Inventory.dto.CategoryDTO;
import Inventory.dto.DefectiveItemDTO;
import Inventory.dto.ProductDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface acting as a domain-level abstraction layer.
 */
public interface InventoryRepository {

    // Products
    void addProduct(ProductDTO dto) throws SQLException;
    Optional<ProductDTO> findProductById(int id) throws SQLException;
    List<ProductDTO> findAllProducts() throws SQLException;
    List<ProductDTO> findProductsBelowThreshold() throws SQLException;
    List<ProductDTO> findProductsByCategory(String category) throws SQLException;
    List<ProductDTO> findProductsBySupplier(int supplierId) throws SQLException;
    void updateInventoryQuantity(int id, int shelf, int warehouse) throws SQLException;
    void updateSellingPrice(int id, double price) throws SQLException;
    void deleteProduct(int id) throws SQLException;
    boolean productExists(int id) throws SQLException;

    // Categories
    void addCategory(CategoryDTO dto) throws SQLException;
    Optional<CategoryDTO> findCategoryByName(String name) throws SQLException;
    List<CategoryDTO> findAllCategories() throws SQLException;
    List<CategoryDTO> findCategoriesByLevel(int level) throws SQLException;

    // Defective items
    void reportDefective(DefectiveItemDTO dto) throws SQLException;
    List<DefectiveItemDTO> findAllDefectiveItems() throws SQLException;
    List<DefectiveItemDTO> findDefectiveByProduct(int productId) throws SQLException;
}
