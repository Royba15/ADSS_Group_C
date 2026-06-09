package domain;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

public class DeliveryMock {

    public static List<Delivery> getDeliveries() {
        List<Delivery> deliveries = new ArrayList<>();

        deliveries.add(new Delivery(new ShiftSlot(DayOfWeek.SUNDAY, ShiftType.MORNING), TruckType.SMALL));
        deliveries.add(new Delivery(new ShiftSlot(DayOfWeek.TUESDAY, ShiftType.EVENING), TruckType.HEAVY));
        deliveries.add(new Delivery(new ShiftSlot(DayOfWeek.THURSDAY, ShiftType.MORNING), TruckType.SMALL));

        return deliveries;
    }
}
