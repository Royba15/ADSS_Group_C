package data;

import domain.Delivery;
import domain.Employee;
import domain.Shift;
import domain.ShiftSlot;
import domain.ShiftType;
import domain.TruckType;
import domain.WeeklySchedule;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ScheduleDAO {

    public WeeklySchedule loadCurrentSchedule(Collection<Employee> employees) {
        DatabaseInitializer.initialize();

        String currentSql =
                "SELECT w.id, w.submission_day, w.submission_time " +
                        "FROM current_schedule c " +
                        "JOIN weekly_schedules w ON c.weekly_schedule_id = w.id " +
                        "WHERE c.id = 1";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(currentSql);
             ResultSet resultSet = statement.executeQuery()) {

            if (!resultSet.next()) {
                return new WeeklySchedule();
            }

            Map<String, Employee> employeeById = mapEmployeesById(employees);
            int scheduleId = resultSet.getInt("id");
            return loadScheduleById(connection, scheduleId, employeeById);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load current schedule", e);
        }
    }

    public Map<LocalDate, WeeklySchedule> loadHistory(Collection<Employee> employees) {
        DatabaseInitializer.initialize();

        String sql =
                "SELECT start_of_week, weekly_schedule_id " +
                        "FROM shift_history " +
                        "ORDER BY start_of_week";

        Map<LocalDate, WeeklySchedule> history = new LinkedHashMap<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            Map<String, Employee> employeeById = mapEmployeesById(employees);

            while (resultSet.next()) {
                LocalDate startOfWeek = LocalDate.parse(resultSet.getString("start_of_week"));
                int scheduleId = resultSet.getInt("weekly_schedule_id");
                history.put(startOfWeek, loadScheduleById(connection, scheduleId, employeeById));
            }

            return history;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load shift history", e);
        }
    }

    public void saveCurrentSchedule(WeeklySchedule schedule) {
        DatabaseInitializer.initialize();

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);

            try {
                deleteCurrentSchedule(connection);

                int scheduleId = insertSchedule(connection, schedule, null);
                setCurrentSchedule(connection, scheduleId);
                saveScheduleDetails(connection, scheduleId, schedule);

                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save current schedule", e);
        }
    }

    public void saveHistoryWeek(LocalDate startOfWeek, WeeklySchedule schedule) {
        DatabaseInitializer.initialize();

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);

            try {
                deleteHistoryWeek(connection, startOfWeek);

                int scheduleId = insertSchedule(connection, schedule, startOfWeek);
                saveScheduleDetails(connection, scheduleId, schedule);
                insertHistoryRow(connection, startOfWeek, scheduleId);

                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save shift history", e);
        }
    }

    public void removeHistoryBefore(LocalDate date) {
        DatabaseInitializer.initialize();

        String selectSql = "SELECT weekly_schedule_id FROM shift_history WHERE start_of_week < ?";
        String deleteHistorySql = "DELETE FROM shift_history WHERE start_of_week < ?";
        String deleteScheduleSql = "DELETE FROM weekly_schedules WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);

            try {
                List<Integer> scheduleIds = new ArrayList<>();

                try (PreparedStatement statement = connection.prepareStatement(selectSql)) {
                    statement.setString(1, date.toString());

                    try (ResultSet resultSet = statement.executeQuery()) {
                        while (resultSet.next()) {
                            scheduleIds.add(resultSet.getInt("weekly_schedule_id"));
                        }
                    }
                }

                try (PreparedStatement statement = connection.prepareStatement(deleteHistorySql)) {
                    statement.setString(1, date.toString());
                    statement.executeUpdate();
                }

                for (Integer scheduleId : scheduleIds) {
                    try (PreparedStatement statement = connection.prepareStatement(deleteScheduleSql)) {
                        statement.setInt(1, scheduleId);
                        statement.executeUpdate();
                    }
                }

                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to remove old shift history", e);
        }
    }

    private Map<String, Employee> mapEmployeesById(Collection<Employee> employees) {
        Map<String, Employee> result = new LinkedHashMap<>();

        for (Employee employee : employees) {
            result.put(employee.getId(), employee);
        }

        return result;
    }

    private void deleteCurrentSchedule(Connection connection) throws SQLException {
        Integer currentScheduleId = null;

        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT weekly_schedule_id FROM current_schedule WHERE id = 1");
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                currentScheduleId = resultSet.getInt("weekly_schedule_id");
            }
        }

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("DELETE FROM current_schedule WHERE id = 1");
        }

        if (currentScheduleId != null) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "DELETE FROM weekly_schedules WHERE id = ?")) {

                statement.setInt(1, currentScheduleId);
                statement.executeUpdate();
            }
        }
    }

    private int insertSchedule(Connection connection, WeeklySchedule schedule, LocalDate startOfWeek)
            throws SQLException {

        String sql =
                "INSERT INTO weekly_schedules(week_start_date, submission_day, submission_time) " +
                        "VALUES (?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {

            if (startOfWeek == null) {
                statement.setString(1, null);
            } else {
                statement.setString(1, startOfWeek.toString());
            }

            statement.setString(2, schedule.getSubmissionDay().name());
            statement.setString(3, schedule.getSubmissionTime().toString());
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new SQLException("Failed to create weekly schedule");
                }

                return keys.getInt(1);
            }
        }
    }

    private void saveScheduleDetails(Connection connection, int scheduleId, WeeklySchedule schedule)
            throws SQLException {

        for (Shift shift : schedule.getAllShifts()) {
            int shiftId = insertShift(connection, scheduleId, shift);
            saveStaffingRequirements(connection, shiftId, shift);
            saveAssignments(connection, shiftId, shift);
            saveDelivery(connection, shiftId, shift);
        }
    }

    private void insertHistoryRow(Connection connection, LocalDate startOfWeek, int scheduleId)
            throws SQLException {

        String sql = "INSERT INTO shift_history(start_of_week, weekly_schedule_id) VALUES (?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, startOfWeek.toString());
            statement.setInt(2, scheduleId);
            statement.executeUpdate();
        }
    }

    private void deleteHistoryWeek(Connection connection, LocalDate startOfWeek) throws SQLException {
        Integer oldScheduleId = null;

        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT weekly_schedule_id FROM shift_history WHERE start_of_week = ?")) {

            statement.setString(1, startOfWeek.toString());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    oldScheduleId = resultSet.getInt("weekly_schedule_id");
                }
            }
        }

        try (PreparedStatement statement = connection.prepareStatement(
                "DELETE FROM shift_history WHERE start_of_week = ?")) {

            statement.setString(1, startOfWeek.toString());
            statement.executeUpdate();
        }

        if (oldScheduleId != null) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "DELETE FROM weekly_schedules WHERE id = ?")) {

                statement.setInt(1, oldScheduleId);
                statement.executeUpdate();
            }
        }
    }

    private WeeklySchedule loadScheduleById(
            Connection connection,
            int scheduleId,
            Map<String, Employee> employeeById) throws SQLException {

        String sql = "SELECT submission_day, submission_time FROM weekly_schedules WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, scheduleId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    throw new SQLException("Weekly schedule not found: " + scheduleId);
                }

                WeeklySchedule schedule = new WeeklySchedule();
                schedule.setSubmissionDeadline(
                        DayOfWeek.valueOf(resultSet.getString("submission_day")),
                        LocalTime.parse(resultSet.getString("submission_time")));

                Map<Integer, Shift> shiftById =
                        loadShifts(connection, scheduleId, schedule, employeeById);

                loadStaffingRequirements(connection, shiftById);
                loadAssignments(connection, shiftById, employeeById);
                loadDeliveries(connection, shiftById);

                return schedule;
            }
        }
    }

    private void setCurrentSchedule(Connection connection, int scheduleId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO current_schedule(id, weekly_schedule_id) VALUES (1, ?)")) {

            statement.setInt(1, scheduleId);
            statement.executeUpdate();
        }
    }

    private int insertShift(Connection connection, int scheduleId, Shift shift) throws SQLException {
        String sql =
                "INSERT INTO shifts(weekly_schedule_id, day_of_week, shift_type, manager_id, blocked) " +
                        "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, scheduleId);
            statement.setString(2, shift.getSlot().getDay().name());
            statement.setString(3, shift.getSlot().getType().name());

            if (shift.getManager() == null) {
                statement.setString(4, null);
            } else {
                statement.setString(4, shift.getManager().getId());
            }

            statement.setInt(5, shift.isBlocked() ? 1 : 0);
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new SQLException("Failed to create shift");
                }

                return keys.getInt(1);
            }
        }
    }

    private void saveStaffingRequirements(Connection connection, int shiftId, Shift shift)
            throws SQLException {

        String roleSql = "INSERT OR IGNORE INTO roles(name) VALUES (?)";
        String requirementSql =
                "INSERT INTO staffing_requirements(shift_id, role_name, required_amount) " +
                        "VALUES (?, ?, ?)";

        for (String role : shift.getRequiredRoles()) {
            try (PreparedStatement statement = connection.prepareStatement(roleSql)) {
                statement.setString(1, role);
                statement.executeUpdate();
            }

            try (PreparedStatement statement = connection.prepareStatement(requirementSql)) {
                statement.setInt(1, shiftId);
                statement.setString(2, role);
                statement.setInt(3, shift.getRequiredRoleAmount(role));
                statement.executeUpdate();
            }
        }
    }

    private void saveAssignments(Connection connection, int shiftId, Shift shift) throws SQLException {
        String roleSql = "INSERT OR IGNORE INTO roles(name) VALUES (?)";
        String assignmentSql =
                "INSERT INTO shift_assignments(shift_id, employee_id, role_name) VALUES (?, ?, ?)";

        for (Map.Entry<Employee, String> assignment : shift.getAssignedEmployees().entrySet()) {
            try (PreparedStatement statement = connection.prepareStatement(roleSql)) {
                statement.setString(1, assignment.getValue());
                statement.executeUpdate();
            }

            try (PreparedStatement statement = connection.prepareStatement(assignmentSql)) {
                statement.setInt(1, shiftId);
                statement.setString(2, assignment.getKey().getId());
                statement.setString(3, assignment.getValue());
                statement.executeUpdate();
            }
        }
    }

    private void saveDelivery(Connection connection, int shiftId, Shift shift) throws SQLException {
        if (!shift.hasDelivery()) {
            return;
        }

        String sql = "INSERT INTO deliveries(shift_id, truck_type) VALUES (?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, shiftId);
            statement.setString(2, shift.getDelivery().getTruckType().name());
            statement.executeUpdate();
        }
    }

    private Map<Integer, Shift> loadShifts(
            Connection connection,
            int scheduleId,
            WeeklySchedule schedule,
            Map<String, Employee> employeeById) throws SQLException {

        String sql =
                "SELECT id, day_of_week, shift_type, manager_id, blocked " +
                        "FROM shifts WHERE weekly_schedule_id = ?";

        Map<Integer, Shift> shiftById = new LinkedHashMap<>();

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, scheduleId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    ShiftSlot slot = new ShiftSlot(
                            DayOfWeek.valueOf(resultSet.getString("day_of_week")),
                            ShiftType.valueOf(resultSet.getString("shift_type")));

                    Shift shift = schedule.getShift(slot);

                    if (shift == null) {
                        continue;
                    }

                    shift.setBlocked(resultSet.getInt("blocked") == 1);

                    String managerId = resultSet.getString("manager_id");
                    if (managerId != null && employeeById.containsKey(managerId)) {
                        shift.restoreManager(employeeById.get(managerId));
                    }

                    shiftById.put(resultSet.getInt("id"), shift);
                }
            }
        }

        return shiftById;
    }

    private void loadStaffingRequirements(Connection connection, Map<Integer, Shift> shiftById)
            throws SQLException {

        String sql =
                "SELECT shift_id, role_name, required_amount FROM staffing_requirements";

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Shift shift = shiftById.get(resultSet.getInt("shift_id"));

                if (shift != null) {
                    shift.setRequiredRole(
                            resultSet.getString("role_name"),
                            resultSet.getInt("required_amount"));
                }
            }
        }
    }

    private void loadAssignments(
            Connection connection,
            Map<Integer, Shift> shiftById,
            Map<String, Employee> employeeById) throws SQLException {

        String sql =
                "SELECT shift_id, employee_id, role_name FROM shift_assignments";

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Shift shift = shiftById.get(resultSet.getInt("shift_id"));
                Employee employee = employeeById.get(resultSet.getString("employee_id"));

                if (shift != null && employee != null) {
                    shift.restoreAssignedEmployee(employee, resultSet.getString("role_name"));
                }
            }
        }
    }

    private void loadDeliveries(Connection connection, Map<Integer, Shift> shiftById)
            throws SQLException {

        String sql = "SELECT shift_id, truck_type FROM deliveries";

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Shift shift = shiftById.get(resultSet.getInt("shift_id"));

                if (shift != null) {
                    ShiftSlot slot = shift.getSlot();
                    TruckType truckType = TruckType.valueOf(resultSet.getString("truck_type"));
                    shift.setDelivery(new Delivery(slot, truckType));
                }
            }
        }
    }
}
