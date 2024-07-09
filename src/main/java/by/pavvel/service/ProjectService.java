package by.pavvel.service;

import by.pavvel.dto.ProjectTasksDto;
import by.pavvel.dto.request.ProjectTasksDtoRequest;

import java.util.List;

public interface ProjectService {

    List<ProjectTasksDto> getProjects();

    ProjectTasksDto getProjectById(Long projectId);

    ProjectTasksDto addProject(ProjectTasksDtoRequest projectTasksRequest);

    ProjectTasksDto updateProject(ProjectTasksDtoRequest projectTasksRequest);

    void deleteProject(Long projectId);
}
