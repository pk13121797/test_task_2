package by.pavvel.servlet;

import by.pavvel.dto.request.ProjectTasksDtoRequest;
import by.pavvel.exception.ProjectNotFoundException;
import by.pavvel.service.ProjectService;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProjectServletTest {

    @Mock
    private ProjectService projectService;

    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private HttpServletResponse mockResponse;

    @Mock
    private BufferedReader mockBufferedReader;

    @InjectMocks
    private static ProjectServlet underTest;

    @BeforeEach
    void setUp() throws IOException {
        when(mockResponse.getWriter()).thenReturn(new PrintWriter(Writer.nullWriter()));
        underTest = new ProjectServlet(projectService);
    }

    @Test
    public void shouldGetProjects() throws Exception {
        // given
        when(mockRequest.getPathInfo()).thenReturn("/");

        // when
        underTest.doGet(mockRequest, mockResponse);

        // then
        verify(projectService, times(1)).getProjects();
        verify(mockResponse).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    public void shouldGetProjectById() throws Exception {
        // given
        when(mockRequest.getPathInfo()).thenReturn("projects/101/");

        // when
        underTest.doGet(mockRequest, mockResponse);

        // then
        verify(projectService, times(1)).getProjectById(anyLong());
        verify(mockResponse).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    public void shouldSetBadRequestStatusWhenGetException() throws Exception {
        // given
        when(mockRequest.getPathInfo()).thenReturn("projects/101/d");

        // when
        underTest.doGet(mockRequest, mockResponse);

        // then
        verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void shouldThrowExceptionWhenGetProjectNotFound() throws Exception {
        // given
        when(mockRequest.getPathInfo()).thenReturn("projects/101");
        Long projectId = 101L;
        doThrow(new ProjectNotFoundException(String.format("Project with id %s not found", projectId)))
                .when(projectService).getProjectById(projectId);

        // when
        underTest.doGet(mockRequest, mockResponse);

        // then
        verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void shouldAddProject() throws Exception {
        // given
        String title = "Java";
        String abbreviation = "AAN";
        String description = "description 1";
        when(mockRequest.getReader()).thenReturn(mockBufferedReader);

        String projectJson = """
                    {
                      "title" : "%s",
                      "abbreviation" : "%s",
                      "description" : "%s"
                    }
                """.formatted(title, abbreviation, description);
        when(mockBufferedReader.readLine()).thenReturn(projectJson).thenReturn(null);

        // when
        underTest.doPost(mockRequest, mockResponse);

        // then
        ArgumentCaptor<ProjectTasksDtoRequest> argumentCaptor = ArgumentCaptor.forClass(ProjectTasksDtoRequest.class);
        verify(projectService, times(1)).addProject(argumentCaptor.capture());

        ProjectTasksDtoRequest argumentCaptorValue = argumentCaptor.getValue();
        assertEquals(title, argumentCaptorValue.getTitle());
        assertEquals(abbreviation, argumentCaptorValue.getAbbreviation());
        assertEquals(description, argumentCaptorValue.getDescription());
        verify(mockResponse).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    public void shouldAddProjectWithTask() throws Exception {
        // given
        String title = "Java";
        String abbreviation = "AAN";
        String description = "description 1";
        String tasks = "101, 102";
        when(mockRequest.getReader()).thenReturn(mockBufferedReader);

        String projectJson = """
                    {
                      "title" : "%s",
                      "abbreviation" : "%s",
                      "description" : "%s",
                      "tasks" : "%s"
                    }
                """.formatted(title, abbreviation, description, tasks);
        when(mockBufferedReader.readLine()).thenReturn(projectJson).thenReturn(null);

        // when
        underTest.doPost(mockRequest, mockResponse);

        // then
        ArgumentCaptor<ProjectTasksDtoRequest> argumentCaptor = ArgumentCaptor.forClass(ProjectTasksDtoRequest.class);
        verify(projectService, times(1)).addProject(argumentCaptor.capture());

        ProjectTasksDtoRequest argumentCaptorValue = argumentCaptor.getValue();
        assertEquals(title, argumentCaptorValue.getTitle());
        assertEquals(abbreviation, argumentCaptorValue.getAbbreviation());
        assertEquals(description, argumentCaptorValue.getDescription());
        assertEquals(tasks, argumentCaptorValue.getTasks());
        verify(mockResponse).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    public void shouldSetBadRequestStatusWhenPostException() throws Exception {
        // given
        when(mockRequest.getReader()).thenReturn(mockBufferedReader);
        doThrow(new RuntimeException("Runtime exception"))
                .when(projectService).addProject(any());

        // when
        underTest.doPost(mockRequest, mockResponse);

        // then
        verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void shouldUpdateProject() throws Exception {
        // given
        Long id = 101L;
        String title = "Spring";
        String abbreviation = "Spring";
        String description = "description 5";
        when(mockRequest.getReader()).thenReturn(mockBufferedReader);

        String projectJson = """
                    {
                      "id" : "%d",
                      "title" : "%s",
                      "abbreviation" : "%s",
                      "description" : "%s"
                    }
                """.formatted(id, title, abbreviation, description);
        when(mockBufferedReader.readLine()).thenReturn(projectJson).thenReturn(null);

        // when
        underTest.doPut(mockRequest, mockResponse);

        // then
        ArgumentCaptor<ProjectTasksDtoRequest> argumentCaptor = ArgumentCaptor.forClass(ProjectTasksDtoRequest.class);
        verify(projectService, times(1)).updateProject(argumentCaptor.capture());

        ProjectTasksDtoRequest argumentCaptorValue = argumentCaptor.getValue();
        assertEquals(id, argumentCaptorValue.getId());
        assertEquals(title, argumentCaptorValue.getTitle());
        assertEquals(abbreviation, argumentCaptorValue.getAbbreviation());
        assertEquals(description, argumentCaptorValue.getDescription());
        verify(mockResponse).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    public void shouldUpdateProjectWithTasks() throws Exception {
        // given
        Long id = 102L;
        String title = "Spring";
        String abbreviation = "Spring";
        String description = "description 5";
        String tasks = "101";
        when(mockRequest.getReader()).thenReturn(mockBufferedReader);

        String projectJson = """
                    {
                      "id" : "%d",
                      "title" : "%s",
                      "abbreviation" : "%s",
                      "description" : "%s",
                      "tasks" : "%s"
                    }
                """.formatted(id, title, abbreviation, description, tasks);
        when(mockBufferedReader.readLine()).thenReturn(projectJson).thenReturn(null);

        // when
        underTest.doPut(mockRequest, mockResponse);

        // then
        ArgumentCaptor<ProjectTasksDtoRequest> argumentCaptor = ArgumentCaptor.forClass(ProjectTasksDtoRequest.class);
        verify(projectService, times(1)).updateProject(argumentCaptor.capture());

        ProjectTasksDtoRequest argumentCaptorValue = argumentCaptor.getValue();
        assertEquals(id, argumentCaptorValue.getId());
        assertEquals(title, argumentCaptorValue.getTitle());
        assertEquals(abbreviation, argumentCaptorValue.getAbbreviation());
        assertEquals(description, argumentCaptorValue.getDescription());
        assertEquals(tasks, argumentCaptorValue.getTasks());
        verify(mockResponse).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    public void shouldSetBadRequestStatusWhenPutException() throws Exception {
        // given
        when(mockRequest.getReader()).thenReturn(mockBufferedReader);
        doThrow(new RuntimeException("Runtime exception"))
                .when(projectService).updateProject(any());

        // when
        underTest.doPut(mockRequest, mockResponse);

        // then
        verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void shouldDeleteProjectById() throws Exception {
        // given
        when(mockRequest.getPathInfo()).thenReturn("projects/101");

        // when
        underTest.doDelete(mockRequest, mockResponse);

        // then
        verify(projectService, times(1)).deleteProject(anyLong());
        verify(mockResponse).setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    @Test
    public void shouldSetBadRequestStatusWhenProjectNotFoundExceptionDeleteMethod() throws Exception {
        // given
        when(mockRequest.getPathInfo()).thenReturn("projects/101");
        Long projectId = 101L;
        doThrow(new ProjectNotFoundException(String.format("Project with id %s not found", projectId )))
                .when(projectService).deleteProject(projectId);

        // when
        underTest.doDelete(mockRequest, mockResponse);

        // then
        verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void shouldSetBadRequestStatusWhenDeleteException() throws Exception {
        // given
        when(mockRequest.getPathInfo()).thenReturn("projects/101/d");

        // when
        underTest.doDelete(mockRequest, mockResponse);

        // then
        verify(projectService, times(0)).getProjectById(anyLong());
        verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }
}
