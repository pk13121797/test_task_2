package by.pavvel.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class Project {

    private Long id;

    private String title;

    private String abbreviation;

    private String description;

    private List<Task> tasks = new ArrayList<>();

    public Project() {
    }

    public Project(String title, String abbreviation, String description) {
        this.title = title;
        this.abbreviation = abbreviation;
        this.description = description;
    }

    public Project(Long id, String title, String abbreviation, String description) {
        this.id = id;
        this.title = title;
        this.abbreviation = abbreviation;
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return Objects.equals(id, project.id) && Objects.equals(title, project.title) && Objects.equals(abbreviation, project.abbreviation) && Objects.equals(description, project.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, abbreviation, description);
    }

    @Override
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", abbreviation='" + abbreviation + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
