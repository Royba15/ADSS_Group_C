package Inventory.domain;

public class Product {
    private final int productID;
    private final String name;
    private final int manufacturerID;
    private final double costPrice;
    private double sellingPrice;
    private final double originalSellingPrice;
    private final String supplierCatalogID;
    private InventoryLevel inventory;
    private DiscountPromotion promotion;
    private final Category mainCategory;
    private final Category subCategory;
    private final Category subSubCategory;

    // Constructor
    public Product(int id, String name, int manufacturerID, double cost, double selling, String catalogID, Category mainCategory, Category subCategory, Category subSubCategory, InventoryLevel inventory) {
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
    public InventoryLevel getInventory() {
        return inventory;
    }
    public DiscountPromotion getPromotion() {
        return promotion;
    }
    public double getOriginalSellingPrice() { return originalSellingPrice; }
    public Category getMainCategory() { return mainCategory; }
    public Category getSubCategory() { return subCategory; }
    public Category getSubSubCategory() { return subSubCategory; }

    // Setters
    public void setInventory(InventoryLevel inventory) {
        this.inventory = inventory;
    }

    @Override
    public String toString() {
        return getDetails();
    }
}