package Inventory.domain;
import java.time.LocalDate;

public class DiscountPromotion {
    private int discountID;
    private double percentage;
    private LocalDate startDate;
    private LocalDate endDate;

    public DiscountPromotion(int discountID, double percentage, LocalDate startDate, LocalDate endDate) {
        this.discountID = discountID;
        this.percentage = percentage;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public boolean isActive(LocalDate current) {
        return (!current.isBefore(startDate) && !current.isAfter(endDate));
    }

    public double applyDiscount(double price) {
        return price * (1 - (percentage / 100));
    }


    // getters & setters
    public void setDiscountID(int discountID) {
        this.discountID = discountID;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public int getDiscountID() {
        return discountID;
    }

    public double getPercentage() {
        return percentage;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

}
