package by.pavvel.exception.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.time.LocalDate;

public class GlobalExceptionHandler {

    public static ApiException processError(HttpServletRequest req, HttpServletResponse resp, RuntimeException e) {
        return new ApiException(
                e.getMessage(),
                req.getServletPath() + req.getPathInfo(),
                resp.getStatus(),
                LocalDate.now()
        );
    }
}
