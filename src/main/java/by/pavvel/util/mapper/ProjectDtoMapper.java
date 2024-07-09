package by.pavvel.util.mapper;

import by.pavvel.dto.ProjectTasksDto;
import by.pavvel.dto.TaskProjectDto;
import by.pavvel.model.Project;

import java.util.List;
import java.util.function.BiFunction;

public class ProjectDtoMapper implements BiFunction<Project, List<TaskProjectDto>, ProjectTasksDto> {

    @Override
    public ProjectTasksDto apply(Project project, List<TaskProjectDto> taskProjectDto) {
        return new ProjectTasksDto(
                project.getId(),
                project.getTitle(),
                project.getAbbreviation(),
                project.getDescription(),
                taskProjectDto.stream().map(task -> new TaskProjectDto(
                        task.getId(),
                        task.getTitle(),
                        task.getStatus()
                )).toList()
        );
    }
}
