package by.pavvel.service;

import by.pavvel.dao.TaskDao;
import by.pavvel.dto.ProjectDto;
import by.pavvel.dto.TaskDto;
import by.pavvel.dto.TaskProjectDto;
import by.pavvel.dto.request.TaskProjectEmployeesDtoRequest;
import by.pavvel.exception.TaskNotFoundException;
import by.pavvel.model.Status;
import by.pavvel.model.Task;
import by.pavvel.service.impl.TaskServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

class TaskServiceTest {

    @Mock
    private TaskDao taskDao;

    @Captor
    private ArgumentCaptor<Task> taskArgumentCaptor;

    private AutoCloseable autoCloseable;

    private TaskService underTest;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTest = new TaskServiceImpl(taskDao);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void shouldGetTasks() {
        // given
        Long taskId = 1L;
        TaskDto taskDto = new TaskDto(
                taskId,
                "write code",
                4,
                LocalDate.parse("2021-04-13"),
                LocalDate.parse("2025-04-15"),
                Status.POSTPONED,
                new ProjectDto(
                        null, null
                )
        );
        List<TaskDto> taskList = List.of(taskDto);

        when(taskDao.findTasks()).thenReturn(taskList);

        // when
        List<TaskDto> tasks = underTest.getTasks();

        // then
        assertThat(tasks).isNotNull();
        tasks.forEach(t -> {
            assertThat(t.getId()).isEqualTo(taskDto.getId());
            assertThat(t.getTitle()).isEqualTo(taskDto.getTitle());
            assertThat(t.getHours()).isEqualTo(taskDto.getHours());
            assertThat(t.getStartDate()).isEqualTo(taskDto.getStartDate());
            assertThat(t.getEndDate()).isEqualTo(taskDto.getEndDate());
            assertThat(t.getStatus()).isEqualTo(taskDto.getStatus());
            assertThat(t.getProject()).isEqualTo(taskDto.getProject());
        });
    }

    @Test
    void shouldGetTaskById() {
        // given
        Long taskId = 1L;
        TaskDto taskDto = new TaskDto(
                taskId,
                "write code",
                4,
                LocalDate.parse("2021-04-13"),
                LocalDate.parse("2025-04-15"),
                Status.POSTPONED,
                new ProjectDto(
                        null, null
                )
        );
        when(taskDao.findTaskById(taskId)).thenReturn(Optional.of(taskDto));

        // when
        TaskDto taskById = underTest.getTaskById(taskId);

        // then
        assertThat(taskById).isNotNull().isEqualTo(taskDto);
    }

