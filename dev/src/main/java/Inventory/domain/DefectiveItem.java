package Inventory.domain;

import java.time.LocalDate;

public class DefectiveItem {
    private int recordID;
    private LocalDate reportDate;
    private String reason;
    private int quantity;

    public void reportDefective(int qty, String reason) {
        this.quantity = qty;
        this.reason = reason;
        this.reportDate = LocalDate.now();
    }

    public String getDefectiveDetails() {
        return "Record: " + recordID + ", Date: " + reportDate + ", Reason: " + reason + ", Qty: " + quantity;
    }

    // getter & setter
    public int getRecordID() {
        return recordID;
    }
    public void setRecordID(int recordID) {
        this.recordID = recordID;
    }
    public LocalDate getReportDate() {
        return reportDate;
    }
    public void setReportDate(LocalDate reportDate) {
        this.reportDate = reportDate;
    }
    public String getReason() {
        return reason;
    }
    public void setReason(String reason) {
        this.reason = reason;
    }
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
