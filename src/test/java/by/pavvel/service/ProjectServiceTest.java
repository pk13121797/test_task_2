package by.pavvel.service;

import by.pavvel.dao.ProjectDao;
import by.pavvel.dto.ProjectTasksDto;
import by.pavvel.dto.TaskProjectDto;
import by.pavvel.dto.request.ProjectTasksDtoRequest;
import by.pavvel.exception.ProjectNotFoundException;
import by.pavvel.model.Project;
import by.pavvel.model.Status;
import by.pavvel.service.impl.ProjectServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static by.pavvel.util.converter.RequestStringToLongIdsConverter.convertStringToLongIds;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

class ProjectServiceTest {

    @Mock
    private ProjectDao projectDao;

    @Mock
    private TaskService taskService;

    @Captor
    private ArgumentCaptor<Project> projectArgumentCaptor;

    private AutoCloseable autoCloseable;

    private ProjectService underTest;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTest = new ProjectServiceImpl(taskService, projectDao);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void shouldGetProjects() {
        // given
        Long projectId = 1L;
        Project project = new Project("Java", "AAN", "description 1");
        project.setId(projectId);
        List<Project> projectList = List.of(project);

        TaskProjectDto taskDto1 = new TaskProjectDto(1L,"write code", Status.POSTPONED);
        TaskProjectDto taskDto2 = new TaskProjectDto(2L,"read article", Status.NOT_STARTED);
        List<TaskProjectDto> taskList = List.of(taskDto1, taskDto2);

        when(projectDao.findProjects()).thenReturn(projectList);
        when(taskService.getTasksByProject(projectId)).thenReturn(taskList);

        // when
        List<ProjectTasksDto> projects = underTest.getProjects();

        // then
        assertThat(projects).isNotNull();
        projects.forEach(p -> {
            assertThat(p.getId()).isEqualTo(project.getId());
            assertThat(p.getTitle()).isEqualTo(project.getTitle());
            assertThat(p.getAbbreviation()).isEqualTo(project.getAbbreviation());
            assertThat(p.getDescription()).isEqualTo(project.getDescription());
        });
    }

    @Test
    void shouldGetProjectById() {
        // given
        Long projectId = 1L;
        Project project = new Project("Java", "AAN", "description 1");
        project.setId(projectId);
        TaskProjectDto taskDto1 = new TaskProjectDto(1L,"write code", Status.POSTPONED);
        TaskProjectDto taskDto2 = new TaskProjectDto(2L,"read article", Status.NOT_STARTED);
        List<TaskProjectDto> taskList = List.of(taskDto1, taskDto2);

        when(projectDao.findProjectById(projectId)).thenReturn(Optional.of(project));
        when(taskService.getTasksByProject(projectId)).thenReturn(taskList);

        // when
        ProjectTasksDto projectById = underTest.getProjectById(projectId);

        // then
        assertThat(projectById).isNotNull();
        assertThat(projectById.getId()).isEqualTo(project.getId());
        assertThat(projectById.getTitle()).isEqualTo(project.getTitle());
        assertThat(projectById.getAbbreviation()).isEqualTo(project.getAbbreviation());
        assertThat(projectById.getDescription()).isEqualTo(project.getDescription());
    }

