package Inventory.dto;

/**
 * DTO – נושא נתונים בין שכבות.
 * אין לוגיקה, אין SQL, אין domain objects.
 */
public record ProductDTO(
        int productId,
        String name,
        int supplierId,
        double costPrice,
        double sellingPrice,
        double originalSellingPrice,
        String supplierCatalogId,
        String mainCategory,
        String subCategory,
        String subSubCategory,
        int shelfQuantity,
        int warehouseQuantity,
        int minQuantityThreshold,
        String location
) {}
