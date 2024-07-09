package by.pavvel.config;

import by.pavvel.util.PropertiesUtil;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class AbstractTestcontainers {

    private static final String SCHEMA_SQL = "sql/schema.sql";

    @Container // allows to start and stop the container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:15-alpine"
    ).withInitScript(SCHEMA_SQL);

    @BeforeAll
    static void beforeAll() {
        PropertiesUtil.setProperties("db.url", postgres.getJdbcUrl());
        PropertiesUtil.setProperties("db.username", postgres.getUsername());
        PropertiesUtil.setProperties("db.password", postgres.getPassword());
    }
}
