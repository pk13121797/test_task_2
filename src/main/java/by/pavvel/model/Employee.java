package by.pavvel.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class Employee {

    private Long id;

    private String name;

    private String surname;

    private String middleName;

    private String post;

    public Employee() {
    }

    public Employee(String name, String surname, String middleName, String post) {
        this.name = name;
        this.surname = surname;
        this.middleName = middleName;
        this.post = post;
    }

    public Employee(Long id, String name, String surname, String middleName, String post) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.middleName = middleName;
        this.post = post;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return Objects.equals(id, employee.id) && Objects.equals(name, employee.name) && Objects.equals(surname, employee.surname) && Objects.equals(middleName, employee.middleName) && Objects.equals(post, employee.post);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, surname, middleName, post);
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", middleName='" + middleName + '\'' +
                ", post='" + post + '\'' +
                '}';
    }
}
