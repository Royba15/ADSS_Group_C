package Inventory.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class DefectiveItem {
    private final int recordID;
    private final Product product;
    private final LocalDateTime reportDateTime;
    private final String reason;
    private final int quantity;

    // Constructor
    public DefectiveItem(int recordID, Product product, int quantity, String reason) {
        this.recordID = recordID;
        this.product = product;
        this.quantity = quantity;
        this.reason = reason;
        this.reportDateTime = LocalDateTime.now();
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
    @Override
    public String toString() {
        return getDefectiveDetails();
    }
}