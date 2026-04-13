package Inventory.domain;

public class InventoryLevel {
    private int shelfQuantity;
    private int warehouseQuantity;
    private int minQuantityThreshold;
    private final String location;

    // Constructor
    public InventoryLevel(int shelfQuantity, int warehouseQuantity, int minQuantityThreshold, String location) {
        if (shelfQuantity < 0 || warehouseQuantity < 0) {
            throw new IllegalArgumentException("Quantities cannot be negative");
        }
        if (minQuantityThreshold < 0) {
            throw new IllegalArgumentException("Minimum threshold cannot be negative");
        }

        this.shelfQuantity = shelfQuantity;
        this.warehouseQuantity = warehouseQuantity;
        this.minQuantityThreshold = minQuantityThreshold;
        this.location = location;
    }

    // ===== SHELF OPERATIONS =====

    // Get shelf quantity
    public int getShelfQuantity() {
        return shelfQuantity;
    }

    // Set shelf quantity directly
    public void setShelfQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        this.shelfQuantity = quantity;
    }

    // ===== WAREHOUSE OPERATIONS =====


    // Get warehouse quantity
    public int getWarehouseQuantity() {
        return warehouseQuantity;
    }

    // Set warehouse quantity directly
    public void setWarehouseQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        this.warehouseQuantity = quantity;
    }

    // Calculate total quantity (shelf + warehouse)
    public int getTotalQuantity() {
        return shelfQuantity + warehouseQuantity;
    }

    // ===== THRESHOLD OPERATIONS =====

    // Check if total quantity is below minimum threshold
    public boolean isBelowThreshold() {
        return getTotalQuantity() < minQuantityThreshold;
    }

    // Get how much below/above threshold
    public int getThresholdDifference() {
        return getTotalQuantity() - minQuantityThreshold;
    }

    // Update threshold
    public void setThreshold(int min) {
        if (min < 0) {
            throw new IllegalArgumentException("Threshold cannot be negative");
        }
        this.minQuantityThreshold = min;
    }

    public int getMinQuantityThreshold() {
        return minQuantityThreshold;
    }

    // ===== LOCATION =====

    public String getLocation() {
        return location;
    }

    // ===== SUMMARY =====

    public String getInventorySummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("Location: ").append(location)
                .append(" | Total: ").append(getTotalQuantity())
                .append(" | Shelf: ").append(shelfQuantity)
                .append(" | Warehouse: ").append(warehouseQuantity)
                .append(" | Threshold: ").append(minQuantityThreshold)
                .append(" | Status: ").append(isBelowThreshold() ? "BELOW THRESHOLD" : "OK");
        return summary.toString();
    }

    @Override
    public String toString() {
        return getInventorySummary();
    }
}