package by.pavvel.exception.handler;

import java.time.LocalDate;

public record ApiException(String message, String path, int httpStatus, LocalDate localDate) {

    @Override
    public String toString() {
        return "ApiException{" +
                "message='" + message + '\'' +
                ", path='" + path + '\'' +
                ", httpStatus=" + httpStatus +
                ", localDate=" + localDate +
                '}';
    }
}
