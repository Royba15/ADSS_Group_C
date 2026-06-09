package service;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import domain.WeeklySchedule;
import repository.DaoScheduleRepository;
import repository.ScheduleRepository;

public class ShiftHistory {

    // Stores all weekly schedules by week start date
    private Map<LocalDate, WeeklySchedule> history;
    private ScheduleRepository scheduleRepository;

    public ShiftHistory() {
        this.history = new LinkedHashMap<>();
        this.scheduleRepository = new DaoScheduleRepository();
    }

    public ShiftHistory(Map<LocalDate, WeeklySchedule> history) {
        this.history = new LinkedHashMap<>(history);
        this.scheduleRepository = new DaoScheduleRepository();
    }

    // Add a new weekly schedule to history
    public void addWeek(LocalDate startOfWeek, WeeklySchedule schedule) {
        history.put(startOfWeek, schedule);
        scheduleRepository.saveHistoryWeek(startOfWeek, schedule);
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

        scheduleRepository.removeHistoryBefore(date);
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
