package by.pavvel.dao.impl;

import by.pavvel.dao.EmployeeDao;
import by.pavvel.config.ConnectionManager;
import by.pavvel.config.ConnectionManagerImpl;
import by.pavvel.model.Employee;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmployeeDaoImpl implements EmployeeDao {

    private static final ConnectionManager connectionManager = ConnectionManagerImpl.getInstance();

    private static final String SELECT_ALL_EMPLOYEES = """
            SELECT employee_id, name, surname, middle_name, post FROM employee;
            """;

    private static final String SELECT_EMPLOYEE_BY_ID = """
            SELECT employee_id, name, surname, middle_name, post FROM employee WHERE employee_id = ?;
            """;

    private static final String INSERT_EMPLOYEE = """
            INSERT INTO employee(name, surname, middle_name, post) VALUES (?, ?, ?, ?);
            """;

    private static final String UPDATE_EMPLOYEE = """
            UPDATE employee SET name = ?, surname = ?, middle_name = ?, post = ? WHERE employee_id = ?;
            """;

    private static final String DELETE_EMPLOYEE_BY_ID = """
            DELETE FROM employee WHERE employee_id = ?;
            """;

    private static final String DELETE_ALL_EMPLOYEES = """
            DELETE FROM employee;
            """;

    private static EmployeeDao instance;

    public static synchronized EmployeeDao getInstance() {
        if (instance == null) {
            instance = new EmployeeDaoImpl();
        }
        return instance;
    }

    @Override
    public List<Employee> findEmployees() {
        List<Employee> employees = new ArrayList<>();
        try(Connection connection = connectionManager.getConnection()) {
            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery(SELECT_ALL_EMPLOYEES);
            while (resultSet.next()) {
                Employee employee = new Employee();
                employee.setId(resultSet.getLong("employee_id"));
                employee.setName(resultSet.getString("name"));
                employee.setSurname(resultSet.getString("surname"));
                employee.setMiddleName(resultSet.getString("middle_name"));
                employee.setPost(resultSet.getString("post"));
                employees.add(employee);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return employees;
    }

    @Override
    public Optional<Employee> findEmployeeById(Long employeeId) {
        Employee employee = null;
        try (Connection connection = connectionManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_EMPLOYEE_BY_ID);
            preparedStatement.setLong(1, employeeId);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                employee = new Employee();
                employee.setId(resultSet.getLong("employee_id"));
                employee.setName(resultSet.getString("name"));
                employee.setSurname(resultSet.getString("surname"));
                employee.setMiddleName(resultSet.getString("middle_name"));
                employee.setPost(resultSet.getString("post"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.ofNullable(employee);
    }

    @Override
    public Employee saveEmployee(Employee employee) {

        Long employeeId = null;

        try (Connection connection = connectionManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_EMPLOYEE, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, employee.getName());
            preparedStatement.setString(2, employee.getSurname());
            preparedStatement.setString(3, employee.getMiddleName());
            preparedStatement.setString(4, employee.getPost());
            preparedStatement.executeUpdate();

            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet != null && resultSet.next()) {
                employeeId = resultSet.getLong(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        employee.setId(employeeId);
        return employee;
    }

    @Override
    public Employee updateEmployee(Employee employee) {
        try (Connection connection = connectionManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_EMPLOYEE);

            preparedStatement.setString(1, employee.getName());
            preparedStatement.setString(2, employee.getSurname());
            preparedStatement.setString(3, employee.getMiddleName());
            preparedStatement.setString(4, employee.getPost());
            preparedStatement.setLong(5, employee.getId());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return employee;
    }

    @Override
    public void deleteEmployee(Long employeeId) {
        try (Connection connection = connectionManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(DELETE_EMPLOYEE_BY_ID);
            preparedStatement.setLong(1, employeeId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteAll() {
        try (Connection connection = connectionManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(DELETE_ALL_EMPLOYEES);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
