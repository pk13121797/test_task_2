package by.pavvel.dao.impl;

import by.pavvel.dao.TaskDao;
import by.pavvel.config.ConnectionManager;
import by.pavvel.config.ConnectionManagerImpl;
import by.pavvel.dto.ProjectDto;
import by.pavvel.dto.TaskDto;
import by.pavvel.dto.TaskProjectDto;
import by.pavvel.model.Status;
import by.pavvel.model.Task;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TaskDaoImpl implements TaskDao {

    private static final ConnectionManager connectionManager = ConnectionManagerImpl.getInstance();

    private static final String SELECT_ALL_TASKS = """
            SELECT t.task_id, t.title, t.hours, t.start_date, t.end_date, t.status,
                   p.project_id, p.title
            FROM task t left join project p on p.project_id = t.project_id;
            """;

    private static final String SELECT_TASK_BY_ID = """
            SELECT t.task_id, t.title, t.hours, t.start_date, t.end_date, t.status,
                   p.project_id, p.title
            FROM task t left join project p on p.project_id = t.project_id WHERE t.task_id = ?;
            """;

    private static final String SELECT_TASK_DTO_BY_ID = """
            SELECT task_id, title, status FROM task WHERE task_id = ?;
            """;

    private static final String INSERT_TASK = """
            INSERT INTO task(title, hours, start_date, end_date, status, project_id) VALUES (?, ?, ?, ?, ?, ?);
            """;

    private static final String INSERT_TASK_EMPLOYEE = """
            INSERT INTO task_employee(task_id, employee_id) VALUES (?, ?);
            """;

    private static final String UPDATE_TASK = """
            UPDATE task SET title = ?, hours = ?, start_date = ?, end_date = ?, status = ?, project_id = ? WHERE task_id = ?;
            """;

    private static final String UPDATE_TASK_PROJECT = """
            UPDATE task SET project_id = ? WHERE task_id = ?;
            """;

    private static final String UPDATE_TASK_EMPLOYEE = """
            UPDATE task_employee SET employee_id = ? WHERE task_id = ?;
            """;

    private static final String DELETE_TASK_BY_ID = """
            DELETE FROM task WHERE task_id = ?;
            """;

    private static final String DELETE_ALL_TASKS = """
            DELETE FROM task;
            """;

    private static final String SELECT_TASK_DTO_BY_PROJECT_ID = """
            SELECT t.task_id, t.title, t.status
            FROM task t left join project p on p.project_id = t.project_id
            WHERE p.project_id = ?;
            """;

    private static TaskDao instance;

    public static synchronized TaskDao getInstance() {
        if (instance == null) {
            instance = new TaskDaoImpl();
        }
        return instance;
    }

    @Override
    public List<TaskDto> findTasks() {
        List<TaskDto> tasks = new ArrayList<>();
        try(Connection connection = connectionManager.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(SELECT_ALL_TASKS);
            while (resultSet.next()) {
                TaskDto task = new TaskDto(
                        resultSet.getLong(1),
                        resultSet.getString(2),
                        resultSet.getInt(3),
                        LocalDate.parse(resultSet.getString(4)),
                        LocalDate.parse(resultSet.getString(5)),
                        Status.valueOf(resultSet.getString(6)),
                        new ProjectDto(
                                resultSet.getLong(7),
                                resultSet.getString(8)
                        )
                );
                tasks.add(task);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return tasks;
    }

    @Override
    public Optional<TaskDto> findTaskById(Long taskId) {
        TaskDto taskDto = null;
        try (Connection connection = connectionManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_TASK_BY_ID);
            preparedStatement.setLong(1, taskId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                taskDto = new TaskDto(
                        resultSet.getLong(1),
                        resultSet.getString(2),
                        resultSet.getInt(3),
                        LocalDate.parse(resultSet.getString(4)),
                        LocalDate.parse(resultSet.getString(5)),
                        Status.valueOf(resultSet.getString(6)),
                        new ProjectDto(
                                resultSet.getLong(7),
                                resultSet.getString(8)
                        )
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.ofNullable(taskDto);
    }

    public Optional<TaskProjectDto> findTaskDto(Long taskId) {
        TaskProjectDto taskProjectDto = null;
        try (Connection connection = connectionManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_TASK_DTO_BY_ID);
            preparedStatement.setLong(1, taskId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                taskProjectDto = new TaskProjectDto(
                        resultSet.getLong("task_id"),
                        resultSet.getString("title"),
                        Status.valueOf(resultSet.getString("status"))
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.ofNullable(taskProjectDto);
    }

    @Override
    public List<TaskProjectDto> findTasksByProjectId(Long projectId) {
        List<TaskProjectDto> taskProjectDtosList = new ArrayList<>();
        TaskProjectDto taskProjectDto;
        try (Connection connection = connectionManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_TASK_DTO_BY_PROJECT_ID);
            preparedStatement.setLong(1, projectId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                taskProjectDto = new TaskProjectDto(
                        resultSet.getLong(1),
                        resultSet.getString(2),
                        Status.valueOf(resultSet.getString(3))
                );
                taskProjectDtosList.add(taskProjectDto);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return taskProjectDtosList;
    }

    @Override
    public Task saveTask(Task task, Long projectId) {
        Long taskId = null;
        try (Connection connection = connectionManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_TASK, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, task.getTitle());
            preparedStatement.setInt(2, task.getHours());
            preparedStatement.setDate(3, Date.valueOf(task.getStartDate()));
            preparedStatement.setDate(4, Date.valueOf(task.getEndDate()));
            preparedStatement.setString(5, task.getStatus().name());
            preparedStatement.setLong(6, projectId);
            preparedStatement.executeUpdate();

            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet != null && resultSet.next()) {
                taskId = resultSet.getLong(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        task.setId(taskId);
        return task;
    }

    public int saveTaskEmployee(Long taskId, Long employeeId) {
        int i;
        try (Connection connection = connectionManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_TASK_EMPLOYEE);
            preparedStatement.setLong(1, taskId);
            preparedStatement.setLong(2, employeeId);
            i = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return i;
    }

    @Override
    public Task updateTask(Task task, Long employeeId) {
        try (Connection connection = connectionManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_TASK);
            preparedStatement.setString(1, task.getTitle());
            preparedStatement.setInt(2, task.getHours());
            preparedStatement.setDate(3, Date.valueOf(task.getStartDate()));
            preparedStatement.setDate(4, Date.valueOf(task.getEndDate()));
            preparedStatement.setString(5, task.getStatus().name());
            preparedStatement.setLong(6, employeeId);
            preparedStatement.setLong(7, task.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return task;
    }

    public int updateTaskProject(Long taskId, Long projectId) {
        int i;
        try (Connection connection = connectionManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_TASK_PROJECT);
            preparedStatement.setLong(1, projectId);
            preparedStatement.setLong(2, taskId);
            i = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return i;
    }

    public int updateTaskEmployee(Long taskId, Long employeeId) {
        int i;
        try (Connection connection = connectionManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_TASK_EMPLOYEE);
            preparedStatement.setLong(1, employeeId);
            preparedStatement.setLong(2, taskId);
            i = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return i;
    }

    @Override
    public void deleteTask(Long taskId) {
        try (Connection connection = connectionManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(DELETE_TASK_BY_ID);
            preparedStatement.setLong(1, taskId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteAll() {
        try (Connection connection = connectionManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(DELETE_ALL_TASKS);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
