package Inventory.domain;

import Inventory.service.InventoryService;

public class Datainit {
    private InventoryService service;

    public Datainit(InventoryService service) {
        this.service = service;
    }

    public void initializeData() {
        initializeCategories();
        initializeProducts();
        initializeDefectiveItems();
    }

    private void initializeCategories() {

        Category dairy = new Category("Dairy", 0);
        Category milk = new Category("Milk", 1);
        Category milk3 = new Category("3%", 2);
        Category cheese = new Category("Cheese", 1);
        Category cheddar = new Category("Cheddar", 2);
        Category cream = new Category("Cream", 1);

        Category toiletries = new Category("Toiletries", 0);
        Category shampoo = new Category("Shampoo", 1);
        Category shampoo250 = new Category("250ml", 2);
        Category soap = new Category("Soap", 1);
        Category bodySoap = new Category("Body Soap", 2);


        Category beverages = new Category("Beverages", 0);
        Category juice = new Category("Juice", 1);
        Category orange = new Category("Orange Juice", 2);
        Category coffee = new Category("Coffee", 1);


        Category snacks = new Category("Snacks", 0);
        Category chips = new Category("Chips", 1);
        Category popcorn = new Category("Popcorn", 2);
        Category nuts = new Category("Nuts", 1);


        Category bread = new Category("Bread", 0);
        Category whiteB = new Category("White Bread", 1);
        Category brownB = new Category("Brown Bread", 2);

        // Add all categories
        service.addCategory(dairy);
        service.addCategory(milk);
        service.addCategory(milk3);
        service.addCategory(cheese);
        service.addCategory(cheddar);
        service.addCategory(cream);
        service.addCategory(toiletries);
        service.addCategory(shampoo);
        service.addCategory(shampoo250);
        service.addCategory(soap);
        service.addCategory(bodySoap);
        service.addCategory(beverages);
        service.addCategory(juice);
        service.addCategory(orange);
        service.addCategory(coffee);
        service.addCategory(snacks);
        service.addCategory(chips);
        service.addCategory(popcorn);
        service.addCategory(nuts);
        service.addCategory(bread);
        service.addCategory(whiteB);
        service.addCategory(brownB);
    }


