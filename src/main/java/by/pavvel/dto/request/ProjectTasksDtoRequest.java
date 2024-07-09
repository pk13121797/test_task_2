package by.pavvel.dto.request;

import lombok.Getter;

@Getter
public class ProjectTasksDtoRequest {

    private final Long id;

    private final String title;

    private final String abbreviation;

    private final String description;

    private final String tasks;

    public ProjectTasksDtoRequest(Long id, String title, String abbreviation, String description, String tasks) {
        this.id = id;
        this.title = title;
        this.abbreviation = abbreviation;
        this.description = description;
        this.tasks = tasks;
    }

    @Override
    public String toString() {
        return "ProjectDtoTaskIds{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", abbreviation='" + abbreviation + '\'' +
                ", description='" + description + '\'' +
                ", tasks='" + tasks + '\'' +
                '}';
    }
}
