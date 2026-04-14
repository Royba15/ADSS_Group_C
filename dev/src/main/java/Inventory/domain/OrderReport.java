// OrderReport.java
package Inventory.domain;

import java.util.List;

public class OrderReport extends InventoryReport {
    private List<Product> productsToOrder;

    public OrderReport(List<Product> productsToOrder) {
        super();
        this.productsToOrder = productsToOrder;
    }

    public List<Product> getProductsToOrder() { return productsToOrder; }

    @Override
    public String getReportType() { return "Order Report"; }
}