package Inventory.presentation;

import Inventory.service.InventoryService;
import Inventory.domain.Product;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class InventoryMenu {
    private final InventoryService service;
    private final ConsolePrinter printer;
    private final Scanner scanner;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public InventoryMenu() {
        this.service = new InventoryService();
        this.printer = new ConsolePrinter();
        this.scanner = new Scanner(System.in);
    }

    public void initializeData() {
        service.initializeData();
    }

    public static void main(String[] args) {
        InventoryMenu menu = new InventoryMenu();
        menu.promptForDatabaseInitialization();
        menu.run();
    }

    private void promptForDatabaseInitialization() {
        printer.printHeader("DATABASE INITIALIZATION");
        System.out.println("1. Load Existing Database");
        System.out.println("2. Start with Empty Database");
        System.out.print("Choose an option: ");
        try {
            int choice = Integer.parseInt(scanner.nextLine());
            if (choice == 1) {
                initializeData();
                printer.printSuccess("Existing database loaded!");
            } else if (choice == 2) {
                printer.printSuccess("Starting with empty database!");
            } else {
                printer.printError("Invalid choice. Loading existing database...");
                initializeData();
            }
        } catch (Exception e) {
            printer.printError("Error reading input. Loading existing database...");
            initializeData();
        }
    }

    public void run() {
        int choice = -1;
        while (choice != 0) {
            printer.printMainMenu();
            try {
                choice = Integer.parseInt(scanner.nextLine());
                handleMainChoice(choice);
            } catch (Exception e) {
                printer.printError("Invalid input. Please enter a number.");
            }
        }
        printer.printExitMessage();
    }

    private void handleMainChoice(int choice) {
        switch (choice) {
            case 1:
                updateInventoryFlow();
                break;
            case 2:
                viewProductFlow();
                break;
            case 3:
                printer.printAlerts(service.getLowStockProducts());
                break;
            case 4:
                handleReportsMenu();
                break;
            case 5:
                handleDiscountMenu();
                break;
            case 6:
                reportDefectiveFlow();
                break;
            case 7:
                addNewCategoryFlow();
                break;
            case 8:
                addNewProductFlow();
                break;
            case 9:
                deleteProductFlow();
                break;
            case 0: {}
            break;
            default:
                printer.printError("Option not found.");
                break;
        }
    }

    // check the number from employer and call to update function from service
    private void updateInventoryFlow() {
        try {
            printer.promptForProductId();
            int id = Integer.parseInt(scanner.nextLine());
            printer.promptForQuantity("shelf");
            int shelf = Integer.parseInt(scanner.nextLine());
            printer.promptForQuantity("warehouse");
            int warehouse = Integer.parseInt(scanner.nextLine());

            if (service.updateInventory(id, shelf, warehouse)) {
                printer.printSuccess("Inventory updated successfully.");
            } else {
                printer.printError("Product ID not found.");
            }
        } catch (Exception e) {
            printer.printError("Update failed: " + e.getMessage());
        }
    }

    // display product by id
    private void viewProductFlow() {
        printer.promptForProductId();
        try {
            int id = Integer.parseInt(scanner.nextLine());
            Product p = service.getProductByID(id);
            if (p != null) {
                printer.printProduct(p);
            } else {
                printer.printError("Product not found.");
            }
        } catch (Exception e) {
            printer.printError("Invalid ID format.");
        }
    }
    // display report menu
    private void handleReportsMenu() {
        printer.printReportsMenu();
        try {
            int reportChoice = Integer.parseInt(scanner.nextLine());
            switch (reportChoice) {
                case 1:
                    printer.printDefectiveReport(service.generateDefectiveReport());
                    break;
                case 2:
                    categoryReportFlow();
                    break;
                case 3:
                    printer.printOrderReport(service.generateOrderReport());
                    break;
                case 0: break;
                default:
                    printer.printError("Invalid report option.");
                    break;
            }
        } catch (Exception e) {
            printer.printError("Input error.");
        }
    }
    // display discount menu
    private void handleDiscountMenu() {
        printer.printDiscountMenu();
        try {
            int choice = Integer.parseInt(scanner.nextLine());
            switch (choice) {
                case 1:
                    applyDiscountToProductFlow();
                    break;
                case 2:
                    applyDiscountToCategoryFlow();
                    break;
                case 3:
                    applyDiscountToSupplierFlow();
                    break;
                case 0: break;
                default:
                    printer.printError("Invalid option.");
                    break;
            }
        } catch (Exception e) {
            printer.printError("Input error.");
        }
    }
    // create discount by id product
    private void applyDiscountToProductFlow() {
        try {
            printer.promptForProductId();
            int id = Integer.parseInt(scanner.nextLine());
            printer.promptForPromoName();
            String promoName = scanner.nextLine();
            printer.promptForDiscount();
            double discount = Double.parseDouble(scanner.nextLine());
            printer.promptForDate("start");
            LocalDate startDate = LocalDate.parse(scanner.nextLine(), DATE_FORMAT);
            LocalDateTime start = startDate.atStartOfDay();
            printer.promptForDate("end");
            LocalDate endDate = LocalDate.parse(scanner.nextLine(), DATE_FORMAT);
            LocalDateTime end = endDate.atTime(23, 59);

            if (service.applyDiscountToProduct(id, promoName, discount, start, end)) {
                printer.printSuccess("Discount applied!");
            } else {
                printer.printError("Product ID not found.");
            }
        } catch (DateTimeParseException e) {
            printer.printError("Invalid format! Use dd.MM.yyyy (e.g., 31.12.2000).");
        } catch (Exception e) {
            printer.printError("Error: " + e.getMessage());
        }
    }

    // create discount by category
    private void applyDiscountToCategoryFlow () {
        try {
            printer.promptForCategoryName();
            String catName = scanner.nextLine();
            printer.promptForPromoName();
            String promoName = scanner.nextLine();
            printer.promptForDiscount();
            double discount = Double.parseDouble(scanner.nextLine());
            printer.promptForDate("start");
            LocalDate startDate = LocalDate.parse(scanner.nextLine(), DATE_FORMAT);
            LocalDateTime start = startDate.atStartOfDay();
            printer.promptForDate("end");
            LocalDate endDate = LocalDate.parse(scanner.nextLine(), DATE_FORMAT);
            LocalDateTime end = endDate.atTime(23, 59);

            if (service.applyDiscountToCategory(catName, promoName, discount, start, end)) {
                printer.printSuccess("Discount applied!");
            } else {
                printer.printError("Category not found.");
            }
        } catch (DateTimeParseException e) {
            printer.printError("Invalid format! Use dd.MM.yyyy (e.g., 31.12.2000).");
        } catch (Exception e) {
            printer.printError("Error: " + e.getMessage());
        }
    }

    // Apply discount to all products of a supplier (fixes Req 6 feedback)
    private void applyDiscountToSupplierFlow() {
        try {
            printer.promptForSupplierID();
            int supplierID = Integer.parseInt(scanner.nextLine().trim());

            printer.promptForPromoName();
            String promoName = scanner.nextLine().trim();
            if (promoName.isEmpty()) {
                printer.printError("Promotion name cannot be empty.");
                return;
            }

            printer.promptForDiscount();
            double discount = Double.parseDouble(scanner.nextLine().trim());

            printer.promptForDate("start");
            java.time.LocalDate startDate = java.time.LocalDate.parse(scanner.nextLine().trim(), DATE_FORMAT);
            java.time.LocalDateTime start = startDate.atStartOfDay();
            printer.promptForDate("end");
            java.time.LocalDate endDate = java.time.LocalDate.parse(scanner.nextLine().trim(), DATE_FORMAT);
            java.time.LocalDateTime end = endDate.atTime(23, 59);

            if (!end.isAfter(start)) {
                printer.printError("End date must be after start date.");
                return;
            }

            if (service.applyDiscountToSupplier(supplierID, promoName, discount, start, end)) {
                printer.printSuccess("Discount applied to all products from supplier " + supplierID);
            } else {
                printer.printError("No products found for supplier ID " + supplierID);
            }
        } catch (DateTimeParseException e) {
            printer.printError("Invalid format! Use dd.MM.yyyy (e.g., 17.05.2026).");
        } catch (Exception e) {
            printer.printError("Error: " + e.getMessage());
        }
    }

    // create category report
    private void categoryReportFlow() {
        printer.promptForCategoryList();
        String input = scanner.nextLine();
        List<String> categoryNames = Arrays.asList(input.split("\\s*,\\s*"));
        printer.printCategoryReport(service.generateCategoryReport(categoryNames));
    }

    // create defective report
    private void reportDefectiveFlow() {
        try {
            printer.promptForProductId();
            int productID = Integer.parseInt(scanner.nextLine());

            Product product = service.getProductByID(productID);
            if (product == null) {
                printer.printError("Product not found.");
                return;
            }

            printer.promptForQuantity("quantity of defective items");
            int quantity = Integer.parseInt(scanner.nextLine());

            printer.promptForReason();
            String reason = scanner.nextLine().trim();

            if (reason.isEmpty()) {
                printer.printError("Reason cannot be empty.");
                return;
            }

            if (quantity <= 0) {
                printer.printError("Quantity must be greater than 0.");
                return;
            }

            // Get total defective items already reported for this product
            int totalDefectiveReported = service.getTotalDefectiveCountForProduct(productID);
            int totalInventory = product.getInventory().getTotalQuantity();

            // Validate that total defective (already reported + new report) doesn't exceed inventory
            if (totalDefectiveReported + quantity > totalInventory) {
                printer.printError("Cannot report " + quantity + " defective items. "
                        + "Total defective would be " + (totalDefectiveReported + quantity)
                        + " but only " + totalInventory + " total items available. "
                        + "Already reported defective: " + totalDefectiveReported);
                return;
            }

            // Report the defective item
            service.reportDefectiveItem(productID, quantity, reason);
            printer.printSuccess("Defective item reported successfully. "
                    + quantity + " units of " + product.getProductName() + " marked as defective.");

        } catch (NumberFormatException e) {
            printer.printError("Invalid input. Please enter valid numbers.");
        } catch (Exception e) {
            printer.printError("Error reporting defective item: " + e.getMessage());
        }
    }

    // Add new category
    private void addNewCategoryFlow() {
        try {
            printer.promptForCategoryName();
            String categoryName = scanner.nextLine().trim();

            if (categoryName.isEmpty()) {
                printer.printError("Category name cannot be empty.");
                return;
            }

            System.out.print("Enter category level (0=Main, 1=Sub, 2=Sub-Sub): ");
            int level = Integer.parseInt(scanner.nextLine());

            if (level < 0 || level > 2) {
                printer.printError("Invalid level. Must be 0, 1, or 2.");
                return;
            }

            // Use service to add category - no Domain Object creation in Menu
            if (service.addNewCategory(categoryName, level)) {
                printer.printSuccess("Category '" + categoryName + "' added successfully!");
            } else {
                printer.printError("Failed to add category. It may already exist.");
            }
        } catch (NumberFormatException e) {
            printer.printError("Invalid input. Please enter valid numbers.");
        } catch (Exception e) {
            printer.printError("Error adding category: " + e.getMessage());
        }
    }

    // Add new product
    private void addNewProductFlow() {
        try {
            printer.promptForProductId();
            int productID = Integer.parseInt(scanner.nextLine());

            System.out.print("Enter product name: ");
            String productName = scanner.nextLine().trim();
            if (productName.isEmpty()) {
                printer.printError("Product name cannot be empty.");
                return;
            }

            System.out.print("Enter manufacturer ID: ");
            int manufacturerID = Integer.parseInt(scanner.nextLine());

            System.out.print("Enter cost price: ");
            double costPrice = Double.parseDouble(scanner.nextLine());

            System.out.print("Enter selling price: ");
            double sellingPrice = Double.parseDouble(scanner.nextLine());

            System.out.print("Enter supplier catalog ID: ");
            String catalogID = scanner.nextLine().trim();

            // Display available categories by level
            System.out.println("\n--- Available Categories ---");

            System.out.println("\nMain Categories:");
            List<Inventory.domain.Category> mainCategories = service.getCategoriesByLevel(0);
            if (mainCategories.isEmpty()) {
                printer.printError("No main categories available. Please create a main category first (Option 7).");
                return;
            }
            for (Inventory.domain.Category c : mainCategories) {
                System.out.println("  - " + c.getName());
            }

            System.out.print("\nEnter main category name: ");
            String mainCatName = scanner.nextLine().trim();

            if (!service.categoryExists(mainCatName)) {
                printer.printError("Main category '" + mainCatName + "' does not exist.");
                return;
            }

            System.out.println("\nSub Categories:");
            List<Inventory.domain.Category> subCategories = service.getCategoriesByLevel(1);
            if (subCategories.isEmpty()) {
                printer.printError("No sub categories available. Please create sub categories first (Option 7).");
                return;
            }
            for (Inventory.domain.Category c : subCategories) {
                System.out.println("  - " + c.getName());
            }

            System.out.print("Enter sub category name: ");
            String subCatName = scanner.nextLine().trim();

            if (!service.categoryExists(subCatName)) {
                printer.printError("Sub category '" + subCatName + "' does not exist.");
                return;
            }

            System.out.println("\nSub-Sub Categories:");
            List<Inventory.domain.Category> subSubCategories = service.getCategoriesByLevel(2);
            if (subSubCategories.isEmpty()) {
                printer.printError("No sub-sub categories available. Please create sub-sub categories first (Option 7).");
                return;
            }
            for (Inventory.domain.Category c : subSubCategories) {
                System.out.println("  - " + c.getName());
            }

            System.out.print("Enter sub-sub category name: ");
            String subSubCatName = scanner.nextLine().trim();

            if (!service.categoryExists(subSubCatName)) {
                printer.printError("Sub-sub category '" + subSubCatName + "' does not exist.");
                return;
            }

            printer.promptForQuantity("shelf");
            int shelfQty = Integer.parseInt(scanner.nextLine());

            printer.promptForQuantity("warehouse");
            int warehouseQty = Integer.parseInt(scanner.nextLine());

            System.out.print("Enter minimum quantity threshold: ");
            int minThreshold = Integer.parseInt(scanner.nextLine());

            System.out.print("Enter location (e.g., Aisle 1): ");
            String location = scanner.nextLine().trim();

            // Use service to add product - categories must exist
            if (service.addNewProduct(productID, productName, manufacturerID, costPrice, sellingPrice,
                    catalogID, mainCatName, subCatName, subSubCatName,
                    shelfQty, warehouseQty, minThreshold, location)) {
                printer.printSuccess("Product '" + productName + "' added successfully!");
            } else {
                printer.printError("Failed to add product. Check that all categories exist or product ID is already in use.");
            }
        } catch (NumberFormatException e) {
            printer.printError("Invalid input. Please enter valid numbers.");
        } catch (Exception e) {
            printer.printError("Error adding product: " + e.getMessage());
        }
    }

    // Delete product by ID (fixes "Missing Product Delete" feedback)
    private void deleteProductFlow() {
        try {
            printer.printHeader("DELETE PRODUCT");
            printer.promptForProductId();
            int id = Integer.parseInt(scanner.nextLine().trim());

            Product product = service.getProductByID(id);
            if (product == null) {
                printer.printError("Product not found.");
                return;
            }

            printer.printProduct(product);
            System.out.print("Are you sure you want to delete this product? (yes/no): ");
            String confirm = scanner.nextLine().trim().toLowerCase();
            if (!confirm.equals("yes")) {
                printer.printSuccess("Deletion cancelled.");
                return;
            }

            if (service.deleteProduct(id)) {
                printer.printSuccess("Product deleted successfully.");
            } else {
                printer.printError("Failed to delete product.");
            }
        } catch (NumberFormatException e) {
            printer.printError("Invalid ID format.");
        } catch (Exception e) {
            printer.printError("Error deleting product: " + e.getMessage());
        }
    }


}