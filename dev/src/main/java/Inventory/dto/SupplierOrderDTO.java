package Inventory.dto;

/**
 * DTO להזמנות ספקים – נושא נתונים בין שכבות.
 */
public record SupplierOrderDTO(
        int orderId,
        int productId,
        String productName,
        int supplierId,
        String supplierCatalogId,
        int quantity,
        String status,          // CREATED / SENT / RECEIVED / CANCELLED / PENDING
        String orderType,       // IMMEDIATE / SCHEDULED
        String scheduledDate,   // null אם IMMEDIATE
        String frequency,       // ONCE / WEEKLY / MONTHLY / null אם IMMEDIATE
        String createdAt
) {}