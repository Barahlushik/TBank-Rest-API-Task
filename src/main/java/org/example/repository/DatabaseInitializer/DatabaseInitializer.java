package org.example.repository.DatabaseInitializer;

import jakarta.annotation.PostConstruct;
import org.example.service.translate.impl.CachedYandexTranslateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(CachedYandexTranslateService.class);
    private final DataSource dataSource;

    @Value("${db_init_script}")
    private String dbInitScript;

    public DatabaseInitializer(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PostConstruct
    private void initializeDatabase()  {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            String schemaSql = new String(Files.readAllBytes(Paths.get(dbInitScript)));
            statement.execute(schemaSql);
            logger.info("База данных успешно инициализирована с использованием скрипта: '{}'", dbInitScript);
        } catch (SQLException | IOException e) {
            logger.error("Ошибка при инициализации базы данных с использованием скрипта: '{}'. Ошибка: {}", dbInitScript, e.getMessage(), e);
            throw new RuntimeException("Ошибка инициализации базы данных", e);
        }
    }
}
