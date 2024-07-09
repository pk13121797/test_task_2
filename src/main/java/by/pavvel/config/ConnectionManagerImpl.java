package by.pavvel.config;

import by.pavvel.util.PropertiesUtil;
import by.pavvel.util.populator.DatabasePopulator;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConnectionManagerImpl implements ConnectionManager {

    private static ConnectionManager instance;

    static {
        try {
            DatabasePopulator.populateData();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private ConnectionManagerImpl() {}

    public static ConnectionManager getInstance() {
        if (instance == null) {
            instance = new ConnectionManagerImpl();
            loadDriver(PropertiesUtil.getProperties("db.driver"));
        }
        return instance;
    }

    private static void loadDriver(String driverClass) {
        try {
            Class.forName(driverClass);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(String.format("Cannot load driver %s ", driverClass));
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                PropertiesUtil.getProperties("db.url"),
                PropertiesUtil.getProperties("db.username"),
                PropertiesUtil.getProperties("db.password")
        );
    }
}
