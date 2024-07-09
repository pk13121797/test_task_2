package by.pavvel.util.populator;

import by.pavvel.config.ConnectionManager;
import by.pavvel.config.ConnectionManagerImpl;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.List;

public final class DatabasePopulator {

    private static final String SCHEME = "sql/schema.sql";

    private static final String DATA = "sql/data.sql";

    private static final List<String> SQL_FILES = List.of(SCHEME, DATA);

    public static void populateData() throws SQLException {
        ConnectionManager connectionManager = ConnectionManagerImpl.getInstance();
        Connection connection = connectionManager.getConnection();
        Statement statement = connection.createStatement();

        SQL_FILES.forEach(file -> {
            String loadedSqlFile = loadSql(file);
            try {
                statement.execute(loadedSqlFile);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static String loadSql(String file) {
        String sqlFile;
        try (InputStream inputStream = DatabasePopulator.class.getClassLoader().getResourceAsStream(file)) {
            assert inputStream != null;
            sqlFile = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException();
        }
        return sqlFile;
    }
}
