package Inventory.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class DiscountPromotion {
    private int promotionID;
    private String promotionName;
    private double discountPercentage;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean isActive;

    // Constructor
    public DiscountPromotion(int promotionID, String promotionName, double discountPercentage,
                             LocalDateTime startDate, LocalDateTime endDate) {
        this.promotionID = promotionID;
        this.promotionName = promotionName;

        if (discountPercentage < 0 || discountPercentage > 100) {
            throw new IllegalArgumentException("Discount percentage must be between 0 and 100");
        }
        this.discountPercentage = discountPercentage;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isActive = true;
    }

    // Calculate the final price after discount
    public double calculateFinalPrice(double originalPrice) {
        if (!isActive || !isPromotionActive()) {
            return originalPrice;
        }
        double discountAmount = originalPrice * (discountPercentage / 100);
        return originalPrice - discountAmount;
    }

    // Apply discount to a single product (Product calls this when the promotion is assigned to it)
    public void applyDiscountToProduct(Product product) {
        if (product == null || !isActive || !isPromotionActive()) {
            return;
        }

        double originalPrice = product.getSellingPrice();
        double discountedPrice = calculateFinalPrice(originalPrice);
        product.setSellingPrice(discountedPrice);
    }

    // Apply discount to all products in a category
    public void applyDiscountToCategory(Category category) {
        if (category == null || !isActive || !isPromotionActive()) {
            return;
        }

        ArrayList<Product> productsInCategory = category.getProductsInCategory();
        for (Product product : productsInCategory) {
            applyDiscountToProduct(product);
        }
    }

    // Check if promotion is currently active based on date
    public boolean isPromotionActive() {
        LocalDateTime now = LocalDateTime.now();
        return isActive && now.isAfter(startDate) && now.isBefore(endDate);
    }

    // Deactivate the promotion
    public void deactivatePromotion() {
        this.isActive = false;
    }

    // Get promotion details
    public String getPromotionDetails() {
        StringBuilder details = new StringBuilder();
        details.append("Promotion ID: ").append(promotionID)
                .append(" | Name: ").append(promotionName)
                .append(" | Discount: ").append(String.format("%.1f", discountPercentage)).append("%")
                .append(" | Status: ").append(isActive ? "ctive" : "Inactive")
                .append(" | Valid: ").append(startDate)
                .append(" to ").append(endDate);
        return details.toString();
    }

    // Get a concise summary
    public String getSummary() {
        return promotionName + " - " + String.format("%.1f", discountPercentage) + "% off";
    }

    // Getters
    public int getPromotionID() {
        return promotionID;
    }

    public String getPromotionName() {
        return promotionName;
    }

    public double getDiscountPercentage() {
        return discountPercentage;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public boolean isActive() {
        return isActive;
    }

    // Setters
    public void setPromotionName(String promotionName) {
        if (promotionName != null && !promotionName.isEmpty()) {
            this.promotionName = promotionName;
        }
    }

    public void setDiscountPercentage(double discountPercentage) {
        if (discountPercentage >= 0 && discountPercentage <= 100) {
            this.discountPercentage = discountPercentage;
        }
    }

    public void setStartDate(LocalDateTime startDate) {
        if (startDate != null) {
            this.startDate = startDate;
        }
    }

    public void setEndDate(LocalDateTime endDate) {
        if (endDate != null) {
            this.endDate = endDate;
        }
    }

    @Override
    public String toString() {
        return getPromotionDetails();
    }
}