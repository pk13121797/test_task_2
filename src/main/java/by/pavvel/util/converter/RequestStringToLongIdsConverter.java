package by.pavvel.util.converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RequestStringToLongIdsConverter {

    public static List<Long> convertStringToLongIds(String stringIds) {
        List<Long> ids = new ArrayList<>();
        if (stringIds != null) {
            String[] splitIds = stringIds.split(",");
            Arrays.stream(splitIds).forEach(id -> {
                Long parsedId = Long.parseLong(id.trim());
                ids.add(parsedId);
            });
        }
        return ids;
    }
}
