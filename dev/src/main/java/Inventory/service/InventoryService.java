package Inventory.service;
import Inventory.integration.SupplierIntegrationService;
import java.util.Set;
import Inventory.domain.*;
import Inventory.DB.Datainit;

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
    private final SupplierIntegrationService supplierIntegrationService;

    // constructor
    public InventoryService() {
        this.products = new ArrayList<>();
        this.defectiveItems = new ArrayList<>();
        this.categories=new ArrayList<>();
        this.categoryToProducts = new HashMap<>();
        this.supplierIntegrationService = new SupplierIntegrationService();
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
        supplierIntegrationService.createAutomaticOrderIfNeeded(p);
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
            if (p.checkMinThreshold()) toOrder.add(p);
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

    // Add a new category
    public boolean addNewCategory(String categoryName, int level) {
        if (categoryName == null || categoryName.trim().isEmpty()) {
            return false;
        }
        if (level < 0 || level > 2) {
            return false;
        }

        Category newCategory = new Category(categoryName.trim(), level);

        // Check if category already exists
        if (categories.contains(newCategory)) {
            return false;
        }

        categories.add(newCategory);
        return true;
    }

    // Add a new product with all details
    // Note: Categories must already exist - we don't create them automatically
    public boolean addNewProduct(int productID, String productName, int supplierID, double costPrice, double sellingPrice, String catalogID, String mainCategoryName, String subCategoryName, String subSubCategoryName, int shelfQuantity, int warehouseQuantity, int minThreshold, String location) {

        // Validation
        if (productName == null || productName.trim().isEmpty()) {
            return false;
        }

        if (costPrice < 0 || sellingPrice < 0) {
            return false;
        }

        if (shelfQuantity < 0 || warehouseQuantity < 0 || minThreshold < 0) {
            return false;
        }

        // Check if product ID already exists
        if (getProductByID(productID) != null) {
            return false;
        }

        // Check if all categories exist - they must be created beforehand
        Category mainCategory = new Category(mainCategoryName, 0);
        Category subCategory = new Category(subCategoryName, 1);
        Category subSubCategory = new Category(subSubCategoryName, 2);

        if (!categories.contains(mainCategory)) {
            return false; // Main category doesn't exist
        }
        if (!categories.contains(subCategory)) {
            return false; // Sub category doesn't exist
        }
        if (!categories.contains(subSubCategory)) {
            return false; // Sub-sub category doesn't exist
        }

        // Create inventory
        InventoryLevel inventory = new InventoryLevel(shelfQuantity, warehouseQuantity, minThreshold, location);

        // Create product with existing categories
        Product newProduct = new Product(productID, productName.trim(), supplierID,
                costPrice, sellingPrice, catalogID, mainCategory, subCategory, subSubCategory, inventory);

        // Add product
        addProduct(newProduct);
        return true;
    }

    // Get the next available product ID
    public int getNextProductID() {
        if (products.isEmpty()) {
            return 1;
        }
        int maxID = 0;
        for (Product p : products) {
            if (p.getProductID() > maxID) {
                maxID = p.getProductID();
            }
        }
        return maxID + 1;
    }

    // Get all categories by level
    public List<Category> getCategoriesByLevel(int level) {
        List<Category> result = new ArrayList<>();
        for (Category c : categories) {
            if (c.getLevel() == level) {
                result.add(c);
            }
        }
        return result;
    }

    // Check if all three categories exist
    public boolean allCategoriesExist(String mainCatName, String subCatName, String subSubCatName) {
        Category mainCat = new Category(mainCatName, 0);
        Category subCat = new Category(subCatName, 1);
        Category subSubCat = new Category(subSubCatName, 2);

        return categories.contains(mainCat) &&
                categories.contains(subCat) &&
                categories.contains(subSubCat);
    }

    // Check if a category exists (regardless of level)
    public boolean categoryExists(String categoryName) {
        for (Category c : categories) {
            if (c.getName().equalsIgnoreCase(categoryName)) {
                return true;
            }
        }
        return false;
    }

    // Get all products
    public List<Product> getAllProducts() {
        return new ArrayList<>(products);
    }

    // Delete a product by ID (fixes "Missing Product Delete" feedback)
    public boolean deleteProduct(int productID) {
        Product p = getProductByID(productID);
        if (p == null) return false;
        products.remove(p);
        removeFromMap(p.getMainCategory(), p);
        removeFromMap(p.getSubCategory(), p);
        removeFromMap(p.getSubSubCategory(), p);
        return true;
    }

    private void removeFromMap(Category cat, Product p) {
        List<Product> list = categoryToProducts.get(cat);
        if (list != null) list.remove(p);
    }

    // Apply discount to all products of a specific supplier (fixes Req 6 feedback) /////// צריך לתקן כי צריך להחליט האם supplierCatalogID זה המשתנה שאליו אנחנו מתייחסים כשמדובר בספק, ובנוסף לבדוק האם צריך לשנות את זה למספר אינטגר או להשאיר סטרינג
    public boolean applyDiscountToSupplier(int supplierID, String promoName, double discount,
                                           LocalDateTime start, LocalDateTime end) {
        if (discount < 0 || discount > 100)
            throw new IllegalArgumentException("Discount must be between 0 and 100");
        if (start == null || end == null || !end.isAfter(start))
            throw new IllegalArgumentException("End date must be after start date");

        boolean found = false;
        DiscountPromotion promo = new DiscountPromotion(supplierID, promoName, discount, start, end);
        for (Product p : products) {
            if (p.getSupplierID() == supplierID) {
                p.assignPromotion(promo);
                found = true;
            }
        }
        return found;
    }

    public boolean createManualSupplierOrder(int productID, int quantityToOrder) {
        Product p = getProductByID(productID);

        if (p == null) {
            return false;
        }

        if (quantityToOrder <= 0) {
            return false;
        }

        return supplierIntegrationService.createManualOrder(p, quantityToOrder);
    }
}