package by.pavvel.servlet;

import by.pavvel.dto.request.TaskProjectEmployeesDtoRequest;
import by.pavvel.exception.TaskNotFoundException;
import by.pavvel.model.Status;
import by.pavvel.service.TaskService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServletTest {

    @Mock
    private TaskService taskService;

    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private HttpServletResponse mockResponse;

    @Mock
    private BufferedReader mockBufferedReader;

    @InjectMocks
    private static TaskServlet underTest;

    @BeforeEach
    void setUp() throws IOException {
        when(mockResponse.getWriter()).thenReturn(new PrintWriter(Writer.nullWriter()));
        underTest = new TaskServlet(taskService);
    }

    @Test
    public void shouldGetTasks() throws Exception {
        // given
        when(mockRequest.getPathInfo()).thenReturn("/");

        // when
        underTest.doGet(mockRequest, mockResponse);

        // then
        verify(taskService, times(1)).getTasks();
        verify(mockResponse).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    public void shouldGetTaskById() throws Exception {
        // given
        when(mockRequest.getPathInfo()).thenReturn("tasks/101/");

        // when
        underTest.doGet(mockRequest, mockResponse);

        // then
        verify(taskService, times(1)).getTaskById(anyLong());
        verify(mockResponse).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    public void shouldSetBadRequestStatusWhenGetException() throws Exception {
        // given
        when(mockRequest.getPathInfo()).thenReturn("tasks/101/d");

        // when
        underTest.doGet(mockRequest, mockResponse);

        // then
        verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void shouldThrowExceptionWhenGetTaskNotFound() throws Exception {
        // given
        when(mockRequest.getPathInfo()).thenReturn("tasks/101");
        Long taskId = 101L;
        doThrow(new TaskNotFoundException(String.format("Task with id %s not found", taskId)))
                .when(taskService).getTaskById(taskId);

        // when
        underTest.doGet(mockRequest, mockResponse);

        // then
        verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void shouldAddTask() throws Exception {
        // given
        String title = "write code";
        Integer hours = 4;
        LocalDate startDate = LocalDate.parse("2021-04-13");
        LocalDate endDate = LocalDate.parse("2025-04-15");
        Status status = Status.POSTPONED;
        Long project = 101L;
        when(mockRequest.getReader()).thenReturn(mockBufferedReader);

        String taskJson = """
                    {
                    "title" : "%s",
                    "hours" : "%d",
                    "startDate" : "%s",
                    "endDate" : "%s",
                    "status" : "%s",
                    "project" : "%d"
                    }
                """.formatted(title, hours, startDate, endDate, status, project);
        when(mockBufferedReader.readLine()).thenReturn(taskJson).thenReturn(null);

        // when
        underTest.doPost(mockRequest, mockResponse);

        // then
        ArgumentCaptor<TaskProjectEmployeesDtoRequest> argumentCaptor = ArgumentCaptor.forClass(TaskProjectEmployeesDtoRequest.class);
        verify(taskService, times(1)).addTask(argumentCaptor.capture());

        TaskProjectEmployeesDtoRequest argumentCaptorValue = argumentCaptor.getValue();
        assertEquals(title, argumentCaptorValue.getTitle());
        assertEquals(hours, argumentCaptorValue.getHours());
        assertEquals(startDate, argumentCaptorValue.getStartDate());
        assertEquals(endDate, argumentCaptorValue.getEndDate());
        assertEquals(status, argumentCaptorValue.getStatus());
        assertEquals(project, argumentCaptorValue.getProject());
        verify(mockResponse).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    public void shouldAddTaskWithEmployees() throws Exception {
        // given
        String title = "write code";
        Integer hours = 4;
        LocalDate startDate = LocalDate.parse("2021-04-13");
        LocalDate endDate = LocalDate.parse("2025-04-15");
        Status status = Status.POSTPONED;
        Long project = 101L;
        String employees = "101, 102";
        when(mockRequest.getReader()).thenReturn(mockBufferedReader);

        String taskJson = """
                    {
                    "title" : "%s",
                    "hours" : "%d",
                    "startDate" : "%s",
                    "endDate" : "%s",
                    "status" : "%s",
                    "project" : "%d",
                    "employees" : "%s"
                    }
                """.formatted(title, hours, startDate, endDate, status, project, employees);
        when(mockBufferedReader.readLine()).thenReturn(taskJson).thenReturn(null);

        // when
        underTest.doPost(mockRequest, mockResponse);

        // then
        ArgumentCaptor<TaskProjectEmployeesDtoRequest> argumentCaptor = ArgumentCaptor.forClass(TaskProjectEmployeesDtoRequest.class);
        verify(taskService, times(1)).addTask(argumentCaptor.capture());

        TaskProjectEmployeesDtoRequest argumentCaptorValue = argumentCaptor.getValue();
        assertEquals(title, argumentCaptorValue.getTitle());
        assertEquals(hours, argumentCaptorValue.getHours());
        assertEquals(startDate, argumentCaptorValue.getStartDate());
        assertEquals(endDate, argumentCaptorValue.getEndDate());
        assertEquals(status, argumentCaptorValue.getStatus());
        assertEquals(project, argumentCaptorValue.getProject());
        assertEquals(employees, argumentCaptorValue.getEmployees());
        verify(mockResponse).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    public void shouldSetBadRequestStatusWhenPostException() throws Exception {
        // given
        when(mockRequest.getReader()).thenReturn(mockBufferedReader);
        doThrow(new RuntimeException("Runtime exception"))
                .when(taskService).addTask(any());

        // when
        underTest.doPost(mockRequest, mockResponse);

        // then
        verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void shouldUpdateTask() throws Exception {
        // given
        Long id = 103L;
        String title = "read article";
        Integer hours = 5;
        LocalDate startDate = LocalDate.parse("2020-04-30");
        LocalDate endDate = LocalDate.parse("2025-04-23");
        Status status = Status.NOT_STARTED;
        Long project = 102L;
        String employees = "101, 103";
        when(mockRequest.getReader()).thenReturn(mockBufferedReader);

        String taskJson = """
                    {
                    "id" : "%d",
                    "title" : "%s",
                    "hours" : "%d",
                    "startDate" : "%s",
                    "endDate" : "%s",
                    "status" : "%s",
                    "project" : "%d",
                    "employees" : "%s"
                    }
                """.formatted(id, title, hours, startDate, endDate, status, project, employees);
        when(mockBufferedReader.readLine()).thenReturn(taskJson).thenReturn(null);

        // when
        underTest.doPut(mockRequest, mockResponse);

        // then
        ArgumentCaptor<TaskProjectEmployeesDtoRequest> argumentCaptor = ArgumentCaptor.forClass(TaskProjectEmployeesDtoRequest.class);
        verify(taskService, times(1)).updateTask(argumentCaptor.capture());

        TaskProjectEmployeesDtoRequest argumentCaptorValue = argumentCaptor.getValue();
        assertEquals(id, argumentCaptorValue.getId());
        assertEquals(title, argumentCaptorValue.getTitle());
        assertEquals(hours, argumentCaptorValue.getHours());
        assertEquals(startDate, argumentCaptorValue.getStartDate());
        assertEquals(endDate, argumentCaptorValue.getEndDate());
        assertEquals(status, argumentCaptorValue.getStatus());
        assertEquals(project, argumentCaptorValue.getProject());
        assertEquals(employees, argumentCaptorValue.getEmployees());
        verify(mockResponse).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    public void shouldSetBadRequestStatusWhenPutException() throws Exception {
        // given
        when(mockRequest.getReader()).thenReturn(mockBufferedReader);
        doThrow(new RuntimeException("Runtime exception"))
                .when(taskService).updateTask(any());

        // when
        underTest.doPut(mockRequest, mockResponse);

        // then
        verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void shouldDeleteTaskById() throws Exception {
        // given
        when(mockRequest.getPathInfo()).thenReturn("tasks/101");

        // when
        underTest.doDelete(mockRequest, mockResponse);

        // then
        verify(taskService, times(1)).deleteTask(anyLong());
        verify(mockResponse).setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    @Test
    public void shouldSetBadRequestStatusWhenTaskNotFoundExceptionDeleteMethod() throws Exception {
        // given
        when(mockRequest.getPathInfo()).thenReturn("tasks/101");
        Long taskId = 101L;
        doThrow(new TaskNotFoundException(String.format("Task with id %s not found", taskId)))
                .when(taskService).deleteTask(taskId);

        // when
        underTest.doDelete(mockRequest, mockResponse);

        // then
        verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void shouldSetBadRequestStatusWhenDeleteException() throws Exception {
        // given
        when(mockRequest.getPathInfo()).thenReturn("tasks/101/d");

        // when
        underTest.doDelete(mockRequest, mockResponse);

        // then
        verify(taskService, times(0)).deleteTask(anyLong());
        verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }
}
