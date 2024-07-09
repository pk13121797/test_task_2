package by.pavvel.dao;

import by.pavvel.model.Employee;

import java.util.List;
import java.util.Optional;

public interface EmployeeDao {

    List<Employee> findEmployees();

    Optional<Employee> findEmployeeById(Long employeeId);

    Employee saveEmployee(Employee employee);

    Employee updateEmployee(Employee employee);

    void deleteEmployee(Long employeeId);

    void deleteAll();
}
