package Inventory.domain;

public class Product {
    private int productID;
    private String name;
    private int manufacturerID;
    private double costPrice;
    private double sellingPrice;
    private String supplierCatalogID;
    private InventoryLevel inventory;               // הוספתי את זה כי לאל מוצר יש את  המלאי שלו (לכל אחד שלו)

    public Product(int id, String name, int manufacturerID, double cost, double selling, String catalogID, InventoryLevel inventory) {
        this.productID = id;
        this.name = name;
        this.manufacturerID = manufacturerID;
        this.costPrice = cost;
        this.sellingPrice = selling;
        this.supplierCatalogID = catalogID;
        this.inventory = inventory;
    }

    public boolean checkMinThreshold(){
        return true;
    }

    public void updatePrices(double cost, double selling){
        this.costPrice=cost;
        this.sellingPrice=selling;

    }

    public double calculateFinalPrice(){
        return 0.0;
    }

    public String getDetails() {
        return "ID: " + productID + ", Name: " + name + ", Price: " + sellingPrice +
                ", Location: " + inventory.getLocation() + ", Total Qty: " + inventory.getTotalQuantity();
    }

    public int getProductID() {         // new
        return productID;
    }
}
