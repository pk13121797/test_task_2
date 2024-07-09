package by.pavvel.servlet;

import by.pavvel.exception.EmployeeNotFoundException;
import by.pavvel.model.Employee;
import by.pavvel.service.EmployeeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmployeeServletTest {

    @Mock
    private EmployeeService employeeService;

    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private HttpServletResponse mockResponse;

    @Mock
    private BufferedReader mockBufferedReader;

    @InjectMocks
    private static EmployeeServlet underTest;

    @BeforeEach
    void setUp() throws IOException {
        when(mockResponse.getWriter()).thenReturn(new PrintWriter(Writer.nullWriter()));
        underTest = new EmployeeServlet(employeeService);
    }

    @Test
    public void shouldGetEmployees() throws Exception {
        // given
        when(mockRequest.getPathInfo()).thenReturn("/");

        // when
        underTest.doGet(mockRequest, mockResponse);

        // then
        verify(employeeService, times(1)).getEmployees();
        verify(mockResponse).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    public void shouldGetEmployeeById() throws Exception {
        // given
        when(mockRequest.getPathInfo()).thenReturn("employees/101/");

        // when
        underTest.doGet(mockRequest, mockResponse);

        // then
        verify(employeeService, times(1)).getEmployeeById(anyLong());
        verify(mockResponse).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    public void shouldSetBadRequestStatusWhenGetException() throws Exception {
        // given
        when(mockRequest.getPathInfo()).thenReturn("employees/101/d");

        // when
        underTest.doGet(mockRequest, mockResponse);

        // then
        verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void shouldThrowExceptionWhenGetEmployeeNotFound() throws Exception {
        // given
        when(mockRequest.getPathInfo()).thenReturn("employees/101");
        Long employeeId = 101L;
        doThrow(new EmployeeNotFoundException(String.format("Employee with id %s not found", employeeId)))
                .when(employeeService).getEmployeeById(employeeId);

        // when
        underTest.doGet(mockRequest, mockResponse);

        // then
        verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void shouldAddEmployee() throws Exception {
        // given
        String name = "Petr";
        String surname = "Smirnov";
        String middleName = "Petrovich";
        String post = "Middle";
        when(mockRequest.getReader()).thenReturn(mockBufferedReader);

        String employeeJson = """
                    {
                       "name" : %s,
                       "surname" : %s,
                       "middleName" : %s,
                       "post" : %s
                    }
                """.formatted(name, surname, middleName, post);
        when(mockBufferedReader.readLine()).thenReturn(employeeJson).thenReturn(null);

        // when
        underTest.doPost(mockRequest, mockResponse);

        // then
        ArgumentCaptor<Employee> argumentCaptor = ArgumentCaptor.forClass(Employee.class);
        verify(employeeService, times(1)).addEmployee(argumentCaptor.capture());

        Employee argumentCaptorValue = argumentCaptor.getValue();
        assertEquals(name, argumentCaptorValue.getName());
        assertEquals(surname, argumentCaptorValue.getSurname());
        assertEquals(middleName, argumentCaptorValue.getMiddleName());
        assertEquals(post, argumentCaptorValue.getPost());
        verify(mockResponse).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    public void shouldSetBadRequestStatusWhenPostException() throws Exception {
        // given
        when(mockRequest.getReader()).thenReturn(mockBufferedReader);
        doThrow(new RuntimeException("Runtime exception"))
                .when(employeeService).addEmployee(any());

        // when
        underTest.doPost(mockRequest, mockResponse);

        // then
        verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void shouldUpdateEmployee() throws Exception {
        // given
        Long id = 101L;
        String name = "Alexey";
        String surname = "Petrov";
        String middleName = "Yegorovich";
        String post = "Junior";
        when(mockRequest.getReader()).thenReturn(mockBufferedReader);

        String employeeJson = """
                    {
                       "id" : %s,
                       "name" : %s,
                       "surname" : %s,
                       "middleName" : %s,
                       "post" : %s
                    }
                """.formatted(id, name, surname, middleName, post);
        when(mockBufferedReader.readLine()).thenReturn(employeeJson).thenReturn(null);

        // when
        underTest.doPut(mockRequest, mockResponse);

        // then
        ArgumentCaptor<Employee> argumentCaptor = ArgumentCaptor.forClass(Employee.class);
        verify(employeeService, times(1)).updateEmployee(argumentCaptor.capture());

        Employee argumentCaptorValue = argumentCaptor.getValue();
        assertEquals(id, argumentCaptorValue.getId());
        assertEquals(name, argumentCaptorValue.getName());
        assertEquals(surname, argumentCaptorValue.getSurname());
        assertEquals(middleName, argumentCaptorValue.getMiddleName());
        assertEquals(post, argumentCaptorValue.getPost());
        verify(mockResponse).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    public void shouldSetBadRequestStatusWhenPutException() throws Exception {
        // given
        when(mockRequest.getReader()).thenReturn(mockBufferedReader);
        doThrow(new RuntimeException("Runtime exception"))
                .when(employeeService).updateEmployee(any());

        // when
        underTest.doPut(mockRequest, mockResponse);

        // then
        verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void shouldDeleteEmployeeById() throws Exception {
        // given
        when(mockRequest.getPathInfo()).thenReturn("employees/101");

        // when
        underTest.doDelete(mockRequest, mockResponse);

        // then
        verify(employeeService, times(1)).deleteEmployee(anyLong());
        verify(mockResponse).setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    @Test
    public void shouldSetBadRequestStatusWhenEmployeeNotFoundExceptionDeleteMethod() throws Exception {
        // given
        when(mockRequest.getPathInfo()).thenReturn("employees/101");
        Long employeeId = 101L;
        doThrow(new EmployeeNotFoundException(String.format("Employee with id %s not found", employeeId)))
                .when(employeeService).deleteEmployee(employeeId);

        // when
        underTest.doDelete(mockRequest, mockResponse);

        // then
        verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void shouldSetBadRequestStatusWhenDeleteException() throws Exception {
        // given
        when(mockRequest.getPathInfo()).thenReturn("employees/101/d");

        // when
        underTest.doDelete(mockRequest, mockResponse);

        // then
        verify(employeeService, times(0)).getEmployeeById(anyLong());
        verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }
}
