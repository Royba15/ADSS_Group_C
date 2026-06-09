package repository;

import domain.Employee;

import java.util.List;

public interface EmployeeRepository {

    void save(Employee employee);

    Employee findById(String id);

    List<Employee> findAll();
}
