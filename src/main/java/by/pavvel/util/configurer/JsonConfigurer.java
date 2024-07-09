package by.pavvel.util.configurer;

import by.pavvel.util.adapter.LocalDateTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;

public class JsonConfigurer {

    public static void configureJson(HttpServletResponse resp) {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
    }

    public static StringBuilder getStringBuilder(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = req.getReader().readLine()) != null) {
            sb.append(line);
        }
        return sb;
    }

    public static Gson getCustomGson() {
        return new GsonBuilder()
                .registerTypeAdapter(
                        LocalDate.class,
                        new LocalDateTypeAdapter()
                )
                .create();
    }
}
