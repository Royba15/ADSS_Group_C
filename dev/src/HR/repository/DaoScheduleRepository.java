package HR.repository;

import HR.data.ScheduleDAO;
import HR.domain.Employee;
import HR.domain.WeeklySchedule;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;

public class DaoScheduleRepository implements ScheduleRepository {

    private final ScheduleDAO scheduleDAO;

    public DaoScheduleRepository() {
        this.scheduleDAO = new ScheduleDAO();
    }

    @Override
    public WeeklySchedule loadCurrentSchedule(Collection<Employee> employees) {
        return scheduleDAO.loadCurrentSchedule(employees);
    }

    @Override
    public Map<LocalDate, WeeklySchedule> loadHistory(Collection<Employee> employees) {
        return scheduleDAO.loadHistory(employees);
    }

    @Override
    public void saveCurrentSchedule(WeeklySchedule schedule) {
        scheduleDAO.saveCurrentSchedule(schedule);
    }

    @Override
    public void saveHistoryWeek(LocalDate startOfWeek, WeeklySchedule schedule) {
        scheduleDAO.saveHistoryWeek(startOfWeek, schedule);
    }

    @Override
    public void removeHistoryBefore(LocalDate date) {
        scheduleDAO.removeHistoryBefore(date);
    }
}
