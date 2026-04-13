package Inventory.service;

import Inventory.domain.Category;
import Inventory.domain.DefectiveItem;
import Inventory.domain.Product;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryService {
    private List<Product> products;
    private List<DefectiveItem> defectiveItems;
    private List<Category> categories;
    private Map<Category, List<Product>> categoryToProducts;

    // constructor
    public InventoryService() {
        this.products = new ArrayList<>();
        this.defectiveItems = new ArrayList<>();
        this.categories=new ArrayList<>();
        this.categoryToProducts = new HashMap<>();
    }

    // View Product by ID
    public Product getProductByID(int productID) {
        for (Product p : products) {
            if (p.getProductID() == productID) {
                return p;
            }
        }
        return null;
    }

    public boolean updateInventory(int productID, int shelfQty, int warehouseQty) {
        Product p = getProductByID(productID);
        if (p == null) return false;
        p.getInventory().updateQuantity(shelfQty, warehouseQty);
        return true;
    }

    // Alerts
    public List<Product> getLowStockProducts() {
        List<Product> alerts = new ArrayList<>();
        for (Product p : products) {
            if (p.checkMinThreshold()) {
                alerts.add(p);
            }
        }
        return alerts;
    }

    // Reports
    // Defective items report
    public List<DefectiveItem> getDefectiveItems() {
        return new ArrayList<>(defectiveItems);
    }

    // Order report — products below threshold
    public List<Product> getProductsToOrder() {
        List<Product> toOrder = new ArrayList<>();
        for (Product p : products) {
            if (p.checkMinThreshold()) {
                toOrder.add(p);
            }
        }
        return toOrder;
    }

    // Report Defective Product
    public boolean reportDefectiveItem(int productID, int quantity, String reason) {
        Product p = getProductByID(productID);
        if (p == null) return false;
        int newID = defectiveItems.size() + 1;
        DefectiveItem item = new DefectiveItem(newID, p, quantity, reason);
        defectiveItems.add(item);
        p.getInventory().reduceQuantity(quantity);
        return true;
    }

    // add to lists (prod, map)
    public void addProduct(Product p) {
        if (p == null) return;
        products.add(p);
        Category cat = p.getCategory();
        if (cat != null) {
            categoryToProducts.putIfAbsent(cat, new ArrayList<>());
            categoryToProducts.get(cat).add(p);
        }
    }

    public List<Product> getProductsByCategories(List<String> categoryNames) {
        List<Product> result = new ArrayList<>();
        for (Category cat : categoryToProducts.keySet()) {
            if (categoryNames.contains(cat.getCategoryName())) {
                result.addAll(categoryToProducts.get(cat));
            }
        }
        return result;
    }


}
