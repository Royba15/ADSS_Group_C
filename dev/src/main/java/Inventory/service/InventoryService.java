package Inventory.service;

import Inventory.domain.*;
import Inventory.init.Datainit;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryService {
    private final List<Product> products;
    private final List<DefectiveItem> defectiveItems;
    private final List<Category> categories;
    private final Map<Category, List<Product>> categoryToProducts;

    // constructor
    public InventoryService() {
        this.products = new ArrayList<>();
        this.defectiveItems = new ArrayList<>();
        this.categories=new ArrayList<>();
        this.categoryToProducts = new HashMap<>();
    }

    // function init all the data
    public void initializeData() {
        Datainit initializer = new Datainit(this);
        initializer.initializeData();
    }

    // add to lists (prod, map)
    public void addProduct(Product p) {
        if (p == null) return;
        products.add(p);
        addToMap(p.getMainCategory(), p);
        addToMap(p.getSubCategory(), p);
        addToMap(p.getSubSubCategory(), p);
    }

    private void addToMap(Category cat, Product p) {
        categoryToProducts.putIfAbsent(cat, new ArrayList<>());
        categoryToProducts.get(cat).add(p);
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

    public void addCategory(Category c) {
        if (c != null) categories.add(c);
    }

    public List<Category> getCategories() {
        return new ArrayList<>(categories);
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

    // Report Defective Product
    public void reportDefectiveItem(int productID, int quantity, String reason) {
        Product p = getProductByID(productID);
        if (p == null) return;
        int newID = defectiveItems.size() + 1;
        DefectiveItem item = new DefectiveItem(newID, p, quantity, reason);
        defectiveItems.add(item);
    }

    public int getTotalDefectiveCountForProduct(int productID) {
        int totalDefective = 0;
        for (DefectiveItem item : defectiveItems) {
            if (item.getProduct().getProductID() == productID) {
                totalDefective += item.getQuantity();
            }
        }
        return totalDefective;
    }

    public CategoryReport generateCategoryReport(List<String> categoryNames) {
        List<Product> result = new ArrayList<>();
        for (String name : categoryNames) {
            Category key = new Category(name, 0);
            if (categoryToProducts.containsKey(key)) {
                result.addAll(categoryToProducts.get(key));
            }
        }
        return new CategoryReport(categoryNames, result);
    }

    public DefectiveReport generateDefectiveReport() {
        return new DefectiveReport(new ArrayList<>(defectiveItems));
    }

    public OrderReport generateOrderReport() {
        List<Product> toOrder = new ArrayList<>();
        for (Product p : products) {
            if (p.getInventory().getTotalQuantity()==0) toOrder.add(p);
        }
        return new OrderReport(toOrder);
    }

    public boolean applyDiscountToProduct(int productID, String promoName, double discount, LocalDateTime start, LocalDateTime end) {
        Product p = getProductByID(productID);
        if (p == null) return false;
        int newID = products.indexOf(p) + 1;
        DiscountPromotion promo = new DiscountPromotion(newID, promoName, discount, start, end);
        p.assignPromotion(promo);
        return true;
    }

    public boolean applyDiscountToCategory(String categoryName, String promoName, double discount, LocalDateTime start, LocalDateTime end) {
        Category key = new Category(categoryName, 0);
        if (!categoryToProducts.containsKey(key)) return false;
        int newID = categoryName.hashCode();
        DiscountPromotion promo = new DiscountPromotion(newID, promoName, discount, start, end);
        for (Product p : categoryToProducts.get(key)) {
            p.assignPromotion(promo);
        }
        return true;
    }
}
