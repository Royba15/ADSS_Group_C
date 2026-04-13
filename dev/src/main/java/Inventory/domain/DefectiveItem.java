package Inventory.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class DefectiveItem {
    private int recordID;
    private Product product;
    private LocalDateTime reportDateTime;
    private String reason;
    private int quantity;

    // Constructor
    public DefectiveItem(int recordID, Product product, int quantity, String reason) {
        this.recordID = recordID;
        this.product = product;
        this.quantity = quantity;
        this.reason = reason;
        this.reportDateTime = LocalDateTime.now();
    }

    // Report defective items
    public void reportDefective(int qty, String reason) {
        if (qty <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        if (reason == null || reason.isEmpty()) {
            throw new IllegalArgumentException("Reason cannot be empty");
        }

        this.quantity = qty;
        this.reason = reason;
        this.reportDateTime = LocalDateTime.now();
    }

    // Get detailed information about the defective item
    public String getDefectiveDetails() {
        StringBuilder details = new StringBuilder();
        details.append("Record: ").append(recordID)
                .append(" | Product: ").append(product != null ? product.getProductName() : "N/A")
                .append(" | Date: ").append(reportDateTime)
                .append(" | Reason: ").append(reason)
                .append(" | Qty: ").append(quantity);
        return details.toString();
    }

    // Getters
    public int getRecordID() {
        return recordID;
    }

    public Product getProduct() {
        return product;
    }

    public LocalDateTime getReportDateTime() {
        return reportDateTime;
    }

    public String getReason() {
        return reason;
    }

    public int getQuantity() {
        return quantity;
    }

    // Setters
    public void setRecordID(int recordID) {
        this.recordID = recordID;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setReason(String reason) {
        if (reason != null && !reason.isEmpty()) {
            this.reason = reason;
        }
    }

    public void setQuantity(int quantity) {
        if (quantity > 0) {
            this.quantity = quantity;
        }
    }

    @Override
    public String toString() {
        return getDefectiveDetails();
    }
}