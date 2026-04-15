package Inventory.presentation;

import Inventory.domain.Category;
import Inventory.domain.Datainit;
import Inventory.domain.InventoryLevel;
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

    public static void main(String[] args) {
        InventoryMenu menu = new InventoryMenu();
        menu.initializeData();
        menu.run();
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
    //
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
                case 0: break;
                default:
                    printer.printError("Invalid option.");
                    break;
            }
        } catch (Exception e) {
            printer.printError("Input error.");
        }
    }

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


    private void categoryReportFlow() {
        printer.promptForCategoryList();
        String input = scanner.nextLine();
        List<String> categoryNames = Arrays.asList(input.split("\\s*,\\s*"));
        printer.printCategoryReport(service.generateCategoryReport(categoryNames));
    }

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


    // function init all the data
    public void initializeData() {
        Datainit initializer = new Datainit(service);
        initializer.initializeData();
    }

}
