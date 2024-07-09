package by.pavvel.dao;

import by.pavvel.config.AbstractTestcontainers;
import by.pavvel.dao.impl.EmployeeDaoImpl;
import by.pavvel.model.Employee;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class EmployeeDaoTest extends AbstractTestcontainers {

    private EmployeeDao underTest;

    @BeforeEach
    void setUp() {
        underTest = new EmployeeDaoImpl();
    }

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void shouldFindEmployees() {
        // given
        Employee employee1 = new Employee("Petr","Smirnov","Petrovich", "Middle");
        Employee employee2 = new Employee("Alexey","Petrov","Yegorovich", "Junior");

        List<Employee> employeeList = List.of(employee1, employee2);
        underTest.saveEmployee(employee1);
        underTest.saveEmployee(employee2);

        // when
        List<Employee> employees = underTest.findEmployees();

        // then
        assertThat(employees)
                .isNotNull()
                .isEqualTo(employeeList);
    }

    @Test
    void shouldFindEmployeeById() {
        // given
        Employee employee = new Employee("Petr","Smirnov","Petrovich", "Middle");
        underTest.saveEmployee(employee);

        // when
        Optional<Employee> employeeById = underTest.findEmployeeById(employee.getId());

        // then
        assertThat(employeeById)
                .isPresent()
                .hasValueSatisfying(e -> assertThat(e).isEqualTo(employee));
    }

    @Test
    void shouldSaveEmployee() {
        // given
        Employee employee = new Employee("Petr","Smirnov","Petrovich", "Middle");

        // when
        Employee savedEmployee = underTest.saveEmployee(employee);

        // then
        assertThat(savedEmployee)
                .isNotNull()
                .isEqualTo(employee);
    }

    @Test
    void shouldUpdateEmployee() {
        // given
        Employee employee = new Employee("Petr","Smirnov","Petrovich", "Middle");
        underTest.saveEmployee(employee);

        Employee employeeToUpdate = new Employee(employee.getId(), "Alexey", "Petrov", "Yegorovich", "Junior");

        // when
        Employee updatedEmployee = underTest.updateEmployee(employeeToUpdate);

        // then
        assertThat(updatedEmployee).isEqualTo(employeeToUpdate);
    }

    @Test
    void shouldDeleteEmployeeById() {
        // given
        Employee employee = new Employee("Petr","Smirnov","Petrovich", "Middle");
        underTest.saveEmployee(employee);

        // when
        underTest.deleteEmployee(employee.getId());

        // then

        List<Employee> employees = underTest.findEmployees();
        assertThat(employees.contains(employee)).isFalse();
    }

    @Test
    void shouldDeleteAllEmployees() {
        // given
        Employee employee = new Employee("Petr","Smirnov","Petrovich", "Middle");
        underTest.saveEmployee(employee);

        // when
        underTest.deleteAll();

        // then
        List<Employee> employees = underTest.findEmployees();
        assertThat(employees.size()).isEqualTo(0);
    }
}
