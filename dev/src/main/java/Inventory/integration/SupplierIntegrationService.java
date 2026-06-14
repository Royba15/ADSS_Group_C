package Inventory.integration;

import Inventory.DB.dao.SupplierOrderDAO;
import Inventory.DB.impl.JdbcSupplierOrderDAO;
import Inventory.domain.Product;
import Inventory.domain.SupplierOrder;
import Inventory.domain.OrderStatus;
import Inventory.dto.SupplierOrderDTO;
import Suppliers.service.SupplierMockService;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * SupplierIntegrationService – עובד עם DB במקום Set ב-RAM.
 * כל הזמנה נשמרת ב-supplier_orders ומתעדכנת שם.
 */
public class SupplierIntegrationService {

    private final SupplierMockService supplierMockService;
    private final SupplierOrderDAO orderDAO;

    public SupplierIntegrationService() {
        this.supplierMockService = new SupplierMockService();
        this.orderDAO            = new JdbcSupplierOrderDAO();
    }

    // ── יצירת הזמנה אוטומטית אם מתחת לסף ───────────────────────────────────

    public boolean createAutomaticOrderIfNeeded(Product product) {
        if (product == null || product.getInventory() == null) return false;
        if (!product.checkMinThreshold()) return false;

        int quantity = calculateQuantityToOrder(product);
        return createOrder(product, quantity);
    }

    // ── יצירת הזמנה ידנית ────────────────────────────────────────────────────

    public boolean createManualOrder(Product product, int quantityToOrder) {
        if (product == null || product.getInventory() == null) return false;
        if (quantityToOrder <= 0) return false;
        return createOrder(product, quantityToOrder);
    }

    // ── לוגיקה פנימית ────────────────────────────────────────────────────────

    private boolean createOrder(Product product, int quantityToOrder) {
        if (quantityToOrder <= 0) return false;

        // בדוק אם יש הזמנה פעילה קיימת ב-DB
        if (hasActiveOrderForProduct(product.getProductID())) {
            System.out.println("[ORDER BLOCKED] Product " + product.getProductID()
                    + " already has an active order.");
            return false;
        }

        int selectedSupplierID = supplierMockService.findBestSupplierForOrder(
                product.getProductID(), quantityToOrder);

        // צור domain object לצורך שליחה ל-Mock
        SupplierOrder order = new SupplierOrder(
                0,  // ה-ID ייקבע ע"י ה-DB
                product.getProductID(),
                product.getProductName(),
                selectedSupplierID,
                product.getSupplierCatalogID(),
                quantityToOrder
        );

        boolean sent = supplierMockService.sendOrder(order);
        if (!sent) return false;

        // שמור ל-DB
        try {
            SupplierOrderDTO dto = new SupplierOrderDTO(
                    0,
                    product.getProductID(),
                    product.getProductName(),
                    selectedSupplierID,
                    product.getSupplierCatalogID(),
                    quantityToOrder,
                    OrderStatus.SENT.name(),
                    LocalDateTime.now().toString()
            );
            int newId = orderDAO.save(dto);
            System.out.println("[ORDER] Created order #" + newId
                    + " for product " + product.getProductName()
                    + " qty=" + quantityToOrder
                    + " supplier=" + selectedSupplierID);
            return true;
        } catch (SQLException e) {
            System.err.println("[DB] createOrder failed: " + e.getMessage());
            return false;
        }
    }

    private int calculateQuantityToOrder(Product product) {
        int current      = product.getInventory().getTotalQuantity();
        int minThreshold = product.getInventory().getMinQuantityThreshold();
        return (minThreshold + 1) - current;
    }

    // ── שאילתות ──────────────────────────────────────────────────────────────

    public boolean hasActiveOrderForProduct(int productID) {
        try {
            return !orderDAO.findActiveByProductId(productID).isEmpty();
        } catch (SQLException e) {
            System.err.println("[DB] hasActiveOrder failed: " + e.getMessage());
            return false;
        }
    }

    public boolean markOrderAsReceived(int productID) {
        try {
            List<SupplierOrderDTO> active = orderDAO.findActiveByProductId(productID);
            if (active.isEmpty()) return false;
            orderDAO.updateStatus(active.get(0).orderId(), OrderStatus.RECEIVED.name());
            System.out.println("[ORDER] Order for product " + productID + " marked as RECEIVED.");
            return true;
        } catch (SQLException e) {
            System.err.println("[DB] markOrderAsReceived failed: " + e.getMessage());
            return false;
        }
    }

    public boolean cancelActiveOrder(int productID) {
        try {
            List<SupplierOrderDTO> active = orderDAO.findActiveByProductId(productID);
            if (active.isEmpty()) return false;
            orderDAO.updateStatus(active.get(0).orderId(), OrderStatus.CANCELLED.name());
            System.out.println("[ORDER] Order for product " + productID + " CANCELLED.");
            return true;
        } catch (SQLException e) {
            System.err.println("[DB] cancelActiveOrder failed: " + e.getMessage());
            return false;
        }
    }

    public List<SupplierOrderDTO> getAllOrders() {
        try {
            return orderDAO.findAll();
        } catch (SQLException e) {
            System.err.println("[DB] getAllOrders failed: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<SupplierOrderDTO> getOrdersForProduct(int productID) {
        try {
            return orderDAO.findByProductId(productID);
        } catch (SQLException e) {
            System.err.println("[DB] getOrdersForProduct failed: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}