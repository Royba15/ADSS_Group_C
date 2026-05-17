package service;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import data.ScheduleDAO;
import domain.WeeklySchedule;

public class ShiftHistory {

    // Stores all weekly schedules by week start date
    private Map<LocalDate, WeeklySchedule> history;
    private ScheduleDAO scheduleDAO;

    public ShiftHistory() {
        this.history = new LinkedHashMap<>();
        this.scheduleDAO = new ScheduleDAO();
    }

    public ShiftHistory(Map<LocalDate, WeeklySchedule> history) {
        this.history = new LinkedHashMap<>(history);
        this.scheduleDAO = new ScheduleDAO();
    }

    // Add a new weekly schedule to history
    public void addWeek(LocalDate startOfWeek, WeeklySchedule schedule) {
        history.put(startOfWeek, schedule);
        scheduleDAO.saveHistoryWeek(startOfWeek, schedule);
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

        scheduleDAO.removeHistoryBefore(date);
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
