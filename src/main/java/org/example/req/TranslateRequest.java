package org.example.req;

import jakarta.validation.constraints.NotEmpty;



public class TranslateRequest {

    @NotEmpty(message = "Source language cannot be empty")
    private String sourceLang;

    @NotEmpty(message = "Target language cannot be empty")
    private String targetLang;

    @NotEmpty(message = "Text cannot be empty")
    private String text;

    public String getSourceLang() {
        return sourceLang;
    }

    public void setSourceLang(String sourceLang) {
        this.sourceLang = sourceLang;
    }

    public String getTargetLang() {
        return targetLang;
    }

    public void setTargetLang(String targetLang) {
        this.targetLang = targetLang;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}