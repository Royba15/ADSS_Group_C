package Suppliers.service;

import Inventory.domain.SupplierOrder;

public class SupplierMockService {

    public int findBestSupplierForOrder(int productID, int quantity) {
        /*
         * MOCK:
         * כאן אנחנו מדמים את מודול הספקים.
         * במערכת אמיתית, מודול הספקים היה בודק:
         * - אילו ספקים מספקים את המוצר
         * - מחירים לפי כמות
         * - זמינות
         * - תנאי אספקה
         */

        if (productID == 101 && quantity >= 10) {
            return 2;
        }

        if (productID == 101) {
            return 1;
        }

        if (productID == 102) {
            return 3;
        }

        return 1;
    }

    public boolean sendOrder(SupplierOrder order) {
        if (order == null) {
            return false;
        }

        /*
         * MOCK:
         * מבחינתנו מודול הספקים קיבל את ההזמנה המלאה.
         */
        System.out.println("[SUPPLIERS MOCK] Full order received:");
        System.out.println(order);

        return true;
    }
}