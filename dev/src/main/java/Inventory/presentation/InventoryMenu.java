package Inventory.presentation;


import Inventory.domain.Category;
import Inventory.domain.InventoryLevel;
import Inventory.service.InventoryService;
import Inventory.domain.Product;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class InventoryMenu {
    private final InventoryService service;
    private final ConsolePrinter printer;
    private final Scanner scanner;

    public InventoryMenu() {
        this.service = new InventoryService();
        this.printer = new ConsolePrinter();
        this.scanner = new Scanner(System.in);
    }

    public static void main(String[] args) {
        InventoryMenu menu = new InventoryMenu();
        menu.initializeData(); // חובה לפי דרישות המטלה
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
        System.out.println("Exiting Super-Li System. Goodbye!");
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
            System.out.print("Enter Product ID: ");
            int id = Integer.parseInt(scanner.nextLine());
            System.out.print("Enter new Shelf Quantity: ");
            int shelf = Integer.parseInt(scanner.nextLine());
            System.out.print("Enter new Warehouse Quantity: ");
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
        System.out.print("Enter Product ID: ");
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

    private void categoryReportFlow() {
        System.out.print("Enter category names (comma separated): ");
        String input = scanner.nextLine();
        List<String> categoryNames = Arrays.asList(input.split("\\s*,\\s*"));
        printer.printCategoryReport(service.generateCategoryReport(categoryNames));
    }
    // function init all the data
    public void initializeData() {
        // Categories
        Category dairy    = new Category("Dairy", 0);
        Category milk     = new Category("Milk", 1);
        Category milk3    = new Category("3%", 2);

        Category cheese     = new Category("cheese", 1);
        Category cheese2    = new Category("cheese2", 2);

        Category toiletries = new Category("Toiletries", 0);
        Category shampoo    = new Category("Shampoo", 1);
        Category shampoo250 = new Category("250ml", 2);

        service.addCategory(dairy);
        service.addCategory(milk);
        service.addCategory(milk3);
        service.addCategory(toiletries);
        service.addCategory(shampoo);
        service.addCategory(shampoo250);

        // Products
        InventoryLevel inv1 = new InventoryLevel( 3, 2, 10, "Aisle 1");
        Product p1 = new Product(1, "Tnuva 3% Milk", 101, 4.5, 6.9,
                "CAT-001", dairy, milk, milk3, inv1);
        service.addProduct(p1);

        InventoryLevel inv2 = new InventoryLevel( 10, 10, 5, "Aisle 3");
        Product p2 = new Product(2, "Pinuk Shampoo 250ml", 202, 8.0, 12.9,
                "CAT-002", toiletries, shampoo, shampoo250, inv2);
        service.addProduct(p2);

        InventoryLevel inv3 = new InventoryLevel( 10, 10, 3, "Aisle 1");
        Product p3 = new Product(3, "Tnuva cream cheese 500ml", 101, 20.5, 30.9,
                "CAT-003", dairy, cheese, cheese2, inv3);
        service.addProduct(p3);

        // Defective item
        service.reportDefectiveItem(1, 2, "Expired");
    }

}
