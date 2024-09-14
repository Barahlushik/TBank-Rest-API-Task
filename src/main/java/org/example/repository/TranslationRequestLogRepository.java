package org.example.repository;

import org.example.model.TranslationRequestLog;
import org.example.service.translate.impl.CachedYandexTranslateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Repository
public class TranslationRequestLogRepository {
    private static final Logger logger = LoggerFactory.getLogger(TranslationRequestLogRepository.class);
    private final DataSource dataSource;
    private static final String INSERT_SQL = "INSERT INTO translation_request_log (ip_address, input_text, translated_text, request_time) VALUES (?, ?, ?, ?)";
    @Autowired
    public TranslationRequestLogRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void save(TranslationRequestLog log) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_SQL)) {

            preparedStatement.setString(1, log.getIpAddress());
            preparedStatement.setString(2, log.getInputText());
            preparedStatement.setString(3, log.getTranslatedText());
            preparedStatement.setObject(4, log.getRequestTime());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("Лог запроса успешно сохранён в базу данных. IP: '{}', текст запроса: '{}', переведённый текст: '{}', время запроса: '{}'",
                        log.getIpAddress(), log.getInputText(), log.getTranslatedText(), log.getRequestTime());
            } else {
                logger.warn("Запись не была добавлена в базу данных. IP: '{}', текст запроса: '{}', переведённый текст: '{}', время запроса: '{}'",
                        log.getIpAddress(), log.getInputText(), log.getTranslatedText(), log.getRequestTime());
            }
        } catch (SQLException e) {
            logger.error("Ошибка при сохранении лога в базу данных. IP: '{}', текст запроса: '{}', ошибка: {}",
                    log.getIpAddress(), log.getInputText(), e.getMessage(), e);
            throw new RuntimeException("Error saving log to database", e);
        }
    }
}