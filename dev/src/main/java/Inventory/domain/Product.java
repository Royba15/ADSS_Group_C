package Inventory.domain;

public class Product {
    private int productID;
    private String name;
    private int manufacturerID;
    private double costPrice;
    private double sellingPrice;
    private String supplierCatalogID;
    private int minimumThreshold;
    private Category category;
    private InventoryLevel inventory;
    private DiscountPromotion promotion;

    // Constructor
    public Product(int id, String name, int manufacturerID, double cost, double selling,
                   String catalogID, int minimumThreshold, Category category, InventoryLevel inventory) {
        this.productID = id;
        this.name = name;
        this.manufacturerID = manufacturerID;
        this.costPrice = cost;
        this.sellingPrice = selling;
        this.supplierCatalogID = catalogID;
        this.minimumThreshold = minimumThreshold;
        this.category = category;
        this.inventory = inventory;
        this.promotion = null;
    }

    // Check if product is below minimum threshold
    public boolean checkMinThreshold() {
        if (inventory == null) {
            return false;
        }
        return inventory.getTotalQuantity() < minimumThreshold;
    }

    // Assign a promotion to this product
    public void assignPromotion(DiscountPromotion promotion) {
        this.promotion = promotion;
        if (promotion != null) {
            promotion.applyDiscountToProduct(this);
        }
    }

    // Remove promotion from this product
    public void removePromotion() {
        this.promotion = null;
    }

    // Check if product has an active promotion
    public boolean hasActivePromotion() {
        return promotion != null && promotion.isActive() && promotion.isPromotionActive();
    }


    // Get detailed product information
    public String getDetails() {
        StringBuilder details = new StringBuilder();
        details.append("ID: ").append(productID)
                .append(" | Name: ").append(name)
                .append(" | Category: ").append(category != null ? category.getCategoryName() : "N/A")
                .append(" | Manufacturer: ").append(manufacturerID)
                .append(" | Selling Price: $").append(String.format("%.2f", sellingPrice));

        if (hasActivePromotion()) {
            details.append(" | PROMOTION: ").append(promotion.getSummary());
        }

        details.append(" | Min Threshold: ").append(minimumThreshold);

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
        return minimumThreshold;
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

    public void setMinimumThreshold(int threshold) {
        if (threshold > 0) {
            this.minimumThreshold = threshold;
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
}