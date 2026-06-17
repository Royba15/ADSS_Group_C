package Inventory.data.impl;

import Inventory.data.dao.CategoryDAO;
import Inventory.data.dao.DefectiveItemDAO;
import Inventory.data.dao.InventoryRepository;
import Inventory.data.dao.ProductDAO;
import Inventory.dto.CategoryDTO;
import Inventory.dto.DefectiveItemDTO;
import Inventory.dto.ProductDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * InventoryRepositoryImpl – מתאם את כל ה-DAOs.
 * אין כאן SQL — רק האצלה ל-DAO המתאים.
 */
public class InventoryRepositoryImpl implements InventoryRepository {

    private final ProductDAO productDAO;
    private final CategoryDAO categoryDAO;
    private final DefectiveItemDAO defectiveDAO;

    public InventoryRepositoryImpl() {
        this.productDAO   = new JdbcProductDAO();
        this.categoryDAO  = new JdbcCategoryDAO();
        this.defectiveDAO = new JdbcDefectiveItemDAO();
    }

    // ── Products ──────────────────────────────────────────────────────────────

    @Override
    public void addProduct(ProductDTO dto) throws SQLException {
        productDAO.save(dto);
    }

    @Override
    public Optional<ProductDTO> findProductById(int id) throws SQLException {
        return productDAO.findById(id);
    }

    @Override
    public List<ProductDTO> findAllProducts() throws SQLException {
        return productDAO.findAll();
    }

    @Override
    public List<ProductDTO> findProductsBelowThreshold() throws SQLException {
        return productDAO.findBelowThreshold();
    }

    @Override
    public List<ProductDTO> findProductsByCategory(String category) throws SQLException {
        return productDAO.findByCategory(category);
    }

    @Override
    public List<ProductDTO> findProductsBySupplier(int supplierId) throws SQLException {
        return productDAO.findBySupplier(supplierId);
    }

    @Override
    public void updateInventoryQuantity(int id, int shelf, int warehouse) throws SQLException {
        productDAO.updateInventoryQuantity(id, shelf, warehouse);
    }

    @Override
    public void updateSellingPrice(int id, double price) throws SQLException {
        productDAO.updateSellingPrice(id, price);
    }

    @Override
    public void deleteProduct(int id) throws SQLException {
        productDAO.delete(id);
    }

    @Override
    public boolean productExists(int id) throws SQLException {
        return productDAO.existsById(id);
    }

    // ── Categories ────────────────────────────────────────────────────────────

    @Override
    public void addCategory(CategoryDTO dto) throws SQLException {
        categoryDAO.save(dto);
    }

    @Override
    public Optional<CategoryDTO> findCategoryByName(String name) throws SQLException {
        return categoryDAO.findByName(name);
    }

    @Override
    public List<CategoryDTO> findAllCategories() throws SQLException {
        return categoryDAO.findAll();
    }

    @Override
    public List<CategoryDTO> findCategoriesByLevel(int level) throws SQLException {
        return categoryDAO.findByLevel(level);
    }

    // ── Defective items ───────────────────────────────────────────────────────

    @Override
    public void reportDefective(DefectiveItemDTO dto) throws SQLException {
        defectiveDAO.save(dto);
    }

    @Override
    public List<DefectiveItemDTO> findAllDefectiveItems() throws SQLException {
        return defectiveDAO.findAll();
    }

    @Override
    public List<DefectiveItemDTO> findDefectiveByProduct(int productId) throws SQLException {
        return defectiveDAO.findByProductId(productId);
    }
}
