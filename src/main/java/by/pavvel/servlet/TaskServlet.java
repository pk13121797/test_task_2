package by.pavvel.servlet;

import by.pavvel.dto.request.TaskProjectEmployeesDtoRequest;
import by.pavvel.service.TaskService;
import by.pavvel.service.impl.TaskServiceImpl;
import com.google.gson.Gson;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import static by.pavvel.exception.handler.GlobalExceptionHandler.processError;
import static by.pavvel.util.configurer.JsonConfigurer.*;

@WebServlet("/tasks/*")
public class TaskServlet extends HttpServlet {

    private final TaskService taskService;

    public TaskServlet() {
        this.taskService = TaskServiceImpl.getInstance();
    }

    public TaskServlet(TaskService taskService) {
        this.taskService = taskService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        Object data = null;
        String pathInfo = req.getPathInfo();
        configureJson(resp);
        Gson gson = getCustomGson();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                data = taskService.getTasks();
                resp.setStatus(HttpServletResponse.SC_OK);
            } else if (pathInfo.split("/").length > 2) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            } else {
                Long computedId = Long.parseLong(pathInfo.split("/")[1]);
                data = taskService.getTaskById(computedId);
                resp.setStatus(HttpServletResponse.SC_OK);
            }
        } catch (RuntimeException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            data = processError(req, resp, e);
        }

        String json = gson.toJson(data);
        resp.getWriter().write(json);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        Object data;
        configureJson(resp);
        Gson gson = getCustomGson();
        StringBuilder sb = getStringBuilder(req);

        try {
            TaskProjectEmployeesDtoRequest taskRequest = gson.fromJson(String.valueOf(sb), TaskProjectEmployeesDtoRequest.class);
            data = taskService.addTask(taskRequest);
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (RuntimeException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            data = processError(req, resp, e);
        }

        String json = gson.toJson(data);
        resp.getWriter().write(json);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        Object data;
        configureJson(resp);
        Gson gson = getCustomGson();
        StringBuilder sb = getStringBuilder(req);

        try {
            TaskProjectEmployeesDtoRequest taskRequest = gson.fromJson(String.valueOf(sb), TaskProjectEmployeesDtoRequest.class);
            data = taskService.updateTask(taskRequest);
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (RuntimeException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            data = processError(req, resp, e);
        }

        String json = gson.toJson(data);
        resp.getWriter().write(json);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        Object data = null;
        String pathInfo = req.getPathInfo();
        Gson gson = getCustomGson();

        try {
            if (pathInfo.split("/").length > 2) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            } else {
                Long computedId = Long.parseLong(pathInfo.split("/")[1]);
                taskService.deleteTask(computedId);
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            }
        } catch (RuntimeException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            data = processError(req, resp, e);
        }

        String json = gson.toJson(data);
        resp.getWriter().write(json);
    }
}
