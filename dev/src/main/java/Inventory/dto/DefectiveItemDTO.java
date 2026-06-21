package Inventory.dto;

public record DefectiveItemDTO(
        int id,
        int productId,
        String productName,
        int quantity,
        String reason
) {}
