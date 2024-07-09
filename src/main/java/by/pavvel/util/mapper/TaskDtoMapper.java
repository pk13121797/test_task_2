package by.pavvel.util.mapper;

import by.pavvel.dto.TaskProjectDto;
import by.pavvel.model.Task;

import java.util.function.Function;

public class TaskDtoMapper implements Function<Task, TaskProjectDto> {

    @Override
    public TaskProjectDto apply(Task task) {
        return new TaskProjectDto(
                task.getId(),
                task.getTitle(),
                task.getStatus()
        );
    }
}
