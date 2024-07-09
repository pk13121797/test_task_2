package by.pavvel.dao.impl;

import by.pavvel.dao.ProjectDao;
import by.pavvel.config.ConnectionManager;
import by.pavvel.config.ConnectionManagerImpl;
import by.pavvel.model.Project;

import java.sql.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProjectDaoImpl implements ProjectDao {

    private static final ConnectionManager connectionManager = ConnectionManagerImpl.getInstance();

    private static final String SELECT_ALL_PROJECTS = """
            SELECT project_id, title, abbreviation, description FROM project;
            """;

    private static final String SELECT_PROJECT_BY_ID = """
            SELECT project_id, title, abbreviation, description FROM project p WHERE project_id = ?;
            """;

    private static final String INSERT_PROJECT = """
            INSERT INTO project(title, abbreviation, description) VALUES (?, ?, ?);
            """;

    private static final String UPDATE_PROJECT = """
            UPDATE project SET title = ?, abbreviation = ?, description = ? WHERE project_id = ?;
            """;

    private static final String DELETE_PROJECT_BY_ID = """
            DELETE FROM project WHERE project_id = ?;
            """;

    private static final String DELETE_ALL_PROJECTS = """
            DELETE FROM project;
            """;

    private static ProjectDao instance;

    public static synchronized ProjectDao getInstance() {
        if (instance == null) {
            instance = new ProjectDaoImpl();
        }
        return instance;
    }

    @Override
    public List<Project> findProjects() {
        List<Project> projects = new ArrayList<>();
        Project project;
        try(Connection connection = connectionManager.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(SELECT_ALL_PROJECTS);
            while (resultSet.next()) {
                project = new Project();
                project.setId(resultSet.getLong("project_id"));
                project.setTitle(resultSet.getString("title"));
                project.setAbbreviation(resultSet.getString("abbreviation"));
                project.setDescription(resultSet.getString("description"));
                projects.add(project);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return projects;
    }

    @Override
    public Optional<Project> findProjectById(Long id) {
        Project project = null;
        try(Connection connection = connectionManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_PROJECT_BY_ID);
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                project = new Project();
                project.setId(resultSet.getLong("project_id"));
                project.setTitle(resultSet.getString("title"));
                project.setAbbreviation(resultSet.getString("abbreviation"));
                project.setDescription(resultSet.getString("description"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.ofNullable(project);
    }

    @Override
    public Project saveProject(Project project) {
        Long projectId = null;
        try (Connection connection = connectionManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_PROJECT, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, project.getTitle());
            preparedStatement.setString(2, project.getAbbreviation());
            preparedStatement.setString(3, project.getDescription());
            preparedStatement.executeUpdate();

            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet != null && resultSet.next()) {
                projectId = resultSet.getLong(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        project.setId(projectId);
        return project;
    }

    @Override
    public Project updateProject(Project project) {
        try (Connection connection = connectionManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_PROJECT);
            preparedStatement.setString(1, project.getTitle());
            preparedStatement.setString(2, project.getAbbreviation());
            preparedStatement.setString(3, project.getDescription());
            preparedStatement.setLong(4, project.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return project;
    }

    @Override
    public void deleteProject(Long projectId) {
        try (Connection connection = connectionManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(DELETE_PROJECT_BY_ID);
            preparedStatement.setLong(1, projectId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteAll() {
        try (Connection connection = connectionManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(DELETE_ALL_PROJECTS);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
