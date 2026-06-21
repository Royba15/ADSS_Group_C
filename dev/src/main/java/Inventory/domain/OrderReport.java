// OrderReport.java
package Inventory.domain;

import java.util.List;

public class OrderReport extends InventoryReport {
    private final List<Product> productsToOrder;

    // Constructor
    public OrderReport(List<Product> productsToOrder) {
        super();
        this.productsToOrder = productsToOrder;
    }

    // Getter
    public List<Product> getProductsToOrder() { return productsToOrder; }
}