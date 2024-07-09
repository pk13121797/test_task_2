package by.pavvel.dto;

import by.pavvel.model.Status;
import lombok.Getter;

@Getter
public class TaskProjectDto {

    private final Long id;

    private final String title;

    private final Status status;

    public TaskProjectDto(Long id, String title, Status status) {
        this.id = id;
        this.title = title;
        this.status = status;
    }

    @Override
    public String toString() {
        return "TaskProjectDto{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", status=" + status +
                '}';
    }
}
