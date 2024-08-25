package org.example.repository.DatabaseInitializer;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Component
public class DatabaseInitializer {

    private final DataSource dataSource;

    @Value("${db_init_script}")
    private String dbInitScript;

    public DatabaseInitializer(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PostConstruct
    private void initializeDatabase() throws SQLException, IOException {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            String schemaSql = new String(Files.readAllBytes(Paths.get(dbInitScript)));
            statement.execute(schemaSql);
        }
    }
}
