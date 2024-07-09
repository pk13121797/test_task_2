package by.pavvel.service.impl;

import by.pavvel.dao.ProjectDao;
import by.pavvel.dao.impl.ProjectDaoImpl;
import by.pavvel.dto.ProjectTasksDto;
import by.pavvel.dto.TaskProjectDto;
import by.pavvel.dto.request.ProjectTasksDtoRequest;
import by.pavvel.exception.ProjectNotFoundException;
import by.pavvel.model.Project;
import by.pavvel.service.ProjectService;
import by.pavvel.service.TaskService;
import by.pavvel.util.mapper.ProjectDtoMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

import static by.pavvel.util.converter.RequestStringToLongIdsConverter.convertStringToLongIds;

public class ProjectServiceImpl implements ProjectService {

    private static final Logger logger = LogManager.getLogger(ProjectServiceImpl.class);

    private static ProjectService instance;

    private final TaskService taskService;

    private final ProjectDao projectDao;

    private final ProjectDtoMapper projectDtoMapper = new ProjectDtoMapper();

    public ProjectServiceImpl(TaskService taskService, ProjectDao projectDao) {
        this.taskService = taskService;
        this.projectDao = projectDao;
    }

    public static synchronized ProjectService getInstance() {
        if (instance == null) {
            instance = new ProjectServiceImpl(TaskServiceImpl.getInstance(), ProjectDaoImpl.getInstance());
        }
        return instance;
    }

    @Override
    public List<ProjectTasksDto> getProjects() {
        logger.info("getProjects was called: ");
        List<ProjectTasksDto> projectTaskList = new ArrayList<>();
        projectDao.findProjects().forEach(project -> {
            List<TaskProjectDto> tasksByProject = taskService.getTasksByProject(project.getId());
            ProjectTasksDto projectTasksDto = projectDtoMapper.apply(project, tasksByProject);
            projectTaskList.add(projectTasksDto);
        });
        return projectTaskList;
    }

    @Override
    public ProjectTasksDto getProjectById(Long projectId) {
        logger.info("getProjectById was called: {} ", projectId);
        Project project = projectDao.findProjectById(projectId).orElseThrow(() -> {
            ProjectNotFoundException projectNotFoundException = new ProjectNotFoundException(
                    String.format("Project with id %s doesn't exists", projectId)
            );
            logger.error("error in getProjectById: {} ", projectId, projectNotFoundException);
            return projectNotFoundException;
        });

        List<TaskProjectDto> tasksByProject = taskService.getTasksByProject(project.getId());
        return projectDtoMapper.apply(project, tasksByProject);
    }

    @Override
    public ProjectTasksDto addProject(ProjectTasksDtoRequest projectTasksRequest) {
        logger.info("addProject was called for project: {} ", projectTasksRequest.getId());

        Project project = new Project(
                projectTasksRequest.getTitle(),
                projectTasksRequest.getAbbreviation(),
                projectTasksRequest.getDescription()
        );

        List<Long> tasksIds = convertStringToLongIds(projectTasksRequest.getTasks());
        Project savedProject = projectDao.saveProject(project);
        bindTasksToProject(savedProject.getId(), tasksIds);

        List<TaskProjectDto> tasksByProject = taskService.getTasksByProject(project.getId());
        return projectDtoMapper.apply(project, tasksByProject);
    }

    @Override
    public ProjectTasksDto updateProject(ProjectTasksDtoRequest projectTasksRequest) {
        logger.info("updateProject was called for project: {} ", projectTasksRequest.getId());
        getProjectById(projectTasksRequest.getId());

        Project project = new Project(
                projectTasksRequest.getId(),
                projectTasksRequest.getTitle(),
                projectTasksRequest.getAbbreviation(),
                projectTasksRequest.getDescription()
        );

        List<Long> tasksIds = convertStringToLongIds(projectTasksRequest.getTasks());
        Project updatedProject = projectDao.updateProject(project);
        bindTasksToProject(updatedProject.getId(), tasksIds);

        List<TaskProjectDto> tasksByProject = taskService.getTasksByProject(project.getId());
        return projectDtoMapper.apply(project, tasksByProject);
    }

    private void bindTasksToProject(Long projectId, List<Long> tasksIds) {
        if (tasksIds != null) {
            tasksIds.forEach(taskId -> taskService.updateTaskProject(taskId, projectId));
        }
    }

    @Override
    public void deleteProject(Long projectId) {
        logger.info("deleteProject was called for project: {} ", projectId);
        getProjectById(projectId);
        projectDao.deleteProject(projectId);
    }
}
