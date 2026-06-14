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
        if (product == null || product.getInventory() == null) {return false;}
        if (!product.checkMinThreshold()) {return false;}
        int quantity = calculateQuantityToOrder(product);
        // Automatic orders should not create duplicates
        return createOrder(product, quantity, false);
    }

    // ── יצירת הזמנה ידנית ────────────────────────────────────────────────────

    public boolean createManualOrder(Product product, int quantityToOrder, boolean allowDuplicateOrder) {
        if (product == null || product.getInventory() == null) {return false;}
        if (quantityToOrder <= 0) {return false;}
        return createOrder(product, quantityToOrder, allowDuplicateOrder);
    }

    // ── לוגיקה פנימית ────────────────────────────────────────────────────────

    private boolean createOrder(Product product, int quantityToOrder, boolean allowDuplicateOrder) {
        if (quantityToOrder <= 0) {
            return false;
        }

        boolean hasActiveOrder = hasActiveOrderForProduct(product.getProductID());
        if (hasActiveOrder && !allowDuplicateOrder) {
            return false;
        }

        int selectedSupplierID = supplierMockService.findBestSupplierForOrder(product.getProductID(), quantityToOrder);

        try {
            SupplierOrderDTO createdDto = new SupplierOrderDTO(
                    0,
                    product.getProductID(),
                    product.getProductName(),
                    selectedSupplierID,
                    product.getSupplierCatalogID(),
                    quantityToOrder,
                    OrderStatus.CREATED.name(),
                    "IMMEDIATE",                   // orderType ← חדש
                    null,                          // scheduledDate ← חדש
                    null,                          // frequency ← חדש
                    LocalDateTime.now().toString()
            );

            int newId = orderDAO.save(createdDto);

            SupplierOrder order = new SupplierOrder(
                    newId,
                    product.getProductID(),
                    product.getProductName(),
                    selectedSupplierID,
                    product.getSupplierCatalogID(),
                    quantityToOrder
            );

            boolean sent = supplierMockService.sendOrder(order);

            if (!sent) {
                orderDAO.updateStatus(newId, OrderStatus.CANCELLED.name());
                return false;
            }

            orderDAO.updateStatus(newId, OrderStatus.SENT.name());

            System.out.println("[ORDER] Created order #" + newId
                    + " for product " + product.getProductName()
                    + " qty=" + quantityToOrder
                    + " supplier=" + selectedSupplierID
                    + " status=" + OrderStatus.SENT.name());

            return true;
        } catch (SQLException e) {
            System.err.println("[DB] createOrder failed: " + e.getMessage());
            return false;
        }
    }

    private int calculateQuantityToOrder(Product product) {
        int minThreshold = product.getInventory().getMinQuantityThreshold();
        return (minThreshold*2);
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

    public List<SupplierOrderDTO> getActiveOrdersForProduct(int productID) {
        try {
            return orderDAO.findActiveByProductId(productID);
        } catch (SQLException e) {
            System.err.println("[DB] getActiveOrdersForProduct failed: " + e.getMessage());
            return new ArrayList<>();
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
    public List<SupplierOrderDTO> getActiveOrders() {
        try {
            return orderDAO.findAll().stream()
                    .filter(o -> o.status().equals("CREATED") || o.status().equals("SENT"))
                    .collect(java.util.stream.Collectors.toList());
        } catch (SQLException e) {
            System.err.println("[DB] getActiveOrders failed: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public SupplierOrderDTO findOrderById(int orderId) {
        try {
            return orderDAO.findById(orderId).orElse(null);
        } catch (SQLException e) {
            System.err.println("[DB] findOrderById failed: " + e.getMessage());
            return null;
        }
    }

    public void receiveOrder(int orderId) {
        try {
            orderDAO.updateStatus(orderId, "RECEIVED");
        } catch (SQLException e) {
            System.err.println("[DB] receiveOrder failed: " + e.getMessage());
        }
    }
    public boolean createScheduledOrder(Product product, int quantity,
                                        String scheduledDate, String frequency) {
        // בדוק תאריך — חייב להיות לפחות מחר
        try {
            java.time.LocalDate scheduled = java.time.LocalDate.parse(scheduledDate);
            if (!scheduled.isAfter(java.time.LocalDate.now())) {
                System.out.println("[ORDER] Scheduled date must be at least tomorrow.");
                return false;
            }
        } catch (Exception e) {
            System.out.println("[ORDER] Invalid date format.");
            return false;
        }

        int supplierId = supplierMockService.findBestSupplierForOrder(
                product.getProductID(), quantity);

        try {
            SupplierOrderDTO dto = new SupplierOrderDTO(
                    0,
                    product.getProductID(),
                    product.getProductName(),
                    supplierId,
                    product.getSupplierCatalogID(),
                    quantity,
                    "PENDING",
                    "SCHEDULED",
                    scheduledDate,
                    frequency,
                    java.time.LocalDateTime.now().toString()
            );
            int newId = orderDAO.save(dto);
            System.out.println("[ORDER] Scheduled order #" + newId + " created for " + scheduledDate);
            return true;
        } catch (SQLException e) {
            System.err.println("[DB] createScheduledOrder failed: " + e.getMessage());
            return false;
        }
    }
}