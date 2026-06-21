import Inventory.presentation.InventoryMenu;

import java.util.Scanner;

public class Main {

    private static int readInt(Scanner scanner) {
        while (!scanner.hasNextInt()) {
            System.out.println("Invalid input. Please enter a number:");
            scanner.next();
        }

        return scanner.nextInt();
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("=================================");
            System.out.println("      Welcome to SuperLi");
            System.out.println("=================================");
            System.out.println("Choose a module:");
            System.out.println("1 - HR");
            System.out.println("2 - Inventory");
            System.out.println("3 - Exit");
            System.out.print("Please enter your choice: ");

            int choice = readInt(scanner);
            scanner.nextLine();

            if (choice == 1) {
                HR.presentation.Main.main(new String[0]);
            } else if (choice == 2) {
                InventoryMenu.main(new String[0]);
            } else if (choice == 3) {
                System.out.println("Goodbye!");
                break;
            } else {
                System.out.println("Invalid choice.");
            }
        }
    }
}
