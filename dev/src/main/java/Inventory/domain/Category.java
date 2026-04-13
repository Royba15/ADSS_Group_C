package Inventory.domain;

import java.util.ArrayList;

public class Category {
    private String categoryName;
    private int level; //level 0 = root, level 1 = sub-category
    private ArrayList<Product> products = new ArrayList<>();
    private ArrayList<Category> subCategories = new ArrayList<>(); //allows categories to have nested sub-categories

    // Constructor
    public Category(String categoryName, int level) {
        this.categoryName = categoryName;
        this.level = level;
    }

    // Getters
    public String getCategoryName() {
        return categoryName;
    }

    public int getLevel() {
        return level;
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public ArrayList<Category> getSubCategories() {
        return subCategories;
    }

    // Setters
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    // Add a sub-category
    public void addSubCategory(Category sub) {
        if (sub != null && sub.getLevel() == this.level + 1) {
            subCategories.add(sub);
        }
    }

    // Add a product to this category
    public void addProduct(Product product) {
        if (product != null && !products.contains(product)) {
            products.add(product);
        }
    }

    // Remove a product from this category
    public void removeProduct(Product product) {
        if (product != null) {
            products.remove(product);
        }
    }

    // Get all products in this category and sub-categories (recursive)
    public ArrayList<Product> getProductsInCategory() {
        ArrayList<Product> allProducts = new ArrayList<>(products);

        // Recursively add products from all sub-categories
        for (Category subCategory : subCategories) {
            allProducts.addAll(subCategory.getProductsInCategory());
        }

        return allProducts;
    }

    // Get only products directly in this category (not in sub-categories)
    public ArrayList<Product> getDirectProducts() {
        return new ArrayList<>(products);
    }

    // Check if this category has sub-categories
    public boolean hasSubCategories() {
        return !subCategories.isEmpty();
    }

    // Get category hierarchy as string for display
    @Override
    public String toString() {
        return categoryName + " (Level: " + level + ", Products: " + products.size() +
                ", SubCategories: " + subCategories.size() + ")";
    }
}

