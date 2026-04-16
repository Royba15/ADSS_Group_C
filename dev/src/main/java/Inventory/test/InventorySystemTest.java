package Inventory.test;

import Inventory.domain.*;
import Inventory.service.InventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

public class InventorySystemTest {

    private InventoryService service;

    @BeforeEach
    void setUp() {
        service = new InventoryService();
    }


    @Test
    @DisplayName("Test InventoryLevel creation and quantity calculation")
    void testInventoryLevelCreation() {
        InventoryLevel inv = new InventoryLevel(10, 5, 8, "Aisle 1");

        assertEquals(10, inv.getShelfQuantity());
        assertEquals(5, inv.getWarehouseQuantity());
        assertEquals(15, inv.getTotalQuantity()); // 10 + 5
        assertEquals(8, inv.getMinQuantityThreshold());
        assertEquals("Aisle 1", inv.getLocation());
    }

    @Test
    @DisplayName("Test InventoryLevel.updateQuantity()")
    void testInventoryLevelUpdate() {
        InventoryLevel inv = new InventoryLevel(10, 5, 8, "Aisle 1");
        inv.updateQuantity(20, 10);

        assertEquals(20, inv.getShelfQuantity());
        assertEquals(10, inv.getWarehouseQuantity());
        assertEquals(30, inv.getTotalQuantity());
    }


    @Test
    @DisplayName("Test InventoryLevel.isBelowThreshold()")
    void testInventoryLevelThreshold() {
        InventoryLevel inv = new InventoryLevel(5, 2, 10, "Aisle 1");

        assertTrue(inv.isBelowThreshold()); // 7 < 10
        inv.updateQuantity(8, 5);
        assertFalse(inv.isBelowThreshold()); // 13 >= 10
    }


    @Test
    @DisplayName("Test Product creation with categories")
    void testProductCreation() {
        Category dairy = new Category("Dairy", 0);
        Category milk = new Category("Milk", 1);
        Category milk3 = new Category("3%", 2);
        InventoryLevel inv = new InventoryLevel(3, 2, 10, "Aisle 1");

        Product p = new Product(1, "Tnuva 3% Milk", 101, 4.5, 6.9,
                "CAT-001", dairy, milk, milk3, inv);

        assertEquals(1, p.getProductID());
        assertEquals("Tnuva 3% Milk", p.getProductName());
        assertEquals(101, p.getManufacturerID());
        assertEquals(4.5, p.getCostPrice());
        assertEquals(6.9, p.getSellingPrice());
        assertEquals("CAT-001", p.getSupplierCatalogID());
        assertEquals(10, p.getMinimumThreshold());
    }


    @Test
    @DisplayName("Test Product.checkMinThreshold()")
    void testProductCheckMinThreshold() {
        Category dairy = new Category("Dairy", 0);
        Category milk = new Category("Milk", 1);
        Category milk3 = new Category("3%", 2);
        InventoryLevel inv = new InventoryLevel(3, 2, 10, "Aisle 1");

        Product p = new Product(1, "Tnuva 3% Milk", 101, 4.5, 6.9,
                "CAT-001", dairy, milk, milk3, inv);

        assertTrue(p.checkMinThreshold()); // 5 < 10
    }


    @Test
    @DisplayName("Test Product promotion assignment")
    void testProductPromotionAssignment() {
        Category dairy = new Category("Dairy", 0);
        Category milk = new Category("Milk", 1);
        Category milk3 = new Category("3%", 2);
        InventoryLevel inv = new InventoryLevel(10, 5, 8, "Aisle 1");

        Product p = new Product(1, "Tnuva 3% Milk", 101, 4.5, 6.9,
                "CAT-001", dairy, milk, milk3, inv);

        double originalPrice = p.getSellingPrice();

        // Assign promotion
        DiscountPromotion promo = new DiscountPromotion(1, "Summer Sale", 10,
                java.time.LocalDateTime.now().minusDays(1),
                java.time.LocalDateTime.now().plusDays(10));

        p.assignPromotion(promo);

        assertTrue(p.hasActivePromotion());
        assertNotNull(p.getPromotion());
        // Price should be reduced by discount
        assertTrue(p.getSellingPrice() < originalPrice);
    }

    @Test
    @DisplayName("Test DefectiveItem creation")
    void testDefectiveItemCreation() {
        Category dairy = new Category("Dairy", 0);
        Category milk = new Category("Milk", 1);
        Category milk3 = new Category("3%", 2);
        InventoryLevel inv = new InventoryLevel(10, 5, 8, "Aisle 1");
        Product p = new Product(1, "Tnuva 3% Milk", 101, 4.5, 6.9,
                "CAT-001", dairy, milk, milk3, inv);

        DefectiveItem defective = new DefectiveItem(1, p, 2, "Expired");

        assertEquals(1, defective.getRecordID());
        assertEquals(p, defective.getProduct());
        assertEquals(2, defective.getQuantity());
        assertEquals("Expired", defective.getReason());
        assertNotNull(defective.getReportDateTime());
    }


