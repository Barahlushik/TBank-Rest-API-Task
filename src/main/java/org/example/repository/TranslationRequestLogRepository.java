package org.example.repository;

import org.example.model.TranslationRequestLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Repository
public class TranslationRequestLogRepository {

    private final DataSource dataSource;

    @Autowired
    public TranslationRequestLogRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void save(TranslationRequestLog log) {
        String sql = "INSERT INTO translation_request_log (ip_address, input_text, translated_text, request_time) VALUES (?, ?, ?, ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, log.getIpAddress());
            preparedStatement.setString(2, log.getInputText());
            preparedStatement.setString(3, log.getTranslatedText());
            preparedStatement.setObject(4, log.getRequestTime());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error saving log to database", e);
        }
    }
}