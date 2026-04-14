package Inventory.presentation;

import Inventory.domain.*;

import java.util.ArrayList;
import java.util.List;

public class ConsolePrinter {
    private final String DIVIDER = "========================================";
    public void printHeader(String title) {
        System.out.println(DIVIDER);
        System.out.println("   " + title);
        System.out.println(DIVIDER);
    }
    // Main menu
    public void printMainMenu() {
        printHeader("INVENTORY MANAGEMENT");
        System.out.println("1. Update Inventory");
        System.out.println("2. View Product by ID");
        System.out.println("3. Alerts");
        System.out.println("4. Reports");
        System.out.println("0. Exit");
        System.out.print("Choose an option: ");
    }
    // Report Menu
    public void printReportsMenu() {
        printHeader("REPORTS");
        System.out.println("1. Defective Items Report");
        System.out.println("2. Inventory Report by Category");
        System.out.println("3. Order Report");
        System.out.println("0. Back");
        System.out.print("Choose an option: ");
    }
    // print all the defective products with all the details
    public void printDefectiveReport(DefectiveReport report) {
        printHeader("DEFECTIVE ITEMS REPORT");
        if (report.getDefectiveItems().isEmpty()) {
            System.out.println("No defective items found.");
            return;
        }
        for (DefectiveItem item : report.getDefectiveItems()) {
            System.out.println("- " + item.getProduct().getProductName()
                    + " | Reason: " + item.getReason()
                    + " | Qty: " + item.getQuantity()
                    + " | Date: " + item.getReportDateTime());
        }
    }
    // Print product
    public void printProduct(Product p) {
        printHeader("PRODUCT DETAILS");
        System.out.println("ID:            " + p.getProductID());
        System.out.println("Name:          " + p.getProductName());
        System.out.println("Manufacturer:  " + p.getManufacturerID());
        System.out.println("Total Qty:     " + p.getInventory().getTotalQuantity());
        System.out.println("Shelf Qty:     " + p.getInventory().getShelfQuantity());
        System.out.println("Warehouse Qty: " + p.getInventory().getWarehouseQuantity());
        System.out.println("Location:      " + p.getInventory().getLocation());
        System.out.println("Cost Price:    " + p.getCostPrice());
        System.out.println("Sell Price:    " + p.getSellingPrice());
        System.out.println("Min Threshold: " + p.getMinimumThreshold());
    }
    // Print all products
    public void printOrderReport(OrderReport report) {
        printHeader("ORDER REPORT");
        if (report.getProductsToOrder().isEmpty()) {
            System.out.println("No items need to be ordered.");
            return;
        }
        for (Product p : report.getProductsToOrder()) {
            System.out.println("- " + p.getProductName()
                    + " | Current: " + p.getInventory().getTotalQuantity()
                    + " | Minimum: " + p.getMinimumThreshold());
        }
    }
    // Print all products by category
    public void printCategoryReport(CategoryReport report) {
        printHeader("INVENTORY REPORT");
        if (report.getProducts().isEmpty()) {
            System.out.println("No products found in these categories.");
            return;
        }
        // track printed products to avoid duplicates
        List<Integer> printed = new ArrayList<>();
        for (String catName : report.getCategoryNames()) {
            System.out.println("\n--- Category: " + catName + " ---");
            boolean found = false;
            for (Product p : report.getProducts()) {
                if (p.belongsToCategory(catName) && !printed.contains(p.getProductID())) {
                    System.out.println("  - " + p.getProductName()
                            + " | Qty: " + p.getInventory().getTotalQuantity()
                            + " | Location: " + p.getInventory().getLocation());
                    printed.add(p.getProductID());
                    found = true;
                }
            }
            if (!found) System.out.println("  No products found.");
        }
    }

    // Print all the products that below the threshold
    public void printAlerts(List<Product> lowStockProducts) {
        printHeader("LOW STOCK ALERTS");
        if (lowStockProducts.isEmpty()) {
            System.out.println("No alerts — all products are sufficiently stocked.");
            return;
        }
        for (Product p : lowStockProducts) {
            System.out.println("[ALERT] " + p.getProductName()
                    + " | Current: " + p.getInventory().getTotalQuantity()
                    + " | Minimum: " + p.getMinimumThreshold());
        }
    }

    public void printSuccess(String msg) {
        System.out.println("[OK] " + msg);
    }

    public void printError(String msg) {
        System.out.println("[ERROR] " + msg);
    }

}