    @Test
    void shouldThrowExceptionWhenTaskNotExists() {
        // given
        Long taskId = 1L;
        when(taskDao.findTaskById(taskId)).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.getTaskById(taskId))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining(String.format("Task with id %s not found", taskId));
    }

    @Test
    void shouldGetTaskByProject() {
        // given
        Long taskId = 1L;
        Long projectId = 1L;
        TaskProjectDto taskProjectDto = new TaskProjectDto(taskId, "write code", Status.POSTPONED);
        when(taskDao.findTasksByProjectId(projectId)).thenReturn(List.of(taskProjectDto));

        // when
        List<TaskProjectDto> tasksByProject = underTest.getTasksByProject(projectId);

        // then
        assertThat(tasksByProject).isNotNull();
        assertThat(taskProjectDto).isIn(tasksByProject);
    }

    @Test
    void shouldAddTask() {
        // given
        Long taskId = 1L;
        String title = "write code";
        int hours = 4;
        LocalDate startDate = LocalDate.parse("2021-04-13");
        LocalDate endDate = LocalDate.parse("2025-04-15");
        Status status = Status.POSTPONED;
        Task task = new Task(title, hours, startDate, endDate, status);

        String employees = "1, 2";
        Long projectId = 1L;
        when(taskDao.saveTask(task, projectId)).thenReturn(task);

        // when
        TaskProjectEmployeesDtoRequest taskProjectEmployeesDtoRequest =
                new TaskProjectEmployeesDtoRequest(taskId, title, hours, startDate, endDate, status, projectId, employees);
        underTest.addTask(taskProjectEmployeesDtoRequest);

        // then
        then(taskDao).should().saveTask(taskArgumentCaptor.capture(), anyLong());
        Task taskArgumentCaptorValue = taskArgumentCaptor.getValue();

        assertThat(taskArgumentCaptorValue.getTitle()).isEqualTo(taskProjectEmployeesDtoRequest.getTitle());
        assertThat(taskArgumentCaptorValue.getHours()).isEqualTo(taskProjectEmployeesDtoRequest.getHours());
        assertThat(taskArgumentCaptorValue.getStartDate()).isEqualTo(taskProjectEmployeesDtoRequest.getStartDate());
        assertThat(taskArgumentCaptorValue.getEndDate()).isEqualTo(taskProjectEmployeesDtoRequest.getEndDate());
        assertThat(taskArgumentCaptorValue.getStatus()).isEqualTo(taskProjectEmployeesDtoRequest.getStatus());
    }

    @Test
    void shouldUpdateTask() {
        // given
        Long taskId = 1L;
        String title = "write code";
        int hours = 4;
        LocalDate startDate = LocalDate.parse("2021-04-13");
        LocalDate endDate = LocalDate.parse("2025-04-15");
        Status status = Status.POSTPONED;
        Task task = new Task(title, hours, startDate, endDate, status);
        task.setId(taskId);

        TaskDto taskDto = new TaskDto(taskId, title, hours, startDate, endDate, status,
                new ProjectDto(null, null)
        );

        String employees = "1, 2";
        Long projectId = 1L;
        when(taskDao.findTaskById(taskId)).thenReturn(Optional.of(taskDto));
        when(taskDao.updateTask(task, projectId)).thenReturn(task);

        // when
        TaskProjectEmployeesDtoRequest taskProjectEmployeesDtoRequest =
                new TaskProjectEmployeesDtoRequest(taskId, title, hours, startDate, endDate, status, projectId, employees);
        underTest.updateTask(taskProjectEmployeesDtoRequest);

        // then
        then(taskDao).should().updateTask(taskArgumentCaptor.capture(), anyLong());
        Task taskArgumentCaptorValue = taskArgumentCaptor.getValue();

        assertThat(taskArgumentCaptorValue.getTitle()).isEqualTo(taskProjectEmployeesDtoRequest.getTitle());
        assertThat(taskArgumentCaptorValue.getHours()).isEqualTo(taskProjectEmployeesDtoRequest.getHours());
        assertThat(taskArgumentCaptorValue.getStartDate()).isEqualTo(taskProjectEmployeesDtoRequest.getStartDate());
        assertThat(taskArgumentCaptorValue.getEndDate()).isEqualTo(taskProjectEmployeesDtoRequest.getEndDate());
        assertThat(taskArgumentCaptorValue.getStatus()).isEqualTo(taskProjectEmployeesDtoRequest.getStatus());
    }

    @Test
    void shouldNotUpdateTaskByIdWhenTaskNotExists() {
        // given
        Long taskId = 1L;
        when(taskDao.findTaskById(taskId)).thenReturn(Optional.empty());
        TaskProjectEmployeesDtoRequest taskProjectEmployeesDtoRequest
                = new TaskProjectEmployeesDtoRequest(
                taskId, "write code",
                4,
                LocalDate.parse("2021-04-13"),
                LocalDate.parse("2025-04-15"),
                Status.POSTPONED,
                null,
                null
        );

        // when
        // then
        assertThatThrownBy(() -> underTest.updateTask(taskProjectEmployeesDtoRequest))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining(String.format("Task with id %s not found", taskId));
        then(taskDao).should(never()).updateTask(any(), anyLong());
    }

    @Test
    void shouldUpdateTaskProject() {
        // given
        Long taskId = 1L;
        String title = "write code";
        Status status = Status.POSTPONED;
        Long projectId = 1L;

        TaskProjectDto taskProjectDto = new TaskProjectDto(taskId, title, status);
        when(taskDao.updateTaskProject(taskId, projectId)).thenReturn(1);
        when(taskDao.findTaskDto(taskId)).thenReturn(Optional.of(taskProjectDto));

        // when
        TaskProjectDto updatedTaskProjectDto = underTest.updateTaskProject(taskId, projectId);

        // then
        assertThat(updatedTaskProjectDto).isEqualTo(taskProjectDto);
    }

    @Test
    void shouldNotUpdateTaskProjectWhenTaskNotExists() {
        // given
        Long taskId = 1L;
        Long projectId = 1L;

        when(taskDao.updateTaskProject(taskId, projectId)).thenReturn(1);
        when(taskDao.findTaskDto(taskId)).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.updateTaskProject(taskId, projectId))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining(String.format("Task with id %s not found", taskId));
    }

    @Test
    void shouldDeleteTask() {
        // given
        Long taskId = 1L;
        TaskDto taskDto = new TaskDto(
                taskId,
                "write code",
                4,
                LocalDate.parse("2021-04-13"),
                LocalDate.parse("2025-04-15"),
                Status.POSTPONED,
                new ProjectDto(
                        null, null
                )
        );
        when(taskDao.findTaskById(taskId)).thenReturn(Optional.of(taskDto));
        doNothing().when(taskDao).deleteTask(taskId);

        // when
        underTest.deleteTask(taskId);

        // then
        verify(taskDao, times(1)).deleteTask(taskId);
    }

    @Test
    void shouldNotDeleteTaskByIdWhenTaskNotExists() {
        // given
        Long taskId = 1L;
        when(taskDao.findTaskById(taskId)).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.deleteTask(taskId))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining(String.format("Task with id %s not found", taskId));
        then(taskDao).should(never()).deleteTask(any());
    }
}