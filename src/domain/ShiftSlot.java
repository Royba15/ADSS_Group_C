package domain;

import java.time.DayOfWeek;
import java.util.Objects;

public class ShiftSlot {

    private DayOfWeek day;
    private ShiftType type;

    public ShiftSlot(DayOfWeek day, ShiftType type) {
        this.day = day;
        this.type = type;
    }

    public DayOfWeek getDay() {
        return day;
    }

    public ShiftType getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ShiftSlot)) return false;
        ShiftSlot that = (ShiftSlot) o;
        return day == that.day && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(day, type);
    }
}