    @Test
    void shouldThrowExceptionWhenProjectNotExists() {
        // given
        Long projectId = 1L;
        when(projectDao.findProjectById(projectId)).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.getProjectById(projectId))
                .isInstanceOf(ProjectNotFoundException.class)
                .hasMessageContaining(String.format("Project with id %s doesn't exists", projectId));
    }

    @Test
    void shouldAddProject() {
        // given
        Long projectId = 1L;
        String title = "Java";
        String abbreviation = "AAN";
        String description = "description 1";
        Project project = new Project(title, abbreviation, description);

        String tasks = "1, 2";
        List<Long> taskIds = convertStringToLongIds(tasks);
        TaskProjectDto taskProjectDto1 = new TaskProjectDto(1L, "write code", Status.POSTPONED);
        TaskProjectDto taskProjectDto2 = new TaskProjectDto(2L, "write code", Status.POSTPONED);

        when(projectDao.saveProject(project)).thenReturn(project);

        taskIds.forEach(t -> when(taskService.updateTaskProject(t, projectId)).thenReturn(taskProjectDto1, taskProjectDto2));

        List<TaskProjectDto> taskProjectDtoList = List.of(taskProjectDto1, taskProjectDto2);
        when(taskService.getTasksByProject(projectId)).thenReturn(taskProjectDtoList);

        // when
        ProjectTasksDtoRequest projectTasksRequest = new ProjectTasksDtoRequest(
                project.getId(),
                title,
                abbreviation,
                description,
                tasks
        );
        underTest.addProject(projectTasksRequest);

        // then
        then(projectDao).should().saveProject(projectArgumentCaptor.capture());
        Project projectArgumentCaptorValue = projectArgumentCaptor.getValue();

        assertThat(projectArgumentCaptorValue.getTitle()).isEqualTo(projectTasksRequest.getTitle());
        assertThat(projectArgumentCaptorValue.getAbbreviation()).isEqualTo(projectTasksRequest.getAbbreviation());
        assertThat(projectArgumentCaptorValue.getDescription()).isEqualTo(projectTasksRequest.getDescription());
    }

    @Test
    void shouldUpdateEmployee() {
        // given
        Long projectId = 1L;
        String title = "Java";
        String abbreviation = "AAN";
        String description = "description 1";
        Project project = new Project(title, abbreviation, description);
        project.setId(projectId);

        String tasks = "1, 2";
        List<Long> taskIds = convertStringToLongIds(tasks);
        TaskProjectDto taskProjectDto1 = new TaskProjectDto(1L, "write code", Status.POSTPONED);
        TaskProjectDto taskProjectDto2 = new TaskProjectDto(2L, "write code", Status.POSTPONED);

        when(projectDao.findProjectById(projectId)).thenReturn(Optional.of(project));
        when(projectDao.updateProject(project)).thenReturn(project);

        taskIds.forEach(t -> when(taskService.updateTaskProject(t, projectId)).thenReturn(taskProjectDto1, taskProjectDto2));

        List<TaskProjectDto> taskProjectDtoList = List.of(taskProjectDto1, taskProjectDto2);
        when(taskService.getTasksByProject(projectId)).thenReturn(taskProjectDtoList);

        // when
        ProjectTasksDtoRequest projectTasksRequest = new ProjectTasksDtoRequest(
                project.getId(),
                title,
                abbreviation,
                description,
                tasks
        );
        underTest.updateProject(projectTasksRequest);

        // then
        then(projectDao).should().updateProject(projectArgumentCaptor.capture());
        Project projectArgumentCaptorValue = projectArgumentCaptor.getValue();

        verify(projectDao, times(1)).updateProject(project);
        assertThat(projectArgumentCaptorValue.getTitle()).isEqualTo(projectTasksRequest.getTitle());
        assertThat(projectArgumentCaptorValue.getAbbreviation()).isEqualTo(projectTasksRequest.getAbbreviation());
        assertThat(projectArgumentCaptorValue.getDescription()).isEqualTo(projectTasksRequest.getDescription());
    }

    @Test
    void shouldNotUpdateProjectByIdWhenProjectNotExists() {
        // given
        Long projectId = 1L;
        when(projectDao.findProjectById(projectId)).thenReturn(Optional.empty());
        ProjectTasksDtoRequest projectTasksDtoRequest = new ProjectTasksDtoRequest(
                projectId,
                null, null, null, null
        );

        // when
        // then
        assertThatThrownBy(() -> underTest.updateProject(projectTasksDtoRequest))
                .isInstanceOf(ProjectNotFoundException.class)
                .hasMessageContaining(String.format("Project with id %s doesn't exists", projectId));
        then(projectDao).should(never()).updateProject(any());
    }

    @Test
    void shouldDeleteProject() {
        // given
        Long projectId = 1L;
        Project project = new Project("Java", "AAN", "description 1");
        project.setId(projectId);

        when(projectDao.findProjectById(projectId)).thenReturn(Optional.of(project));
        doNothing().when(projectDao).deleteProject(projectId);

        // when
        underTest.deleteProject(projectId);

        // then
        verify(projectDao, times(1)).deleteProject(projectId);
    }

    @Test
    void shouldNotDeleteProjectByIdWhenProjectNotExists() {
        // given
        Long projectId = 1L;
        when(projectDao.findProjectById(projectId)).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.deleteProject(projectId))
                .isInstanceOf(ProjectNotFoundException.class)
                .hasMessageContaining(String.format("Project with id %s doesn't exists", projectId));
        then(projectDao).should(never()).deleteProject(any());
    }
}