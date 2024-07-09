package by.pavvel.dao;

import by.pavvel.config.AbstractTestcontainers;
import by.pavvel.dao.impl.EmployeeDaoImpl;
import by.pavvel.dao.impl.ProjectDaoImpl;
import by.pavvel.dao.impl.TaskDaoImpl;
import by.pavvel.dto.TaskDto;
import by.pavvel.dto.TaskProjectDto;
import by.pavvel.model.Employee;
import by.pavvel.model.Project;
import by.pavvel.model.Status;
import by.pavvel.model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class TaskDaoTest extends AbstractTestcontainers {

    private TaskDao underTest;

    private final ProjectDao projectDao = new ProjectDaoImpl();

    private final EmployeeDao employeeDao = new EmployeeDaoImpl();

    @BeforeEach
    void setUp() {
        projectDao.saveProject(new Project("Java", "AAN", "description 1"));
        employeeDao.saveEmployee(new Employee("Petr","Smirnov","Petrovich", "Middle"));
        underTest = new TaskDaoImpl();
    }

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void shouldFindTasks() {
        // given
        Long projectId = 1L;
        Task task = new Task(
                "write code",
                4,
                LocalDate.parse("2021-04-13"),
                LocalDate.parse("2025-04-15"),
                Status.POSTPONED
        );
        underTest.saveTask(task, projectId);

        // when
        List<TaskDto> tasks = underTest.findTasks();

        // then
        assertThat(tasks).isNotNull();
        assertThat(tasks.size()).isEqualTo(1);
    }

    @Test
    void shouldFindTaskById() {
        // given
        Long projectId = 1L;
        Task task = new Task(
                "write code",
                4,
                LocalDate.parse("2021-04-13"),
                LocalDate.parse("2025-04-15"),
                Status.POSTPONED
        );
        underTest.saveTask(task, projectId);

        // when
        Optional<TaskDto> taskById = underTest.findTaskById(task.getId());

        // then
        assertThat(taskById)
                .isPresent()
                .hasValueSatisfying(t -> {
                    assertThat(t.getId()).isEqualTo(task.getId());
                    assertThat(t.getTitle()).isEqualTo(task.getTitle());
                    assertThat(t.getHours()).isEqualTo(task.getHours());
                    assertThat(t.getStartDate()).isEqualTo(task.getStartDate());
                    assertThat(t.getEndDate()).isEqualTo(task.getEndDate());
                    assertThat(t.getStatus()).isEqualTo(task.getStatus());
                });
    }

    @Test
    void shouldFindTaskDtoById() {
        // given
        Long projectId = 1L;
        Task task = new Task(
                "write code",
                4,
                LocalDate.parse("2021-04-13"),
                LocalDate.parse("2025-04-15"),
                Status.POSTPONED
        );
        underTest.saveTask(task, projectId);

        // when
        Optional<TaskProjectDto> taskDto = underTest.findTaskDto(task.getId());

        // then
        assertThat(taskDto)
                .isPresent()
                .hasValueSatisfying(t -> {
                    assertThat(t.getId()).isEqualTo(task.getId());
                    assertThat(t.getTitle()).isEqualTo(task.getTitle());
                    assertThat(t.getStatus()).isEqualTo(task.getStatus());
                });
    }

    @Test
    void shouldFindTaskByProjectId() {
        // given
        Long projectId = 1L;
        Task task = new Task(
                "write code",
                4,
                LocalDate.parse("2021-04-13"),
                LocalDate.parse("2025-04-15"),
                Status.POSTPONED
        );

        // when
        List<TaskProjectDto> tasksByProjectId = underTest.findTasksByProjectId(projectId);

        // then
        assertThat(tasksByProjectId).isNotNull();
        tasksByProjectId.forEach(t -> {
            assertThat(t.getId()).isEqualTo(task.getId());
            assertThat(t.getTitle()).isEqualTo(task.getTitle());
            assertThat(t.getStatus()).isEqualTo(task.getStatus());
        });
    }

    @Test
    void shouldSaveTask() {
        // given
        Long projectId = 1L;
        Task task = new Task(
                "write code",
                4,
                LocalDate.parse("2021-04-13"),
                LocalDate.parse("2025-04-15"),
                Status.POSTPONED
        );

        // when
        Task savedTask = underTest.saveTask(task, projectId);

        // then
        assertThat(savedTask)
                .isNotNull()
                .isEqualTo(task);
    }

    @Test
    void shouldSaveTaskEmployee() {
        // given
        Long employeeId = 1L;
        Long projectId = 1L;
        Task task = new Task(
                "write code",
                4,
                LocalDate.parse("2021-04-13"),
                LocalDate.parse("2025-04-15"),
                Status.POSTPONED
        );
        underTest.saveTask(task, projectId);

        // when
        int i = underTest.saveTaskEmployee(task.getId(), employeeId);

        // then
        assertThat(i).isEqualTo(1);
    }

    @Test
    void shouldUpdateTask() {
        // given
        Long projectId = 1L;
        Task task = new Task(
                "write code",
                4,
                LocalDate.parse("2021-04-13"),
                LocalDate.parse("2025-04-15"),
                Status.POSTPONED
        );
        underTest.saveTask(task, projectId);

        Long projectIdToUpdate = 1L;
        Task taskToUpdate = new Task(
                task.getId(),
                "read article",
                5,
                LocalDate.parse("2020-04-30"),
                LocalDate.parse("2025-04-23"),
                Status.NOT_STARTED
        );

        // when
        Task updatedTask = underTest.updateTask(taskToUpdate, projectIdToUpdate);

        // then
        assertThat(updatedTask).isEqualTo(taskToUpdate);
    }

    @Test
    void shouldUpdateTaskProject() {
        // given
        Long projectId = 1L;
        Task task = new Task(
                "write code",
                4,
                LocalDate.parse("2021-04-13"),
                LocalDate.parse("2025-04-15"),
                Status.POSTPONED
        );
        underTest.saveTask(task, projectId);
        Long projectIdToUpdate = 1L;

        // when
        int i = underTest.updateTaskProject(task.getId(), projectIdToUpdate);

        // then
        Optional<TaskDto> taskById = underTest.findTaskById(task.getId());
        assertThat(taskById)
                .isPresent()
                .hasValueSatisfying(t -> assertThat(t.getProject().getId()).isEqualTo(projectIdToUpdate));
        assertThat(i).isEqualTo(1);
    }

    @Test
    void shouldUpdateTaskEmployee() {
        // given
        Long projectId = 1L;
        Task task = new Task(
                "write code",
                4,
                LocalDate.parse("2021-04-13"),
                LocalDate.parse("2025-04-15"),
                Status.POSTPONED
        );
        underTest.saveTask(task, projectId);
        Long employeeIdToUpdate = 1L;

        // when
        int i = underTest.updateTaskEmployee(task.getId(), employeeIdToUpdate);

        // then
        assertThat(i).isEqualTo(0);
    }

    @Test
    void shouldDeleteTaskById() {
        // given
        Long projectId = 1L;
        Task task = new Task("write code", 4, LocalDate.parse("2021-04-13"), LocalDate.parse("2025-04-15"), Status.POSTPONED);
        underTest.saveTask(task, projectId);

        // when
        underTest.deleteTask(task.getId());

        // then
        Optional<TaskDto> taskById = underTest.findTaskById(task.getId());
        assertThat(taskById).isNotPresent();
    }

    @Test
    void shouldDeleteAllTasks() {
        // given
        Long projectId = 1L;
        Task task = new Task("write code", 4, LocalDate.parse("2021-04-13"), LocalDate.parse("2025-04-15"), Status.POSTPONED);
        underTest.saveTask(task, projectId);

        // when
        underTest.deleteAll();

        // then
        List<TaskDto> tasks = underTest.findTasks();
        assertThat(tasks.size()).isEqualTo(0);
    }
}
