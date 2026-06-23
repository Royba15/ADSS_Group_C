import Inventory.domain.*;

import org.junit.jupiter.api.*;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UnitTests {

    private Category dairy, milk, pct3, snacks, chips, salted;

    @BeforeEach
    void setUp() {
        dairy  = new Category("Dairy",  0);
        milk   = new Category("Milk",   1);
        pct3   = new Category("3%",     2);
        snacks = new Category("Snacks", 0);
        chips  = new Category("Chips",  1);
        salted = new Category("Salted", 2);
    }


    @Test
    @Order(1)
    @DisplayName("T-01: InventoryLevel")
    void testTotalQuantity() {
        InventoryLevel inv = new InventoryLevel(10, 5, 8, "Aisle 1");
        assertEquals(15, inv.getTotalQuantity());
        assertEquals(10, inv.getShelfQuantity());
        assertEquals(5,  inv.getWarehouseQuantity());
        assertEquals(8,  inv.getMinQuantityThreshold());
        assertEquals("Aisle 1", inv.getLocation());
    }

    @Test
    @Order(2)
    @DisplayName("T-02: InventoryLevel — Update quantities")
    void testUpdateQuantity() {
        InventoryLevel inv = new InventoryLevel(10, 5, 8, "Aisle 1");
        inv.updateQuantity(20, 10);
        assertEquals(20, inv.getShelfQuantity());
        assertEquals(10, inv.getWarehouseQuantity());
        assertEquals(30, inv.getTotalQuantity());
    }

    @Test
    @Order(3)
    @DisplayName("T-03: InventoryLevel — Below and above threshold detection")
    void testBelowThreshold() {
        InventoryLevel inv = new InventoryLevel(3, 2, 10, "Aisle 1");
        assertTrue(inv.isBelowThreshold(), "5 < 10 → below");

        inv.updateQuantity(8, 5);
        assertFalse(inv.isBelowThreshold(), "13 >= 10 → above");
    }

    @Test
    @Order(4)
    @DisplayName("T-04: InventoryLevel — Negative quantities throw an exception.")
    void testNegativeQuantityThrows() {
        InventoryLevel inv = new InventoryLevel(10, 5, 8, "Aisle 1");
        assertThrows(IllegalArgumentException.class, () -> inv.updateQuantity(-1, 5));
    }



    @Test
    @Order(5)
    @DisplayName("T-05: Product — Creating and checking fields")
    void testProductCreation() {
        InventoryLevel inv = new InventoryLevel(10, 5, 8, "Aisle 1");
        Product p = new Product(1, "Tnuva Milk", 101, 4.5, 6.9,
                "CAT-001", dairy, milk, pct3, inv);

        assertEquals(1, p.getProductID());
        assertEquals("Tnuva Milk", p.getProductName());
        assertEquals(101, p.getSupplierID());
        assertEquals(4.5, p.getCostPrice());
        assertEquals(6.9, p.getSellingPrice());
        assertEquals(6.9, p.getOriginalSellingPrice());
        assertEquals("CAT-001", p.getSupplierCatalogID());
        assertEquals("Dairy", p.getMainCategory().getName());
        assertEquals("Milk", p.getSubCategory().getName());
        assertEquals("3%", p.getSubSubCategory().getName());
    }

    @Test
    @Order(6)
    @DisplayName("T-06: Product — checkMinThreshold")
    void testCheckMinThreshold() {
        InventoryLevel belowInv = new InventoryLevel(3, 2, 10, "Aisle 1"); // total=5, min=10
        Product belowProduct = new Product(1, "Low", 101, 4.5, 6.9,
                "CAT-001", dairy, milk, pct3, belowInv);
        assertTrue(belowProduct.checkMinThreshold());

        InventoryLevel aboveInv = new InventoryLevel(10, 5, 8, "Aisle 1"); // total=15, min=8
        Product aboveProduct = new Product(2, "OK", 101, 4.5, 6.9,
                "CAT-001", dairy, milk, pct3, aboveInv);
        assertFalse(aboveProduct.checkMinThreshold());
    }

    @Test
    @Order(7)
    @DisplayName("T-07: Product — Negative price throws an exception")
    void testNegativePriceThrows() {
        InventoryLevel inv = new InventoryLevel(10, 5, 8, "Aisle 1");
        Product p = new Product(1, "Milk", 101, 4.5, 6.9,
                "CAT-001", dairy, milk, pct3, inv);

        assertThrows(IllegalArgumentException.class, () -> p.setSellingPrice(-5.0));
    }


    @Test
    @Order(8)
    @DisplayName("T-08: DiscountPromotion — Calculate price after discount")
    void testDiscountCalculation() {
        DiscountPromotion promo = new DiscountPromotion(1, "Sale", 20,
                LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(10));

        assertEquals(80.0, promo.calculateFinalPrice(100.0), 0.01);
        assertEquals(40.0, promo.calculateFinalPrice(50.0), 0.01);
    }

    @Test
    @Order(9)
    @DisplayName("T-9: DiscountPromotion — Invalid percentage throws an exception")
    void testInvalidDiscountThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                new DiscountPromotion(1, "Bad", -5,
                        LocalDateTime.now(), LocalDateTime.now().plusDays(1)));

        assertThrows(IllegalArgumentException.class, () ->
                new DiscountPromotion(1, "Bad", 150,
                        LocalDateTime.now(), LocalDateTime.now().plusDays(1)));
    }

    @Test
    @Order(10)
    @DisplayName("T-10: Product — Discount updates price")
    void testPromotionApplied() {
        InventoryLevel inv = new InventoryLevel(10, 5, 8, "Aisle 1");
        Product p = new Product(1, "Milk", 101, 4.5, 10.0,
                "CAT-001", dairy, milk, pct3, inv);

        DiscountPromotion promo = new DiscountPromotion(1, "Summer", 25,
                LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(10));
        p.assignPromotion(promo);

        assertEquals(7.5, p.getSellingPrice(), 0.01, "25% off 10.0 = 7.5");
        assertTrue(p.hasActivePromotion());
    }


}