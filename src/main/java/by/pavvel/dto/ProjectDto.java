package by.pavvel.dto;

import lombok.Getter;

@Getter
public class ProjectDto {

    private final Long id;

    private final String title;

    public ProjectDto(Long id, String title) {
        this.id = id;
        this.title = title;
    }

    @Override
    public String toString() {
        return "ProjectDto{" +
                "id=" + id +
                ", title='" + title + '\'' +
                '}';
    }
}
