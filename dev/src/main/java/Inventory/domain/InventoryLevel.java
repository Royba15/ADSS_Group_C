package Inventory.domain;

public class InventoryLevel {
    private int totalQuantity;
    private int shelfQuantity;
    private int warehouseQuantity;
    private int minQuantityThreshold;
    private final String location;

    // Constructor
    public InventoryLevel(int shelfQuantity, int warehouseQuantity,
                          int minQuantityThreshold, String location) {
        if (minQuantityThreshold < 0) {
            throw new IllegalArgumentException("Minimum threshold cannot be negative");
        }

        this.totalQuantity = shelfQuantity+warehouseQuantity;
        this.shelfQuantity = shelfQuantity;
        this.warehouseQuantity = warehouseQuantity;
        this.minQuantityThreshold = minQuantityThreshold;
        this.location = location;
    }

    // Update both shelf and warehouse quantities
    public void updateQuantity(int shelf, int warehouse) {
        if (shelf < 0 || warehouse < 0) {
            throw new IllegalArgumentException("Quantities cannot be negative");
        }
        int newTotal = shelf + warehouse;

        this.shelfQuantity = shelf;
        this.warehouseQuantity = warehouse;
        this.totalQuantity = newTotal;
    }

    // Reduce quantity (when items are sold or removed)
    public boolean reduceQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        if (quantity > totalQuantity) {
            throw new IllegalArgumentException("Not enough items. Available: " + totalQuantity);
        }

        // Reduce from shelf first, then warehouse
        if (quantity <= shelfQuantity) {
            shelfQuantity -= quantity;
        } else {
            int remainingAfterShelf = quantity - shelfQuantity;
            shelfQuantity = 0;
            warehouseQuantity -= remainingAfterShelf;
        }
        totalQuantity = shelfQuantity + warehouseQuantity;
        return true;
    }

    // Check if below minimum threshold
    public boolean isBelowThreshold() {
        return totalQuantity < minQuantityThreshold;
    }

    // Update threshold
    public void setThreshold(int min) {
        if (min < 0) {
            throw new IllegalArgumentException("Threshold cannot be negative");
        }
        this.minQuantityThreshold = min;
    }

    // Get inventory summary
    public String getInventorySummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("Location: ").append(location)
                .append(" | Total: ").append(totalQuantity)
                .append(" | Shelf: ").append(shelfQuantity)
                .append(" | Warehouse: ").append(warehouseQuantity)
                .append(" | Threshold: ").append(minQuantityThreshold)
                .append(" | Status: ").append(isBelowThreshold() ? "BELOW THRESHOLD" : "OK");
        return summary.toString();
    }

    // Getters
    public int getTotalQuantity() {
        return totalQuantity;
    }

    public int getShelfQuantity() {
        return shelfQuantity;
    }

    public int getWarehouseQuantity() {
        return warehouseQuantity;
    }

    public int getMinQuantityThreshold() {
        return minQuantityThreshold;
    }

    public String getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return getInventorySummary();
    }
}