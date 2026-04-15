package Inventory.domain;

public class Product {
    private int productID;
    private String name;
    private int manufacturerID;
    private double costPrice;
    private double sellingPrice;
    private double originalSellingPrice;
    private String supplierCatalogID;
    private Category category;
    private InventoryLevel inventory;
    private DiscountPromotion promotion;
    private Category mainCategory;
    private Category subCategory;
    private Category subSubCategory;

    // Constructor
    public Product(int id, String name, int manufacturerID, double cost, double selling,
                   String catalogID,
                   Category mainCategory, Category subCategory, Category subSubCategory,
                   InventoryLevel inventory) {
        this.productID = id;
        this.name = name;
        this.manufacturerID = manufacturerID;
        this.costPrice = cost;
        this.sellingPrice = selling;
        this.originalSellingPrice = selling;
        this.supplierCatalogID = catalogID;
        this.mainCategory = mainCategory;
        this.subCategory = subCategory;
        this.subSubCategory = subSubCategory;
        this.inventory = inventory;
        this.promotion = null;
    }


    // Check if product is below minimum threshold
    public boolean checkMinThreshold() {
        if (inventory == null) {
            return false;
        }
        return inventory.getTotalQuantity() < inventory.getMinQuantityThreshold();
    }

    // Assign a promotion to this product
    public void assignPromotion(DiscountPromotion promotion) {
        this.promotion = promotion;
        if (promotion != null) {
            this.sellingPrice = originalSellingPrice;
            promotion.applyDiscountToProduct(this);
        } else {
            this.sellingPrice = originalSellingPrice;
        }
    }

    public void removePromotion() {
        this.promotion = null;
        this.sellingPrice = originalSellingPrice;
    }

    // Check if product has an active promotion
    public boolean hasActivePromotion() {
        return promotion != null && promotion.isActive() && promotion.isPromotionActive();
    }

    // Update selling price
    public void setSellingPrice(double sellingPrice) {
        if (sellingPrice < 0) {
            throw new IllegalArgumentException("Selling price cannot be negative");
        }
        this.sellingPrice = sellingPrice;
    }


    // Get detailed product information
    public String getDetails() {
        StringBuilder details = new StringBuilder();
        details.append("ID: ").append(productID)
                .append(" | Name: ").append(name)
                .append(" | Main: ").append(mainCategory.getName())
                .append(" | Sub: ").append(subCategory.getName())
                .append(" | SubSub: ").append(subSubCategory.getName())
                .append(" | Manufacturer: ").append(manufacturerID)
                .append(" | Selling Price: $").append(String.format("%.2f", sellingPrice));

        if (hasActivePromotion()) {
            details.append(" | PROMOTION: ").append(promotion.getSummary());
        }

        details.append(" | Min Threshold: ").append(inventory.getMinQuantityThreshold());

        if (inventory != null) {
            details.append(" | Location: ").append(inventory.getLocation())
                    .append(" | Shelf: ").append(inventory.getShelfQuantity())
                    .append(" | Warehouse: ").append(inventory.getWarehouseQuantity())
                    .append(" | Total: ").append(inventory.getTotalQuantity())
                    .append(" | Status: ").append(checkMinThreshold() ? "BELOW THRESHOLD" : "OK");
        }

        return details.toString();
    }

    // Get a concise summary
    public String getSummary() {
        if (inventory != null) {
            return name + " (ID: " + productID + ") - " + inventory.getTotalQuantity() +
                    " units @ $" + String.format("%.2f", sellingPrice);
        }
        return name + " (ID: " + productID + ") - $" + String.format("%.2f", sellingPrice);
    }

    // Getters
    public int getProductID() {
        return productID;
    }

    public String getProductName() {
        return name;
    }

    public int getManufacturerID() {
        return manufacturerID;
    }

    public double getCostPrice() {
        return costPrice;
    }

    public double getSellingPrice() {
        return sellingPrice;
    }

    public String getSupplierCatalogID() {
        return supplierCatalogID;
    }

    public int getMinimumThreshold() {
        return inventory.getMinQuantityThreshold();
    }

    public Category getCategory() {
        return category;
    }

    public InventoryLevel getInventory() {
        return inventory;
    }

    public DiscountPromotion getPromotion() {
        return promotion;
    }

    public double getOriginalSellingPrice() { return originalSellingPrice; }

    // Setters
    public void setProductID(int productID) {
        this.productID = productID;
    }

    public void setProductName(String name) {
        if (name != null && !name.isEmpty()) {
            this.name = name;
        }
    }

    public void setManufacturerID(int manufacturerID) {
        this.manufacturerID = manufacturerID;
    }

    public void setSupplierCatalogID(String catalogID) {
        if (catalogID != null && !catalogID.isEmpty()) {
            this.supplierCatalogID = catalogID;
        }
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setInventory(InventoryLevel inventory) {
        this.inventory = inventory;
    }

    @Override
    public String toString() {
        return getDetails();
    }


    public Category getMainCategory() { return mainCategory; }
    public Category getSubCategory() { return subCategory; }
    public Category getSubSubCategory() { return subSubCategory; }

    public void setMainCategory(Category c) { this.mainCategory = c; }
    public void setSubCategory(Category c) { this.subCategory = c; }
    public void setSubSubCategory(Category c) { this.subSubCategory = c; }

    public boolean belongsToCategory(String categoryName) {
        return mainCategory.getName().equals(categoryName) ||
                subCategory.getName().equals(categoryName) ||
                subSubCategory.getName().equals(categoryName);
    }
}