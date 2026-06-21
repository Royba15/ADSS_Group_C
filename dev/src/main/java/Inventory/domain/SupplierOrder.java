package Inventory.domain;

import java.time.LocalDateTime;
import java.util.Objects;

public class SupplierOrder {
    private final int orderID;
    private final int productID;
    private final String productName;
    private final int supplierID;
    private final String supplierCatalogID;
    private final int quantity;
    private final LocalDateTime createdAt;
    private OrderStatus status;

    public SupplierOrder(int orderID, int productID, String productName, int supplierID, String supplierCatalogID, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Order quantity must be positive");
        }

        this.orderID = orderID;
        this.productID = productID;
        this.productName = productName;
        this.supplierID = supplierID;
        this.supplierCatalogID = supplierCatalogID;
        this.quantity = quantity;
        this.createdAt = LocalDateTime.now();
        this.status = OrderStatus.CREATED;
    }

    public int getOrderID() {
        return orderID;
    }

    public int getProductID() {
        return productID;
    }

    public String getProductName() {
        return productName;
    }

    public int getSupplierID() {
        return supplierID;
    }

    public String getSupplierCatalogID() {
        return supplierCatalogID;
    }

    public int getQuantity() {
        return quantity;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public boolean isActiveOrder() {
        return status == OrderStatus.CREATED || status == OrderStatus.SENT;
    }

    public void markAsSent() {
        this.status = OrderStatus.SENT;
    }

    public void markAsReceived() {
        this.status = OrderStatus.RECEIVED;
    }

    public void cancel() {
        this.status = OrderStatus.CANCELLED;
    }

    @Override
    public String toString() {
        return "Order #" + orderID
                + " | Product: " + productName
                + " | Product ID: " + productID
                + " | Supplier ID: " + supplierID
                + " | Supplier Catalog ID: " + supplierCatalogID
                + " | Quantity: " + quantity
                + " | Status: " + status
                + " | Created At: " + createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SupplierOrder)) return false;
        SupplierOrder that = (SupplierOrder) o;
        return orderID == that.orderID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderID);
    }
}