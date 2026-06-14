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
        String status,      // CREATED / SENT / RECEIVED / CANCELLED
        String createdAt
) {}
