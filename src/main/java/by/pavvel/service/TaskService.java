package by.pavvel.service;

import by.pavvel.dto.TaskDto;
import by.pavvel.dto.TaskProjectDto;
import by.pavvel.dto.request.TaskProjectEmployeesDtoRequest;

import java.util.List;

public interface TaskService {

    List<TaskDto> getTasks();

    TaskDto getTaskById(Long taskId);

    List<TaskProjectDto> getTasksByProject(Long projectId);

    TaskProjectDto addTask(TaskProjectEmployeesDtoRequest taskRequest);

    TaskProjectDto updateTask(TaskProjectEmployeesDtoRequest taskRequest);

    TaskProjectDto updateTaskProject(Long taskId, Long projectId);

    void deleteTask(Long taskId);
}
