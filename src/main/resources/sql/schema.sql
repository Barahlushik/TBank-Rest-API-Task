CREATE TABLE translation_request_log
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    ip_address      VARCHAR(30)  NOT NULL,
    input_text      TEXT         NOT NULL,
    translated_text TEXT         NOT NULL,
    request_time    TIMESTAMP    NOT NULL
);