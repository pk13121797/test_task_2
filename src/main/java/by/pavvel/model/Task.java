package by.pavvel.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
public class Task {

    private Long id;

    private String title;

    private Integer hours;

    private LocalDate startDate;

    private LocalDate endDate;

    private Status status;

    private Project project;

    private Set<Employee> employees = new HashSet<>();

    public Task() {
    }

    public Task(String title, Integer hours, LocalDate startDate, LocalDate endDate, Status status) {
        this.title = title;
        this.hours = hours;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public Task(Long id, String title, Integer hours, LocalDate startDate, LocalDate endDate, Status status) {
        this.id = id;
        this.title = title;
        this.hours = hours;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id) && Objects.equals(title, task.title) && Objects.equals(hours, task.hours) && Objects.equals(startDate, task.startDate) && Objects.equals(endDate, task.endDate) && status == task.status && Objects.equals(project, task.project) && Objects.equals(employees, task.employees);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, hours, startDate, endDate, status, project, employees);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", hours=" + hours +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", status=" + status +
                '}';
    }
}
