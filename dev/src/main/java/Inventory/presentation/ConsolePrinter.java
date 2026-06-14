package Inventory.presentation;
import Inventory.dto.SupplierOrderDTO;

import Inventory.domain.*;
import Inventory.dto.SupplierOrderDTO;

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
        System.out.println("5. Apply Discount");
        System.out.println("6. Report Defective Product");
        System.out.println("7. Add New Category");
        System.out.println("8. Add New Product");
        System.out.println("9. Delete Product");
        System.out.println("10. Create Manual Supplier Order");
        System.out.println("11. Receive Shipment");
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

    // Discount menu
    public void printDiscountMenu() {
        printHeader("APPLY DISCOUNT");
        System.out.println("1. Apply Discount to Product by ID");
        System.out.println("2. Apply Discount to Category");
        System.out.println("3. Apply Discount to Supplier");
        System.out.println("0. Back");
        System.out.print("Choose an option: ");
    }


    // Print product
    public void printProduct(Product p) {
        printHeader("PRODUCT DETAILS");
        System.out.println("ID:            " + p.getProductID());
        System.out.println("Name:          " + p.getProductName());
        System.out.println("Supplier:  " + p.getSupplierID());
        System.out.println("Categories:    Main: " + p.getMainCategory().getName()
                + " | Sub: " + p.getSubCategory().getName()
                + " | Sub-Sub: " + p.getSubSubCategory().getName());
        System.out.println("Total Qty:     " + p.getInventory().getTotalQuantity());
        System.out.println("Shelf Qty:     " + p.getInventory().getShelfQuantity());
        System.out.println("Warehouse Qty: " + p.getInventory().getWarehouseQuantity());
        System.out.println("Location:      " + p.getInventory().getLocation());
        System.out.println("Cost Price:    " + p.getCostPrice());
        System.out.println("Sell Price:    " + p.getSellingPrice());
        System.out.println("Min Threshold: " + p.getMinimumThreshold());
    }

    // Print products below threshold
    public void printOrderReport(List<SupplierOrderDTO> orders) {
        printHeader("ORDER REPORT");
        if (orders.isEmpty()) {
            System.out.println("No orders found.");
            return;
        }
        for (SupplierOrderDTO o : orders) {
            System.out.println("  Order #"     + o.orderId()
                    + " | Product: "           + o.productName()
                    + " (ID: "                 + o.productId() + ")"
                    + " | Supplier ID: "       + o.supplierId()
                    + " | Catalog: "           + o.supplierCatalogId()
                    + " | Qty: "               + o.quantity()
                    + " | Status: "            + o.status()
                    + " | Created: "           + o.createdAt());
        }
    }
    // Print all products by category
    public void printCategoryReport(CategoryReport report) {
        printHeader("INVENTORY REPORT");
        if (report.getProducts().isEmpty()) {
            System.out.println("No products found in these categories.");
            return;
        }

        // Track printed products to avoid duplicates
        java.util.Set<Integer> printedProductIds = new java.util.HashSet<>();

        for (Product p : report.getProducts()) {
            if (!printedProductIds.contains(p.getProductID())) {
                System.out.println("- " + p.getProductName()
                        + " | Qty: " + p.getInventory().getTotalQuantity()
                        + " | Location: " + p.getInventory().getLocation());
                printedProductIds.add(p.getProductID());
            }
        }
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

    // Prompts for prints
    public void promptForProductId() {
        System.out.print("Enter Product ID: ");
    }
    public void promptForQuantity(String type) {
        System.out.print("Enter new " + type + " Quantity: ");
    }
    public void promptForPromoName() {
        System.out.print("Enter promotion name: ");
    }
    public void promptForSupplierID() {
        System.out.print("Enter supplier ID: ");
    }
    public void promptForDiscount() {
        System.out.print("Enter discount percentage (0-100): ");
    }
    public void promptForCategoryName() {
        System.out.print("Enter Category Name: ");
    }
    public void promptForDate(String label) {
        System.out.print("Enter " + label + " date (e.g. 15.04.2026): ");
    }
    public void promptForCategoryList() {
        System.out.print("Enter category names (comma separated): ");
    }
    public void promptForReason() {
        System.out.print("Enter reason for defect: ");
    }
    public void printExitMessage() {
        System.out.println("Exiting Super-Li System. Goodbye!");
    }
    public void printSuccess(String msg) {
        System.out.println("[OK] " + msg);
    }
    public void printError(String msg) {
        System.out.println("[ERROR] " + msg);
    }
    public void promptForOrderQuantity() {
        System.out.print("Enter quantity to order: ");
    }
    public void printSupplierOrderDTO(SupplierOrderDTO order) {
        System.out.println("Order ID:            " + order.orderId());
        System.out.println("Product ID:          " + order.productId());
        System.out.println("Product Name:        " + order.productName());
        System.out.println("Supplier ID:         " + order.supplierId());
        System.out.println("Supplier Catalog ID: " + order.supplierCatalogId());
        System.out.println("Quantity:            " + order.quantity());
        System.out.println("Status:              " + order.status());
        System.out.println("Created At:          " + order.createdAt());
    }

    public void printSupplierOrdersDTO(List<SupplierOrderDTO> orders) {
        printHeader("SUPPLIER ORDERS");

        if (orders == null || orders.isEmpty()) {
            System.out.println("No supplier orders found.");
            return;
        }

        for (SupplierOrderDTO order : orders) {
            printSupplierOrderDTO(order);
            System.out.println("----------------------------------------");
        }
    }

    public void printActiveOrders(List<SupplierOrderDTO> orders) {
        printHeader("ACTIVE ORDERS");
        if (orders.isEmpty()) {
            System.out.println("No active orders found.");
            return;
        }
        for (SupplierOrderDTO o : orders) {
            System.out.println("  Order #"   + o.orderId()
                    + " | Product: "         + o.productName()
                    + " (ID: "               + o.productId() + ")"
                    + " | Supplier ID: "     + o.supplierId()
                    + " | Qty: "             + o.quantity()
                    + " | Status: "          + o.status()
                    + " | Created: "         + o.createdAt());
        }
    }
}