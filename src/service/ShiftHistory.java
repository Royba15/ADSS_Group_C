package service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import domain.WeeklySchedule;

public class ShiftHistory {

    // Stores all weekly schedules by week start date
    private Map<LocalDate, WeeklySchedule> history;

    public ShiftHistory() {
        this.history = new HashMap<>();
    }

    // Add a new weekly schedule to history
    public void addWeek(LocalDate startOfWeek, WeeklySchedule schedule) {
        history.put(startOfWeek, schedule);
    }

    // Get schedule for a specific week
    public WeeklySchedule getWeek(LocalDate startOfWeek) {
        return history.get(startOfWeek);
    }

    public Map<LocalDate, WeeklySchedule> getAllHistory() {
        return history;
    }
    public void removeHistoryBefore(LocalDate date) {

        Iterator<LocalDate> it = history.keySet().iterator();

        while (it.hasNext()) {

            LocalDate d = it.next();

            if (d.isBefore(date)) {
                it.remove();
            }
        }
    }
    public WeeklySchedule getLastWeek() {

        if (history.isEmpty()) {
            return null;
        }

        LocalDate latest = null;

        for (LocalDate d : history.keySet()) {
            if (latest == null || d.isAfter(latest)) {
                latest = d;
            }
        }

        return history.get(latest);
    }
}