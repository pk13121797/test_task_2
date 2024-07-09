package by.pavvel.servlet;

import by.pavvel.dto.request.ProjectTasksDtoRequest;
import by.pavvel.service.ProjectService;
import by.pavvel.service.impl.ProjectServiceImpl;
import com.google.gson.Gson;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import static by.pavvel.exception.handler.GlobalExceptionHandler.processError;
import static by.pavvel.util.configurer.JsonConfigurer.*;

@WebServlet("/projects/*")
public class ProjectServlet extends HttpServlet {

    private final ProjectService projectService;

    public ProjectServlet() {
        this.projectService = ProjectServiceImpl.getInstance();
    }

    public ProjectServlet(ProjectService projectService) {
        this.projectService = projectService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        Object data = null;
        String pathInfo = req.getPathInfo();
        configureJson(resp);
        Gson gson = getCustomGson();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                data = projectService.getProjects();
                resp.setStatus(HttpServletResponse.SC_OK);
            } else if (pathInfo.split("/").length > 2) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            } else {
                Long computedId = Long.parseLong(pathInfo.split("/")[1]);
                data = projectService.getProjectById(computedId);
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
            ProjectTasksDtoRequest projectRequest = gson.fromJson(String.valueOf(sb), ProjectTasksDtoRequest.class);
            data = projectService.addProject(projectRequest);
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
            ProjectTasksDtoRequest projectRequest = gson.fromJson(String.valueOf(sb), ProjectTasksDtoRequest.class);
            data = projectService.updateProject(projectRequest);
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
                projectService.deleteProject(computedId);
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
