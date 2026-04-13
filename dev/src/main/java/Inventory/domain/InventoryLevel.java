package Inventory.domain;

public class InventoryLevel {
    private int totalQuantity;
    private int shelfQuantity;
    private int warehouseQuantity;
    private int minQuantityThreshold;
    private int maxCapacity;
    private final String location;

    // Constructor
    public InventoryLevel(int totalQuantity, int shelfQuantity, int warehouseQuantity,
                          int minQuantityThreshold, String location) {
        if (shelfQuantity + warehouseQuantity != totalQuantity) {
            throw new IllegalArgumentException("Shelf quantity + warehouse quantity must equal total quantity");
        }
        if (minQuantityThreshold < 0) {
            throw new IllegalArgumentException("Minimum threshold cannot be negative");
        }

        this.totalQuantity = totalQuantity;
        this.shelfQuantity = shelfQuantity;
        this.warehouseQuantity = warehouseQuantity;
        this.minQuantityThreshold = minQuantityThreshold;
        this.location = location;
        this.maxCapacity = totalQuantity * 2;  // Default: double the initial quantity
    }

    // Constructor with max capacity
    public InventoryLevel(int totalQuantity, int shelfQuantity, int warehouseQuantity,
                          int minQuantityThreshold, int maxCapacity, String location) {
        if (shelfQuantity + warehouseQuantity != totalQuantity) {
            throw new IllegalArgumentException("Shelf quantity + warehouse quantity must equal total quantity");
        }
        if (totalQuantity > maxCapacity) {
            throw new IllegalArgumentException("Total quantity cannot exceed max capacity");
        }
        if (minQuantityThreshold < 0) {
            throw new IllegalArgumentException("Minimum threshold cannot be negative");
        }

        this.totalQuantity = totalQuantity;
        this.shelfQuantity = shelfQuantity;
        this.warehouseQuantity = warehouseQuantity;
        this.minQuantityThreshold = minQuantityThreshold;
        this.maxCapacity = maxCapacity;
        this.location = location;
    }

    // Update both shelf and warehouse quantities
    public void updateQuantity(int shelf, int warehouse) {
        if (shelf < 0 || warehouse < 0) {
            throw new IllegalArgumentException("Quantities cannot be negative");
        }
        int newTotal = shelf + warehouse;
        if (newTotal > maxCapacity) {
            throw new IllegalArgumentException("Total would exceed max capacity of " + maxCapacity);
        }

        this.shelfQuantity = shelf;
        this.warehouseQuantity = warehouse;
        this.totalQuantity = newTotal;
    }

    // Move items from warehouse to shelf
    public boolean moveToShelf(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        if (quantity > warehouseQuantity) {
            throw new IllegalArgumentException("Not enough items in warehouse. Available: " + warehouseQuantity);
        }

        warehouseQuantity -= quantity;
        shelfQuantity += quantity;
        return true;
    }

    // Move items from shelf back to warehouse
    public boolean moveToWarehouse(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        if (quantity > shelfQuantity) {
            throw new IllegalArgumentException("Not enough items on shelf. Available: " + shelfQuantity);
        }

        shelfQuantity -= quantity;
        warehouseQuantity += quantity;
        return true;
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

    // Add quantity (when new stock arrives)
    public boolean addQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        if (totalQuantity + quantity > maxCapacity) {
            throw new IllegalArgumentException("Exceeds max capacity. Max: " + maxCapacity +
                    ", Current: " + totalQuantity + ", Trying to add: " + quantity);
        }

        warehouseQuantity += quantity;
        totalQuantity = shelfQuantity + warehouseQuantity;
        return true;
    }

    // Check if below minimum threshold
    public boolean isBelowThreshold() {
        return totalQuantity < minQuantityThreshold;
    }

    // Get how much below threshold (if negative, means how much over)
    public int getThresholdDifference() {
        return totalQuantity - minQuantityThreshold;
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
                .append(" | Max Capacity: ").append(maxCapacity)
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

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public String getLocation() {
        return location;
    }

    // Setters
    public void setMaxCapacity(int maxCapacity) {
        if (maxCapacity < totalQuantity) {
            throw new IllegalArgumentException("Max capacity cannot be less than current total quantity");
        }
        this.maxCapacity = maxCapacity;
    }

    @Override
    public String toString() {
        return getInventorySummary();
    }
}