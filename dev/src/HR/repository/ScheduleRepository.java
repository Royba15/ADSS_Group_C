package HR.repository;

import HR.domain.Employee;
import HR.domain.WeeklySchedule;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;

public interface ScheduleRepository {

    WeeklySchedule loadCurrentSchedule(Collection<Employee> employees);

    Map<LocalDate, WeeklySchedule> loadHistory(Collection<Employee> employees);

    void saveCurrentSchedule(WeeklySchedule schedule);

    void saveHistoryWeek(LocalDate startOfWeek, WeeklySchedule schedule);

    void removeHistoryBefore(LocalDate date);
}
