package by.pavvel.dto.request;

import by.pavvel.model.Status;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class TaskProjectEmployeesDtoRequest {

    private final Long id;

    private final String title;

    private final Integer hours;

    private final LocalDate startDate;

    private final LocalDate endDate;

    private final Status status;

    private final Long project;

    private final String employees;

    public TaskProjectEmployeesDtoRequest(Long id, String title, Integer hours, LocalDate startDate, LocalDate endDate,
                                          Status status, Long project, String employees) {
        this.id = id;
        this.title = title;
        this.hours = hours;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.project = project;
        this.employees = employees;
    }

    @Override
    public String toString() {
        return "TaskProjectEmployeesDtoRequest{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", hours=" + hours +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", status=" + status +
                ", project=" + project +
                ", employees='" + employees + '\'' +
                '}';
    }
}