    private void initializeProducts() {
        // Get categories for products
        Category dairy = new Category("Dairy", 0);
        Category milk = new Category("Milk", 1);
        Category milk3 = new Category("3%", 2);
        Category cheese = new Category("Cheese", 1);
        Category cheddar = new Category("Cheddar", 2);
        Category cream = new Category("Cream", 1);

        Category toiletries = new Category("Toiletries", 0);
        Category shampoo = new Category("Shampoo", 1);
        Category shampoo250 = new Category("250ml", 2);
        Category soap = new Category("Soap", 1);
        Category bodySoap = new Category("Body Soap", 2);

        Category beverages = new Category("Beverages", 0);
        Category juice = new Category("Juice", 1);
        Category orange = new Category("Orange Juice", 2);
        Category coffee = new Category("Coffee", 1);

        Category snacks = new Category("Snacks", 0);
        Category chips = new Category("Chips", 1);
        Category popcorn = new Category("Popcorn", 2);
        Category nuts = new Category("Nuts", 1);

        Category bread = new Category("Bread", 0);
        Category whiteB = new Category("White Bread", 1);
        Category brownB = new Category("Brown Bread", 2);

        // Product 1: Tnuva 3% Milk
        InventoryLevel inv1 = new InventoryLevel(3, 2, 10, "Aisle 1");
        Product p1 = new Product(1, "Tnuva 3% Milk", 101, 4.5, 6.9,
                "CAT-001", dairy, milk, milk3, inv1);
        service.addProduct(p1);

        // Product 2: Pinuk Shampoo 250ml
        InventoryLevel inv2 = new InventoryLevel(10, 10, 5, "Aisle 3");
        Product p2 = new Product(2, "Pinuk Shampoo 250ml", 202, 8.0, 12.9,
                "CAT-002", toiletries, shampoo, shampoo250, inv2);
        service.addProduct(p2);

        // Product 3: Tnuva Cream Cheese 500ml
        InventoryLevel inv3 = new InventoryLevel(10, 10, 3, "Aisle 1");
        Product p3 = new Product(3, "Tnuva Cream Cheese 500ml", 101, 20.5, 30.9,
                "CAT-003", dairy, cream, cream, inv3);
        service.addProduct(p3);

        // Product 4: Tara Cheddar Cheese
        InventoryLevel inv4 = new InventoryLevel(8, 5, 4, "Aisle 2");
        Product p4 = new Product(4, "Tara Cheddar Cheese 200g", 102, 18.0, 28.5,
                "CAT-004", dairy, cheese, cheddar, inv4);
        service.addProduct(p4);

        // Product 5: Strauss Orange Juice 1L
        InventoryLevel inv5 = new InventoryLevel(15, 20, 8, "Aisle 4");
        Product p5 = new Product(5, "Strauss Orange Juice 1L", 103, 5.5, 8.9,
                "CAT-005", beverages, juice, orange, inv5);
        service.addProduct(p5);

        // Product 6: Nescafé Instant Coffee 200g
        InventoryLevel inv6 = new InventoryLevel(12, 8, 6, "Aisle 5");
        Product p6 = new Product(6, "Nescafé Instant Coffee 200g", 104, 35.0, 52.9,
                "CAT-006", beverages, coffee, coffee, inv6);
        service.addProduct(p6);

        // Product 7: Bissli Cheese Flavored Snacks
        InventoryLevel inv7 = new InventoryLevel(25, 15, 12, "Aisle 6");
        Product p7 = new Product(7, "Bissli Cheese Flavored 100g", 105, 3.5, 5.9,
                "CAT-007", snacks, chips, chips, inv7);
        service.addProduct(p7);

        // Product 8: Nestlé Popcorn
        InventoryLevel inv8 = new InventoryLevel(18, 12, 8, "Aisle 6");
        Product p8 = new Product(8, "Nestlé Popcorn 150g", 106, 6.0, 9.9,
                "CAT-008", snacks, popcorn, popcorn, inv8);
        service.addProduct(p8);

        // Product 9: Almond Nuts 500g
        InventoryLevel inv9 = new InventoryLevel(7, 8, 4, "Aisle 7");
        Product p9 = new Product(9, "Almond Nuts 500g", 107, 25.0, 39.9,
                "CAT-009", snacks, nuts, nuts, inv9);
        service.addProduct(p9);

        // Product 10: White Bread Pita
        InventoryLevel inv10 = new InventoryLevel(20, 10, 8, "Aisle 8");
        Product p10 = new Product(10, "White Bread Pita Pack", 108, 4.0, 6.5,
                "CAT-010", bread, whiteB, whiteB, inv10);
        service.addProduct(p10);

        // Product 11: Brown Bread Whole Wheat
        InventoryLevel inv11 = new InventoryLevel(15, 12, 7, "Aisle 8");
        Product p11 = new Product(11, "Brown Bread Whole Wheat", 109, 5.0, 7.9,
                "CAT-011", bread, brownB, brownB, inv11);
        service.addProduct(p11);

        // Product 12: Palmolive Liquid Soap
        InventoryLevel inv12 = new InventoryLevel(14, 16, 8, "Aisle 3");
        Product p12 = new Product(12, "Palmolive Liquid Soap 500ml", 110, 7.5, 11.9,
                "CAT-012", toiletries, soap, bodySoap, inv12);
        service.addProduct(p12);

        // Product 13: Danone Yogurt 200g
        InventoryLevel inv13 = new InventoryLevel(20, 25, 15, "Aisle 2");
        Product p13 = new Product(13, "Danone Yogurt 200g", 111, 3.0, 4.9,
                "CAT-013", dairy, milk, milk3, inv13);
        service.addProduct(p13);

        // Product 14: Sabra Hummus 250g
        InventoryLevel inv14 = new InventoryLevel(8, 6, 5, "Aisle 2");
        Product p14 = new Product(14, "Sabra Hummus 250g", 112, 8.0, 12.9,
                "CAT-014", dairy, cream, cream, inv14);
        service.addProduct(p14);

        // Product 15: Coca-Cola 1.5L
        InventoryLevel inv15 = new InventoryLevel(30, 40, 20, "Aisle 4");
        Product p15 = new Product(15, "Coca-Cola 1.5L", 113, 5.0, 7.9,
                "CAT-015", beverages, juice, orange, inv15);
        service.addProduct(p15);

        // Product 16: Lay's Classic Chips 150g
        InventoryLevel inv16 = new InventoryLevel(22, 18, 10, "Aisle 6");
        Product p16 = new Product(16, "Lay's Classic Chips 150g", 114, 4.5, 7.5,
                "CAT-016", snacks, chips, chips, inv16);
        service.addProduct(p16);

        // Product 17: Cashew Nuts 400g
        InventoryLevel inv17 = new InventoryLevel(5, 7, 3, "Aisle 7");
        Product p17 = new Product(17, "Cashew Nuts 400g", 115, 30.0, 45.9,
                "CAT-017", snacks, nuts, nuts, inv17);
        service.addProduct(p17);

        // Product 18: Bamba Corn Snack 125g
        InventoryLevel inv18 = new InventoryLevel(28, 22, 15, "Aisle 6");
        Product p18 = new Product(18, "Bamba Corn Snack 125g", 116, 2.5, 3.99,
                "CAT-018", snacks, chips, chips, inv18);
        service.addProduct(p18);
    }


    private void initializeDefectiveItems() {
        // Report some defective items for testing
        service.reportDefectiveItem(1, 2, "Expired");
        service.reportDefectiveItem(5, 1, "Damaged packaging");
        service.reportDefectiveItem(10, 1, "Moldy");
    }
}