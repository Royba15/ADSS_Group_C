package Inventory.domain;

import java.time.LocalDateTime;

// Abstract class
public abstract class InventoryReport {
    private final LocalDateTime date;
    // Constructor
    public InventoryReport() {
        this.date = LocalDateTime.now();
    }

    // Getters
    public LocalDateTime getDate() { return date; }

}