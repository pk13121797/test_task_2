package by.pavvel.servlet;

import by.pavvel.model.Employee;
import by.pavvel.service.EmployeeService;
import by.pavvel.service.impl.EmployeeServiceImpl;
import com.google.gson.Gson;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import static by.pavvel.exception.handler.GlobalExceptionHandler.processError;
import static by.pavvel.util.configurer.JsonConfigurer.*;

@WebServlet("/employees/*")
public class EmployeeServlet extends HttpServlet {

    private final EmployeeService employeeService;

    public EmployeeServlet() {
        this.employeeService = EmployeeServiceImpl.getInstance();
    }

    public EmployeeServlet(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        Object data = null;
        String pathInfo = req.getPathInfo();
        configureJson(resp);
        Gson gson = getCustomGson();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                data = employeeService.getEmployees();
                resp.setStatus(HttpServletResponse.SC_OK);
            } else if (pathInfo.split("/").length > 2) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            } else {
                Long computedId = Long.parseLong(pathInfo.split("/")[1]);
                data = employeeService.getEmployeeById(computedId);
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
            Employee employee = gson.fromJson(String.valueOf(sb), Employee.class);
            data = employeeService.addEmployee(employee);
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
            Employee employee = gson.fromJson(String.valueOf(sb), Employee.class);
            data = employeeService.updateEmployee(employee);
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
                employeeService.deleteEmployee(computedId);
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
