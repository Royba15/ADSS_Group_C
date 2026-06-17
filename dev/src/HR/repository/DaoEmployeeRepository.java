package HR.repository;

import HR.data.EmployeeDAO;
import HR.domain.Employee;

import java.util.List;

public class DaoEmployeeRepository implements EmployeeRepository {

    private final EmployeeDAO employeeDAO;

    public DaoEmployeeRepository() {
        this.employeeDAO = new EmployeeDAO();
    }

    @Override
    public void save(Employee employee) {
        employeeDAO.save(employee);
    }

    @Override
    public Employee findById(String id) {
        return employeeDAO.findById(id);
    }

    @Override
    public List<Employee> findAll() {
        return employeeDAO.findAll();
    }
}
