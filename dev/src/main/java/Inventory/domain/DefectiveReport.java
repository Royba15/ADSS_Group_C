// DefectiveReport.java
package Inventory.domain;

import java.util.List;

public class DefectiveReport extends InventoryReport {
    private final List<DefectiveItem> defectiveItems;

    // Constructor
    public DefectiveReport(List<DefectiveItem> defectiveItems) {
        super();
        this.defectiveItems = defectiveItems;
    }

    // Getter
    public List<DefectiveItem> getDefectiveItems() { return defectiveItems; }
}