package org.example.req;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotEmpty;



public class TranslateRequest {

    @NotEmpty(message = "Source language cannot be empty")
    private String sourceLang;

    @NotEmpty(message = "Target language cannot be empty")
    private String targetLang;

    @NotEmpty(message = "Text cannot be empty")
    private String text;

    @JsonIgnore
    private String remoteAddress;

    public TranslateRequest(String sourceLang, String targetLang, String text) {
        this.sourceLang = sourceLang;
        this.targetLang = targetLang;
        this.text = text;
    }

    public TranslateRequest(String sourceLang, String targetLang, String text, String remoteAddress) {
        this.sourceLang = sourceLang;
        this.targetLang = targetLang;
        this.text = text;
        this.remoteAddress = remoteAddress;
    }

    public TranslateRequest() {
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

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