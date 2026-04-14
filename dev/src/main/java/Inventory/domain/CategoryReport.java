// CategoryReport.java
package Inventory.domain;

import java.util.List;

public class CategoryReport extends InventoryReport {
    private List<String> categoryNames;
    private List<Product> products;

    public CategoryReport(List<String> categoryNames, List<Product> products) {
        super();
        this.categoryNames = categoryNames;
        this.products = products;
    }

    public List<String> getCategoryNames() { return categoryNames; }
    public List<Product> getProducts() { return products; }

    @Override
    public String getReportType() { return "Category Report"; }
}