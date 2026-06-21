package HR.domain;

import java.util.ArrayList;
import java.util.List;

public class AvailabilitySubmission {

    // List of shift slots the employee is available to work
    private List<ShiftSlot> availableShifts;

    public AvailabilitySubmission() {
        this.availableShifts = new ArrayList<>();
    }

    public void addAvailableShift(ShiftSlot slot) {
        availableShifts.add(slot);
    }

    public boolean isAvailableFor(ShiftSlot slot) {
        return availableShifts.contains(slot);
    }

    public List<ShiftSlot> getAvailableShifts() {
        return availableShifts;
    }
}
