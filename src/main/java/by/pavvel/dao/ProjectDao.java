package by.pavvel.dao;

import by.pavvel.model.Project;
import java.util.List;
import java.util.Optional;

public interface ProjectDao {

    List<Project> findProjects();

    Optional<Project> findProjectById(Long projectId);

    Project saveProject(Project project);

    Project updateProject(Project project);

    void deleteProject(Long projectId);

    void deleteAll();
}
