// DefectiveReport.java
package Inventory.domain;

import java.util.List;

public class DefectiveReport extends InventoryReport {
    private List<DefectiveItem> defectiveItems;

    public DefectiveReport(List<DefectiveItem> defectiveItems) {
        super();
        this.defectiveItems = defectiveItems;
    }

    public List<DefectiveItem> getDefectiveItems() { return defectiveItems; }

    @Override
    public String getReportType() { return "Defective Items Report"; }
}