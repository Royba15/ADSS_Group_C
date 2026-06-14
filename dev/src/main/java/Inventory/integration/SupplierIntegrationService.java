package Inventory.integration;

import Inventory.domain.Product;
import Inventory.domain.SupplierOrder;
import Suppliers.service.SupplierMockService;

import java.util.HashSet;
import java.util.Set;

public class SupplierIntegrationService {
    private final SupplierMockService supplierMockService;
    private final Set<SupplierOrder> orders;
    private int nextOrderID;

    public SupplierIntegrationService() {
        this.supplierMockService = new SupplierMockService();
        this.orders = new HashSet<>();
        this.nextOrderID = 1;
    }

    public boolean createAutomaticOrderIfNeeded(Product product) {
        if (product == null || product.getInventory() == null) {
            return false;
        }

        if (!product.checkMinThreshold()) {
            return false;
        }

        int quantityToOrder = calculateQuantityToOrder(product);

        return createOrder(product, quantityToOrder);
    }

    public boolean createManualOrder(Product product, int quantityToOrder) {
        if (product == null || product.getInventory() == null) {
            return false;
        }

        if (quantityToOrder <= 0) {
            return false;
        }

        return createOrder(product, quantityToOrder);
    }

    private boolean createOrder(Product product, int quantityToOrder) {
        if (hasActiveOrderForProduct(product.getProductID())) {
            System.out.println("[ORDER BLOCKED] Product already has an active order.");
            return false;
        }

        if (quantityToOrder <= 0) {
            return false;
        }

        int selectedSupplierID = supplierMockService.findBestSupplierForOrder(
                product.getProductID(),
                quantityToOrder
        );

        SupplierOrder order = new SupplierOrder(
                nextOrderID,
                product.getProductID(),
                product.getProductName(),
                selectedSupplierID,
                product.getSupplierCatalogID(),
                quantityToOrder
        );

        boolean sent = supplierMockService.sendOrder(order);

        if (!sent) {
            order.cancel();
            return false;
        }

        order.markAsSent();
        orders.add(order);
        nextOrderID++;

        return true;
    }

    private int calculateQuantityToOrder(Product product) {
        int currentQuantity = product.getInventory().getTotalQuantity();
        int minThreshold = product.getInventory().getMinQuantityThreshold();

        return (minThreshold + 1) - currentQuantity;
    }

    public boolean hasActiveOrderForProduct(int productID) {
        for (SupplierOrder order : orders) {
            if (order.getProductID() == productID && order.isActiveOrder()) {
                return true;
            }
        }

        return false;
    }

    public boolean markOrderAsReceived(int productID) {
        for (SupplierOrder order : orders) {
            if (order.getProductID() == productID && order.isActiveOrder()) {
                order.markAsReceived();
                return true;
            }
        }

        return false;
    }

    public boolean cancelActiveOrder(int productID) {
        for (SupplierOrder order : orders) {
            if (order.getProductID() == productID && order.isActiveOrder()) {
                order.cancel();
                return true;
            }
        }

        return false;
    }

    public Set<SupplierOrder> getOrders() {
        return new HashSet<>(orders);
    }
}