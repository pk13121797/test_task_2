package by.pavvel.dto;

import by.pavvel.model.Status;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class TaskDto {

    private final Long id;

    private final String title;

    private final Integer hours;

    private final LocalDate startDate;

    private final LocalDate endDate;

    private final Status status;

    private final ProjectDto project;

    public TaskDto(Long id, String title, Integer hours, LocalDate startDate, LocalDate endDate, Status status, ProjectDto project) {
        this.id = id;
        this.title = title;
        this.hours = hours;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.project = project;
    }

    @Override
    public String toString() {
        return "TaskDto{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", hours=" + hours +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", status=" + status +
                ", project=" + project +
                '}';
    }
}
