// CategoryReport.java
package Inventory.domain;

import java.util.List;

public class CategoryReport extends InventoryReport {
    private final List<String> categoryNames;
    private final List<Product> products;

    public CategoryReport(List<String> categoryNames, List<Product> products) {
        super();
        this.categoryNames = categoryNames;
        this.products = products;
    }

    // Getters
    public List<String> getCategoryNames() { return categoryNames; }
    public List<Product> getProducts() { return products; }
}