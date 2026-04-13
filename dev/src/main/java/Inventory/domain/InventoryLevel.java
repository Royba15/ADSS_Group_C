package Inventory.domain;

public class InventoryLevel {
    private int totalQuantity;
    private int shelfQuantity;
    private int warehouseQuantity;
    private int minQuantityThreshold;
    private final String location;

    public InventoryLevel(int totalQuantity, int shelfQuantity, int warehouseQuantity, int minQuantityThreshold, String location) {         // new
        this.totalQuantity = totalQuantity;
        this.shelfQuantity = shelfQuantity;
        this.warehouseQuantity = warehouseQuantity;
        this.minQuantityThreshold = minQuantityThreshold;
        this.location = location;
    }

    public void updateQuantity(int shelf, int warehouse) {
        this.shelfQuantity = shelf;
        this.warehouseQuantity = warehouse;
        this.totalQuantity = shelf + warehouse;
    }

    public boolean isBelowThreshold() {
        return totalQuantity < minQuantityThreshold;
    }

    public void setThreshold(int min) {
        this.minQuantityThreshold = min;
    }

    public int getTotalQuantity() {         // new
        return totalQuantity;
    }

    public String getLocation() {           // new
        return location;
    }
}
