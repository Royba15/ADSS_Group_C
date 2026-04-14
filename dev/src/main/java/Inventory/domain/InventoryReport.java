package Inventory.domain;

import java.time.LocalDateTime;

public abstract class InventoryReport {
    private LocalDateTime date;

    public InventoryReport() {
        this.date = LocalDateTime.now();
    }

    public LocalDateTime getDate() { return date; }
    public abstract String getReportType();
}