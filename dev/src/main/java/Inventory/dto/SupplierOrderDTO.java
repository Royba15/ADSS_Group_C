package Inventory.dto;

public record SupplierOrderDTO(
        int orderId,
        int productId,
        String productName,
        int supplierId,
        String supplierCatalogId,
        int quantity,
        String status,// CREATED / SENT / RECEIVED / CANCELLED / PENDING
        String orderType,// IMMEDIATE / SCHEDULED
        String scheduledDate,
        String frequency,
        String createdAt
) {}