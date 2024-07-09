package by.pavvel.service.impl;

import by.pavvel.dao.TaskDao;
import by.pavvel.dao.impl.TaskDaoImpl;
import by.pavvel.dto.TaskDto;
import by.pavvel.dto.TaskProjectDto;
import by.pavvel.dto.request.TaskProjectEmployeesDtoRequest;
import by.pavvel.exception.TaskNotFoundException;
import by.pavvel.model.Task;
import by.pavvel.service.TaskService;
import by.pavvel.util.mapper.TaskDtoMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

import static by.pavvel.util.converter.RequestStringToLongIdsConverter.convertStringToLongIds;

public class TaskServiceImpl implements TaskService {

    private static final Logger logger = LogManager.getLogger(TaskServiceImpl.class);

    private static TaskService instance;

    private final TaskDao taskDao;

    private final TaskDtoMapper taskDtoMapper = new TaskDtoMapper();

    public TaskServiceImpl(TaskDao taskDao) {
        this.taskDao = taskDao;
    }

    public static synchronized TaskService getInstance() {
        if (instance == null) {
            instance = new TaskServiceImpl(TaskDaoImpl.getInstance());
        }
        return instance;
    }

    @Override
    public List<TaskDto> getTasks() {
        logger.info("getTasks was called: ");
        return taskDao.findTasks();
    }

    @Override
    public TaskDto getTaskById(Long taskId) {
        logger.info("getTaskById was called: {} ", taskId);
        return taskDao.findTaskById(taskId).orElseThrow(() -> {
            TaskNotFoundException taskNotFoundException = new TaskNotFoundException(
                    String.format("Task with id %s not found", taskId)
            );
            logger.error("error in getTaskById: {} ", taskId, taskNotFoundException);
            return taskNotFoundException;
        });
    }

    @Override
    public List<TaskProjectDto> getTasksByProject(Long projectId) {
        logger.info("getTasksByProject was called for project: {} ", projectId);
        return taskDao.findTasksByProjectId(projectId);
    }

    @Override
    public TaskProjectDto addTask(TaskProjectEmployeesDtoRequest taskRequest) {
        logger.info("addTask was called for task: {} ", taskRequest.getId());
        Long projectId = taskRequest.getProject();
        String stringEmployeeIds = taskRequest.getEmployees();
        List<Long> employeeIds = convertStringToLongIds(stringEmployeeIds);
        Task task = new Task(
                taskRequest.getTitle(),
                taskRequest.getHours(),
                taskRequest.getStartDate(),
                taskRequest.getEndDate(),
                taskRequest.getStatus()
        );

        Task savedTask = taskDao.saveTask(task, projectId);
        bindEmployeesToTask(task.getId(), employeeIds);
        return taskDtoMapper.apply(savedTask);
    }

    @Override
    public TaskProjectDto updateTask(TaskProjectEmployeesDtoRequest taskRequest) {
        logger.info("updateTask was called for task: {} ", taskRequest.getId());
        getTaskById(taskRequest.getId());
        Long projectId = taskRequest.getProject();
        String stringEmployeeIds = taskRequest.getEmployees();
        List<Long> employeeIds = convertStringToLongIds(stringEmployeeIds);
        Task task = new Task(
                taskRequest.getId(),
                taskRequest.getTitle(),
                taskRequest.getHours(),
                taskRequest.getStartDate(),
                taskRequest.getEndDate(),
                taskRequest.getStatus()
        );

        Task updatedTask = taskDao.updateTask(task, projectId);
        bindEmployeesToTask(task.getId(), employeeIds);
        return taskDtoMapper.apply(updatedTask);
    }

    private void bindEmployeesToTask(Long taskId, List<Long> employeesIds) {
        for (Long employeeId : employeesIds) {
            taskDao.saveTaskEmployee(taskId, employeeId);
        }
    }

    @Override
    public TaskProjectDto updateTaskProject(Long taskId, Long projectId) {
        logger.info("updateTaskProject was called for project: {} ", projectId);
        taskDao.updateTaskProject(taskId, projectId);
        return taskDao.findTaskDto(taskId).orElseThrow(() -> {
            TaskNotFoundException taskNotFoundException = new TaskNotFoundException(
                    String.format("Task with id %s not found", taskId)
            );
            logger.error("error in updateTaskProject: {} ", taskId, taskNotFoundException);
            return taskNotFoundException;
        });
    }

    @Override
    public void deleteTask(Long taskId) {
        logger.info("deleteTask was called for task: {} ", taskId);
        getTaskById(taskId);
        taskDao.deleteTask(taskId);
    }
}