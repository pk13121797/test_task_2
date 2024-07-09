package by.pavvel.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class ProjectTasksDto {

    private final Long id;

    private final String title;

    private final String abbreviation;

    private final String description;

    private final List<TaskProjectDto> tasks;

    public ProjectTasksDto(Long id, String title, String abbreviation, String description, List<TaskProjectDto> tasks) {
        this.id = id;
        this.title = title;
        this.abbreviation = abbreviation;
        this.description = description;
        this.tasks = tasks;
    }

    @Override
    public String toString() {
        return "ProjectTasksDto{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", abbreviation='" + abbreviation + '\'' +
                ", description='" + description + '\'' +
                ", tasks=" + tasks +
                '}';
    }
}
