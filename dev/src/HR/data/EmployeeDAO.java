package HR.data;

import HR.domain.Branch;
import HR.domain.Driver;
import HR.domain.Employee;
import HR.domain.ShiftSlot;
import HR.domain.ShiftType;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {

    public void save(Employee employee) {
        DatabaseInitializer.initialize();

        String branchSql = "INSERT OR IGNORE INTO branches(id) VALUES (?)";
        String employeeSql =
                "INSERT OR REPLACE INTO employees(" +
                        "id, name, start_date, salary, employment_conditions, bank_account, " +
                        "branch_id, is_active, employee_type) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String deleteRolesSql = "DELETE FROM employee_roles WHERE employee_id = ?";
        String roleSql = "INSERT OR IGNORE INTO roles(name) VALUES (?)";
        String employeeRoleSql =
                "INSERT OR IGNORE INTO employee_roles(employee_id, role_name) VALUES (?, ?)";
        String deleteDriverSql = "DELETE FROM drivers WHERE employee_id = ?";
        String driverSql =
                "INSERT OR REPLACE INTO drivers(employee_id, license_type) VALUES (?, ?)";

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);

            try {
                try (PreparedStatement statement = connection.prepareStatement(branchSql)) {
                    statement.setString(1, employee.getBranch().getId());
                    statement.executeUpdate();
                }

                try (PreparedStatement statement = connection.prepareStatement(employeeSql)) {
                    statement.setString(1, employee.getId());
                    statement.setString(2, employee.getName());
                    statement.setString(3, employee.getStartDate().toString());
                    statement.setDouble(4, employee.getSalary());
                    statement.setString(5, employee.getEmploymentConditions());
                    statement.setString(6, employee.getBankAccount());
                    statement.setString(7, employee.getBranch().getId());
                    statement.setInt(8, employee.isActive() ? 1 : 0);
                    statement.setString(9, employee instanceof Driver ? "DRIVER" : "EMPLOYEE");
                    statement.executeUpdate();
                }

                try (PreparedStatement statement = connection.prepareStatement(deleteRolesSql)) {
                    statement.setString(1, employee.getId());
                    statement.executeUpdate();
                }

                for (String role : employee.getRoles()) {
                    try (PreparedStatement statement = connection.prepareStatement(roleSql)) {
                        statement.setString(1, role);
                        statement.executeUpdate();
                    }

                    try (PreparedStatement statement = connection.prepareStatement(employeeRoleSql)) {
                        statement.setString(1, employee.getId());
                        statement.setString(2, role);
                        statement.executeUpdate();
                    }
                }

                try (PreparedStatement statement = connection.prepareStatement(deleteDriverSql)) {
                    statement.setString(1, employee.getId());
                    statement.executeUpdate();
                }

                if (employee instanceof Driver) {
                    Driver driver = (Driver) employee;

                    try (PreparedStatement statement = connection.prepareStatement(driverSql)) {
                        statement.setString(1, driver.getId());
                        statement.setString(2, driver.getLicenseType());
                        statement.executeUpdate();
                    }
                }

                saveAvailability(connection, employee);
                connection.commit();
            } catch (SQLException e) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackException) {
                    e.addSuppressed(rollbackException);
                }

                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save employee", e);
        }
    }

    public Employee findById(String id) {
        DatabaseInitializer.initialize();

        String sql =
                "SELECT e.id, e.name, e.start_date, e.salary, e.employment_conditions, " +
                        "e.bank_account, e.branch_id, e.is_active, e.employee_type, d.license_type " +
                        "FROM employees e " +
                        "LEFT JOIN drivers d ON e.id = d.employee_id " +
                        "WHERE e.id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }

                Employee employee = mapEmployee(resultSet);
                loadRoles(connection, employee);
                loadAvailability(connection, employee);
                return employee;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find employee", e);
        }
    }

    public List<Employee> findAll() {
        DatabaseInitializer.initialize();

        String sql =
                "SELECT e.id, e.name, e.start_date, e.salary, e.employment_conditions, " +
                        "e.bank_account, e.branch_id, e.is_active, e.employee_type, d.license_type " +
                        "FROM employees e " +
                        "LEFT JOIN drivers d ON e.id = d.employee_id " +
                        "ORDER BY e.id";

        List<Employee> employees = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Employee employee = mapEmployee(resultSet);
                loadRoles(connection, employee);
                loadAvailability(connection, employee);
                employees.add(employee);
            }

            return employees;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load employees", e);
        }
    }

    private Employee mapEmployee(ResultSet resultSet) throws SQLException {
        Branch branch = new Branch(resultSet.getString("branch_id"));
        LocalDate startDate = LocalDate.parse(resultSet.getString("start_date"));
        String employeeType = resultSet.getString("employee_type");

        Employee employee;

        if ("DRIVER".equals(employeeType)) {
            employee = new Driver(
                    resultSet.getString("id"),
                    resultSet.getString("name"),
                    startDate,
                    resultSet.getDouble("salary"),
                    resultSet.getString("employment_conditions"),
                    resultSet.getString("bank_account"),
                    branch,
                    resultSet.getString("license_type"));
        } else {
            employee = new Employee(
                    resultSet.getString("id"),
                    resultSet.getString("name"),
                    startDate,
                    resultSet.getDouble("salary"),
                    resultSet.getString("employment_conditions"),
                    resultSet.getString("bank_account"),
                    branch);
        }

        employee.setActive(resultSet.getInt("is_active") == 1);
        return employee;
    }

    private void loadRoles(Connection connection, Employee employee) throws SQLException {
        String sql = "SELECT role_name FROM employee_roles WHERE employee_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, employee.getId());

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    employee.addRole(resultSet.getString("role_name"));
                }
            }
        }
    }

    private void saveAvailability(Connection connection, Employee employee) throws SQLException {
        String deleteSubmissionSql = "DELETE FROM availability_submissions WHERE employee_id = ?";
        String submissionSql = "INSERT INTO availability_submissions(employee_id) VALUES (?)";
        String slotSql =
                "INSERT INTO availability_shift_slots(submission_id, day_of_week, shift_type) " +
                        "VALUES (?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(deleteSubmissionSql)) {
            statement.setString(1, employee.getId());
            statement.executeUpdate();
        }

        if (!employee.hasSubmittedAvailability()) {
            return;
        }

        try (PreparedStatement statement = connection.prepareStatement(
                submissionSql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, employee.getId());
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new SQLException("Failed to create availability submission");
                }

                int submissionId = keys.getInt(1);

                for (ShiftSlot slot : employee.getAvailabilitySubmission().getAvailableShifts()) {
                    try (PreparedStatement slotStatement = connection.prepareStatement(slotSql)) {
                        slotStatement.setInt(1, submissionId);
                        slotStatement.setString(2, slot.getDay().name());
                        slotStatement.setString(3, slot.getType().name());
                        slotStatement.executeUpdate();
                    }
                }
            }
        }
    }

    private void loadAvailability(Connection connection, Employee employee) throws SQLException {
        String sql =
                "SELECT s.day_of_week, s.shift_type " +
                        "FROM availability_submissions a " +
                        "JOIN availability_shift_slots s ON a.id = s.submission_id " +
                        "WHERE a.employee_id = ? " +
                        "ORDER BY s.day_of_week, s.shift_type";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, employee.getId());

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    DayOfWeek day = DayOfWeek.valueOf(resultSet.getString("day_of_week"));
                    ShiftType type = ShiftType.valueOf(resultSet.getString("shift_type"));
                    employee.addAvailableShift(new ShiftSlot(day, type));
                }
            }
        }
    }
}
