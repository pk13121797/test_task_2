package by.pavvel.dao;

import by.pavvel.config.AbstractTestcontainers;
import by.pavvel.dao.impl.ProjectDaoImpl;
import by.pavvel.model.Project;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class ProjectDaoTest extends AbstractTestcontainers {

    private ProjectDao underTest;

    @BeforeEach
    void setUp() {
        underTest = new ProjectDaoImpl();
    }

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void shouldFindProjects() {
        // given
        Project project = new Project("Java", "AAN", "description 1");

        List<Project> projectList = List.of(project);
        underTest.saveProject(project);

        // when
        List<Project> projects = underTest.findProjects();

        // then
        assertThat(projects)
                .isNotNull()
                .isEqualTo(projectList);
    }

    @Test
    void shouldFindProjectById() {
        // given
        Project project = new Project("Java", "AAN", "description 1");

        underTest.saveProject(project);

        // when
        Optional<Project> projectById = underTest.findProjectById(project.getId());

        // then
        assertThat(projectById)
                .isPresent()
                .hasValueSatisfying(p -> assertThat(p).isEqualTo(project));
    }

    @Test
    void shouldSaveProject() {
        // given
        Project project = new Project("Java", "AAN", "description 1");

        // when
        Project savedProject = underTest.saveProject(project);

        // then
        assertThat(savedProject)
                .isNotNull()
                .isEqualTo(project);
    }

    @Test
    void shouldUpdateProject() {
        // given
        Project project = new Project("Java", "AAN", "description 1");
        underTest.saveProject(project);

        Project projectToUpdate = new Project(project.getId(),"Kotlin", "AB", "description 3");

        // when
        Project updatedProject = underTest.updateProject(projectToUpdate);

        // then
        assertThat(updatedProject)
                .isEqualTo(projectToUpdate);
    }

    @Test
    void shouldDeleteProjectById() {
        // given
        Project project = new Project( "Java", "AAN", "description 1");
        underTest.saveProject(project);

        // when
        underTest.deleteProject(project.getId());

        // then
        List<Project> projects = underTest.findProjects();
        assertThat(projects.contains(project)).isFalse();
    }

    @Test
    void shouldDeleteAllProjects() {
        // given
        Project project1 = new Project( "Java", "AAN", "description 1");
        Project project2 = new Project("Kotlin", "AB", "description 3");
        underTest.saveProject(project1);
        underTest.saveProject(project2);

        // when
        underTest.deleteAll();

        // then
        List<Project> projects = underTest.findProjects();
        assertThat(projects.size()).isEqualTo(0);
    }
}