    @Test
    @DisplayName("Test InventoryService.updateInventory()")
    void testServiceUpdateInventory() {
        Category dairy = new Category("Dairy", 0);
        Category milk = new Category("Milk", 1);
        Category milk3 = new Category("3%", 2);
        InventoryLevel inv = new InventoryLevel(10, 5, 8, "Aisle 1");

        Product p = new Product(1, "Tnuva 3% Milk", 101, 4.5, 6.9,
                "CAT-001", dairy, milk, milk3, inv);

        service.addProduct(p);
        boolean updated = service.updateInventory(1, 20, 10);

        assertTrue(updated);
        assertEquals(20, service.getProductByID(1).getInventory().getShelfQuantity());
        assertEquals(10, service.getProductByID(1).getInventory().getWarehouseQuantity());
        assertEquals(30, service.getProductByID(1).getInventory().getTotalQuantity());
    }


    @Test
    @DisplayName("Test InventoryService.reportDefectiveItem()")
    void testServiceReportDefectiveItem() {
        Category dairy = new Category("Dairy", 0);
        Category milk = new Category("Milk", 1);
        Category milk3 = new Category("3%", 2);
        InventoryLevel inv = new InventoryLevel(10, 5, 8, "Aisle 1");

        Product p = new Product(1, "Tnuva 3% Milk", 101, 4.5, 6.9,
                "CAT-001", dairy, milk, milk3, inv);
        service.addProduct(p);

        service.reportDefectiveItem(1, 2, "Expired");

        var defectiveItems = service.getDefectiveItems();
        assertEquals(1, defectiveItems.size());
        assertEquals("Expired", defectiveItems.get(0).getReason());
        assertEquals(2, defectiveItems.get(0).getQuantity());
    }

    @Test
    @DisplayName("Test InventoryService.getTotalDefectiveCountForProduct()")
    void testServiceGetTotalDefectiveCountForProduct() {
        Category dairy = new Category("Dairy", 0);
        Category milk = new Category("Milk", 1);
        Category milk3 = new Category("3%", 2);
        InventoryLevel inv = new InventoryLevel(10, 5, 8, "Aisle 1");

        Product p = new Product(1, "Tnuva 3% Milk", 101, 4.5, 6.9,
                "CAT-001", dairy, milk, milk3, inv);
        service.addProduct(p);

        // Report defective items multiple times
        service.reportDefectiveItem(1, 2, "Expired");
        service.reportDefectiveItem(1, 1, "Damaged");

        int totalDefective = service.getTotalDefectiveCountForProduct(1);
        assertEquals(3, totalDefective); // 2 + 1
    }

    @Test
    @DisplayName("Test InventoryService.generateDefectiveReport()")
    void testServiceGenerateDefectiveReport() {
        Category dairy = new Category("Dairy", 0);
        Category milk = new Category("Milk", 1);
        Category milk3 = new Category("3%", 2);
        InventoryLevel inv = new InventoryLevel(10, 5, 8, "Aisle 1");

        Product p = new Product(1, "Tnuva 3% Milk", 101, 4.5, 6.9,
                "CAT-001", dairy, milk, milk3, inv);
        service.addProduct(p);

        service.reportDefectiveItem(1, 2, "Expired");
        service.reportDefectiveItem(1, 1, "Damaged");

        DefectiveReport report = service.generateDefectiveReport();
        assertEquals(2, report.getDefectiveItems().size());
    }

    @Test
    @DisplayName("Test InventoryService.applyDiscountToProduct()")
    void testServiceApplyDiscountToProduct() {
        Category dairy = new Category("Dairy", 0);
        Category milk = new Category("Milk", 1);
        Category milk3 = new Category("3%", 2);
        InventoryLevel inv = new InventoryLevel(10, 5, 8, "Aisle 1");

        Product p = new Product(1, "Tnuva 3% Milk", 101, 4.5, 6.9,
                "CAT-001", dairy, milk, milk3, inv);
        service.addProduct(p);

        double originalPrice = p.getSellingPrice();

        boolean applied = service.applyDiscountToProduct(1, "Summer Sale", 15,
                java.time.LocalDateTime.now().minusDays(1),
                java.time.LocalDateTime.now().plusDays(10));

        assertTrue(applied);
        Product discountedProduct = service.getProductByID(1);
        assertTrue(discountedProduct.getSellingPrice() < originalPrice);
    }

    @Test
    @DisplayName("Test InventoryService.applyDiscountToCategory()")
    void testServiceApplyDiscountToCategory() {
        Category dairy = new Category("Dairy", 0);
        Category milk = new Category("Milk", 1);
        Category milk3 = new Category("3%", 2);

        InventoryLevel inv1 = new InventoryLevel(10, 5, 8, "Aisle 1");
        Product p1 = new Product(1, "Product A", 101, 4.5, 6.9,
                "CAT-001", dairy, milk, milk3, inv1);
        service.addProduct(p1);

        InventoryLevel inv2 = new InventoryLevel(8, 7, 6, "Aisle 1");
        Product p2 = new Product(2, "Product B", 102, 5.0, 7.0,
                "CAT-002", dairy, milk, milk3, inv2);
        service.addProduct(p2);

        double price1 = p1.getSellingPrice();
        double price2 = p2.getSellingPrice();

        boolean applied = service.applyDiscountToCategory("Dairy", "Dairy Sale", 20,
                java.time.LocalDateTime.now().minusDays(1),
                java.time.LocalDateTime.now().plusDays(10));

        assertTrue(applied);
        assertTrue(service.getProductByID(1).getSellingPrice() < price1);
        assertTrue(service.getProductByID(2).getSellingPrice() < price2);
    }


}
