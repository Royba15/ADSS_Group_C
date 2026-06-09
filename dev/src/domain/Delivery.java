package domain;

public class Delivery {

    private ShiftSlot slot;
    private TruckType truckType;

    public Delivery(ShiftSlot slot, TruckType truckType) {
        this.slot = slot;
        this.truckType = truckType;
    }

    public ShiftSlot getSlot() {
        return slot;
    }

    public TruckType getTruckType() {
        return truckType;
    }
}
