package Inventory.domain;

import java.time.LocalDateTime;

public class DiscountPromotion {
    private final int promotionID;
    private final String promotionName;
    private final double discountPercentage;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
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

    // Check if promotion is currently active based on date
    public boolean isPromotionActive() {
        LocalDateTime now = LocalDateTime.now();
        return isActive && now.isAfter(startDate) && now.isBefore(endDate);
    }

    // Deactivate the promotion (not in use currently)
    public void deactivatePromotion() {
        this.isActive = false;
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

    // Get a concise summary
    public String getSummary() {
        return promotionName + " - " + String.format("%.1f", discountPercentage) + "% off";
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

    @Override
    public String toString() {
        return getPromotionDetails();
    }
}