package by.pavvel.service;

import by.pavvel.dao.EmployeeDao;
import by.pavvel.exception.EmployeeNotFoundException;
import by.pavvel.model.Employee;
import by.pavvel.service.impl.EmployeeServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;

class EmployeeServiceTest {

    @Mock
    private EmployeeDao employeeDao;

    @Captor
    private ArgumentCaptor<Employee> employeeArgumentCaptor;

    private AutoCloseable autoCloseable;

    private EmployeeService underTest;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTest = new EmployeeServiceImpl(employeeDao);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void shouldGetEmployees() {
        // given
        Employee employee = new Employee("Petr","Smirnov","Petrovich", "Middle");
        when(employeeDao.findEmployees()).thenReturn(List.of(employee));

        // when
        List<Employee> employees = underTest.getEmployees();

        // then
        assertThat(employees.size())
                .isEqualTo(1);
        assertThat(employee).isIn(employees);
    }

    @Test
    void shouldGetEmployeeById() {
        // given
        Long employeeId = 1L;
        Employee employee = new Employee("Petr","Smirnov","Petrovich", "Middle");
        employee.setId(employeeId);
        when(employeeDao.findEmployeeById(employeeId)).thenReturn(Optional.of(employee));

        // when
        Employee employeeById = underTest.getEmployeeById(employeeId);

        // then
        assertThat(employeeById)
                .isNotNull()
                .isEqualTo(employee);
    }

    @Test
    void shouldThrowExceptionWhenEmployeeNotExists() {
        // given
        Long employeeId = 1L;
        when(employeeDao.findEmployeeById(employeeId)).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.getEmployeeById(employeeId))
                .isInstanceOf(EmployeeNotFoundException.class)
                .hasMessageContaining(String.format("Employee with id %s doesn't exist", employeeId));
    }

    @Test
    void shouldAddEmployee() {
        // given
        Employee employee = new Employee("Petr","Smirnov","Petrovich", "Middle");

        // when
        underTest.addEmployee(employee);

        // then
        then(employeeDao).should().saveEmployee(employeeArgumentCaptor.capture());
        Employee employeeArgumentCaptorValue = employeeArgumentCaptor.getValue();
        assertThat(employeeArgumentCaptorValue).isEqualTo(employee);
    }

    @Test
    void shouldUpdateEmployee() {
        // given
        Long employeeId = 1L;
        Employee employee = new Employee("Petr","Smirnov","Petrovich", "Middle");
        employee.setId(employeeId);

        ArgumentCaptor<Employee> employeeArgumentCaptor = ArgumentCaptor.forClass(Employee.class);
        when(employeeDao.findEmployeeById(employeeId)).thenReturn(Optional.of(employee));
        when(employeeDao.updateEmployee(employeeArgumentCaptor.capture())).thenReturn(employee);

        // when
        Employee updatedEmployee = underTest.updateEmployee(employee);

        // then
        verify(employeeDao, times(1)).updateEmployee(employee);
        assertThat(updatedEmployee).isEqualTo(employeeArgumentCaptor.getValue());
    }

    @Test
    void shouldNotUpdateEmployeeByIdWhenEmployeeNotExists() {
        // given
        Long employeeId = 1L;
        Employee employee = new Employee("Petr","Smirnov","Petrovich", "Middle");
        employee.setId(employeeId);
        when(employeeDao.findEmployeeById(employeeId)).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.updateEmployee(employee))
                .isInstanceOf(EmployeeNotFoundException.class)
                .hasMessageContaining(String.format("Employee with id %s doesn't exist",employeeId));
        then(employeeDao).should(never()).updateEmployee(any());
    }

    @Test
    void shouldDeleteEmployee() {
        // given
        Long employeeId = 1L;
        Employee employee = new Employee("Petr","Smirnov","Petrovich", "Middle");
        when(employeeDao.findEmployeeById(employeeId)).thenReturn(Optional.of(employee));
        doNothing().when(employeeDao).deleteEmployee(employeeId);

        // when
        underTest.deleteEmployee(employeeId);

        // then
        verify(employeeDao, times(1)).deleteEmployee(employeeId);
    }

    @Test
    void shouldNotDeleteEmployeeByIdWhenEmployeeNotExists() {
        // given
        Long employeeId = 1L;
        when(employeeDao.findEmployeeById(employeeId)).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.deleteEmployee(employeeId))
                .isInstanceOf(EmployeeNotFoundException.class)
                .hasMessageContaining(String.format("Employee with id %s doesn't exist",employeeId));
        then(employeeDao).should(never()).deleteEmployee(any());
    }
}