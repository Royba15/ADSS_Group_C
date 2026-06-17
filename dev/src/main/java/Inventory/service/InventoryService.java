package Inventory.service;

import Inventory.data.dao.InventoryRepository;
import Inventory.data.impl.InventoryRepositoryImpl;
import Inventory.integration.SupplierIntegrationService;
import Inventory.domain.*;
import Inventory.dto.CategoryDTO;
import Inventory.dto.DefectiveItemDTO;
import Inventory.dto.ProductDTO;
import Inventory.dto.SupplierOrderDTO;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class InventoryService {

    private final InventoryRepository repo;
    private final SupplierIntegrationService supplierIntegrationService;

    public InventoryService() {
        this.repo                       = new InventoryRepositoryImpl();
        this.supplierIntegrationService = new SupplierIntegrationService();
    }

    // ── Products ──────────────────────────────────────────────────────────────

    public Product getProductByID(int productID) {
        try {
            return repo.findProductById(productID).map(this::dtoToDomain).orElse(null);
        } catch (SQLException e) {
            System.err.println("[DB] getProductByID failed: " + e.getMessage());
            return null;
        }
    }

    public List<Product> getAllProducts() {
        try {
            List<Product> result = new ArrayList<>();
            for (ProductDTO dto : repo.findAllProducts()) result.add(dtoToDomain(dto));
            return result;
        } catch (SQLException e) {
            System.err.println("[DB] getAllProducts failed: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public boolean updateInventory(int productID, int shelfQty, int warehouseQty) {
        try {
            if (!repo.productExists(productID)) return false;
            repo.updateInventoryQuantity(productID, shelfQty, warehouseQty);
            Product p = getProductByID(productID);
            if (p != null) supplierIntegrationService.createAutomaticOrderIfNeeded(p);
            return true;
        } catch (SQLException e) {
            System.err.println("[DB] updateInventory failed: " + e.getMessage());
            return false;
        }
    }

    public boolean addNewProduct(int productID, String productName, int supplierID,
                                 double costPrice, double sellingPrice, String catalogID,
                                 String mainCategoryName, String subCategoryName, String subSubCategoryName,
                                 int shelfQuantity, int warehouseQuantity, int minThreshold, String location) {
        try {
            if (productName == null || productName.trim().isEmpty()) return false;
            if (costPrice < 0 || sellingPrice < 0) return false;
            if (shelfQuantity < 0 || warehouseQuantity < 0 || minThreshold < 0) return false;
            if (repo.productExists(productID)) return false;
            if (repo.findCategoryByName(mainCategoryName).isEmpty())   return false;
            if (repo.findCategoryByName(subCategoryName).isEmpty())    return false;
            if (repo.findCategoryByName(subSubCategoryName).isEmpty()) return false;

            repo.addProduct(new ProductDTO(
                    productID, productName.trim(), supplierID,
                    costPrice, sellingPrice, sellingPrice, catalogID,
                    mainCategoryName, subCategoryName, subSubCategoryName,
                    shelfQuantity, warehouseQuantity, minThreshold, location));
            return true;
        } catch (SQLException e) {
            System.err.println("[DB] addNewProduct failed: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteProduct(int productID) {
        try {
            if (!repo.productExists(productID)) return false;
            repo.deleteProduct(productID);
            return true;
        } catch (SQLException e) {
            System.err.println("[DB] deleteProduct failed: " + e.getMessage());
            return false;
        }
    }

    public int getNextProductID() {
        try {
            return repo.findAllProducts().stream()
                    .mapToInt(ProductDTO::productId).max().orElse(0) + 1;
        } catch (SQLException e) { return 1; }
    }

    // ── Low stock ─────────────────────────────────────────────────────────────

    public List<Product> getLowStockProducts() {
        try {
            List<Product> result = new ArrayList<>();
            for (ProductDTO dto : repo.findProductsBelowThreshold()) result.add(dtoToDomain(dto));
            return result;
        } catch (SQLException e) {
            System.err.println("[DB] getLowStockProducts failed: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // ── Categories ────────────────────────────────────────────────────────────

    public boolean addNewCategory(String categoryName, int level) {
        try {
            if (categoryName == null || categoryName.trim().isEmpty()) return false;
            if (level < 0 || level > 2) return false;
            if (repo.findCategoryByName(categoryName.trim()).isPresent()) return false;
            repo.addCategory(new CategoryDTO(categoryName.trim(), level));
            return true;
        } catch (SQLException e) {
            System.err.println("[DB] addNewCategory failed: " + e.getMessage());
            return false;
        }
    }

    public List<Category> getCategories() {
        try {
            List<Category> result = new ArrayList<>();
            for (CategoryDTO dto : repo.findAllCategories())
                result.add(new Category(dto.name(), dto.level()));
            return result;
        } catch (SQLException e) {
            System.err.println("[DB] getCategories failed: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Category> getCategoriesByLevel(int level) {
        try {
            List<Category> result = new ArrayList<>();
            for (CategoryDTO dto : repo.findCategoriesByLevel(level))
                result.add(new Category(dto.name(), dto.level()));
            return result;
        } catch (SQLException e) {
            System.err.println("[DB] getCategoriesByLevel failed: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public boolean categoryExists(String categoryName) {
        try { return repo.findCategoryByName(categoryName).isPresent(); }
        catch (SQLException e) { return false; }
    }

    public boolean allCategoriesExist(String main, String sub, String subSub) {
        return categoryExists(main) && categoryExists(sub) && categoryExists(subSub);
    }

    // ── Defective items ───────────────────────────────────────────────────────

    public void reportDefectiveItem(int productID, int quantity, String reason) {
        try {
            ProductDTO p = repo.findProductById(productID).orElse(null);
            if (p == null) return;
            repo.reportDefective(new DefectiveItemDTO(0, productID, p.name(), quantity, reason));
        } catch (SQLException e) {
            System.err.println("[DB] reportDefectiveItem failed: " + e.getMessage());
        }
    }

    public List<DefectiveItem> getDefectiveItems() {
        try {
            List<DefectiveItem> result = new ArrayList<>();
            for (DefectiveItemDTO dto : repo.findAllDefectiveItems()) {
                Product p = getProductByID(dto.productId());
                if (p != null)
                    result.add(new DefectiveItem(dto.id(), p, dto.quantity(), dto.reason()));
            }
            return result;
        } catch (SQLException e) {
            System.err.println("[DB] getDefectiveItems failed: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public int getTotalDefectiveCountForProduct(int productID) {
        try {
            return repo.findDefectiveByProduct(productID).stream()
                    .mapToInt(DefectiveItemDTO::quantity).sum();
        } catch (SQLException e) { return 0; }
    }

    // ── Reports ───────────────────────────────────────────────────────────────

    public CategoryReport generateCategoryReport(List<String> categoryNames) {
        List<Product> result = new ArrayList<>();
        for (String name : categoryNames) {
            try {
                for (ProductDTO dto : repo.findProductsByCategory(name))
                    result.add(dtoToDomain(dto));
            } catch (SQLException e) {
                System.err.println("[DB] categoryReport failed: " + e.getMessage());
            }
        }
        return new CategoryReport(categoryNames, result);
    }

    public DefectiveReport generateDefectiveReport() { return new DefectiveReport(getDefectiveItems()); }

    // ── Discounts ─────────────────────────────────────────────────────────────

    public boolean applyDiscountToProduct(int productID, String promoName, double discount,
                                          LocalDateTime start, LocalDateTime end) {
        try {
            ProductDTO dto = repo.findProductById(productID).orElse(null);
            if (dto == null) return false;
            Product p = dtoToDomain(dto);
            p.assignPromotion(new DiscountPromotion(productID, promoName, discount, start, end));
            repo.updateSellingPrice(productID, p.getSellingPrice());
            return true;
        } catch (SQLException e) {
            System.err.println("[DB] applyDiscountToProduct failed: " + e.getMessage());
            return false;
        }
    }

    public boolean applyDiscountToCategory(String categoryName, String promoName, double discount,
                                           LocalDateTime start, LocalDateTime end) {
        try {
            List<ProductDTO> dtos = repo.findProductsByCategory(categoryName);
            if (dtos.isEmpty()) return false;
            DiscountPromotion promo = new DiscountPromotion(categoryName.hashCode(), promoName, discount, start, end);
            for (ProductDTO dto : dtos) {
                Product p = dtoToDomain(dto);
                p.assignPromotion(promo);
                repo.updateSellingPrice(p.getProductID(), p.getSellingPrice());
            }
            return true;
        } catch (SQLException e) {
            System.err.println("[DB] applyDiscountToCategory failed: " + e.getMessage());
            return false;
        }
    }

    public boolean applyDiscountToSupplier(int supplierID, String promoName, double discount,
                                           LocalDateTime start, LocalDateTime end) {
        if (discount < 0 || discount > 100)
            throw new IllegalArgumentException("Discount must be between 0 and 100");
        if (start == null || end == null || !end.isAfter(start))
            throw new IllegalArgumentException("End date must be after start date");
        try {
            List<ProductDTO> dtos = repo.findProductsBySupplier(supplierID);
            if (dtos.isEmpty()) return false;
            DiscountPromotion promo = new DiscountPromotion(supplierID, promoName, discount, start, end);
            for (ProductDTO dto : dtos) {
                Product p = dtoToDomain(dto);
                p.assignPromotion(promo);
                repo.updateSellingPrice(p.getProductID(), p.getSellingPrice());
            }
            return true;
        } catch (SQLException e) {
            System.err.println("[DB] applyDiscountToSupplier failed: " + e.getMessage());
            return false;
        }
    }

    // ── Supplier integration ──────────────────────────────────────────────────

    public boolean createManualSupplierOrder(int productID, int quantityToOrder,
                                             boolean allowDuplicateOrder) {
        Product p = getProductByID(productID);
        if (p == null || quantityToOrder <= 0) return false;
        return supplierIntegrationService.createManualOrder(p, quantityToOrder, allowDuplicateOrder);
    }

    public boolean createScheduledSupplierOrder(int productID, int quantity,
                                                String scheduledDate, String frequency) {
        Product p = getProductByID(productID);
        if (p == null || quantity <= 0) return false;
        return supplierIntegrationService.createScheduledOrder(p, quantity, scheduledDate, frequency);
    }

    public boolean receiveOrder(int orderId) {
        try {
            SupplierOrderDTO order = supplierIntegrationService.findOrderById(orderId);
            if (order == null) return false;
            if (!order.status().equals("CREATED") && !order.status().equals("SENT")
                    && !order.status().equals("PENDING")) return false;

            supplierIntegrationService.receiveOrder(orderId);

            ProductDTO product = repo.findProductById(order.productId()).orElse(null);
            if (product == null) return false;
            repo.updateInventoryQuantity(
                    order.productId(),
                    product.shelfQuantity(),
                    product.warehouseQuantity() + order.quantity());
            return true;
        } catch (SQLException e) {
            System.err.println("[DB] receiveOrder failed: " + e.getMessage());
            return false;
        }
    }

    public List<SupplierOrderDTO> getActiveSupplierOrdersForProduct(int productID) {
        return supplierIntegrationService.getActiveOrdersForProduct(productID);
    }

    public boolean markOrderAsReceived(int productID) {
        return supplierIntegrationService.markOrderAsReceived(productID);
    }

    public boolean cancelActiveOrder(int productID) {
        return supplierIntegrationService.cancelActiveOrder(productID);
    }

    public boolean hasActiveOrderForProduct(int productID) {
        return supplierIntegrationService.hasActiveOrderForProduct(productID);
    }

    public List<SupplierOrderDTO> getAllOrders() {
        return supplierIntegrationService.getAllOrders();
    }

    public List<SupplierOrderDTO> getActiveOrders() {
        return supplierIntegrationService.getActiveOrders();
    }

    public List<SupplierOrderDTO> getOrdersForProduct(int productID) {
        return supplierIntegrationService.getOrdersForProduct(productID);
    }

    public void createAutomaticOrdersForLowStock() {
        List<Product> lowStock = getLowStockProducts();
        if (lowStock.isEmpty()) return;
        for (Product p : lowStock) {
            supplierIntegrationService.createAutomaticOrderIfNeeded(p);
        }
    }

    // ── helper: DTO → Domain object ───────────────────────────────────────────

    private Product dtoToDomain(ProductDTO dto) {
        Category main   = new Category(dto.mainCategory(),   0);
        Category sub    = new Category(dto.subCategory(),    1);
        Category subSub = new Category(dto.subSubCategory(), 2);
        InventoryLevel inv = new InventoryLevel(
                dto.shelfQuantity(), dto.warehouseQuantity(),
                dto.minQuantityThreshold(), dto.location());
        return new Product(
                dto.productId(), dto.name(), dto.supplierId(),
                dto.costPrice(), dto.sellingPrice(), dto.supplierCatalogId(),
                main, sub, subSub, inv);
    }
}
