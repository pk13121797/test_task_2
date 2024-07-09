package by.pavvel.dao;

import by.pavvel.dto.TaskDto;
import by.pavvel.dto.TaskProjectDto;
import by.pavvel.model.Task;

import java.util.List;
import java.util.Optional;

public interface TaskDao {

    List<TaskDto> findTasks();

    Optional<TaskDto> findTaskById(Long taskId);

    Optional<TaskProjectDto> findTaskDto(Long taskId);

    List<TaskProjectDto> findTasksByProjectId(Long projectId);

    Task saveTask(Task task, Long projectId);

    int saveTaskEmployee(Long taskId, Long employeeId);

    Task updateTask(Task task, Long projectId);

    int updateTaskProject(Long taskId, Long projectId);

    int updateTaskEmployee(Long taskId, Long employeeId);

    void deleteTask(Long taskId);

    void deleteAll();
}
