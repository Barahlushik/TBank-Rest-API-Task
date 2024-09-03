package org.example.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.example.model.TranslateRequest;
import org.example.model.TranslateResponse;

import org.example.service.translate.Translator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/translate")
public class TranslateController {

    @Autowired
    @Qualifier("cachedYandexTranslateService")
    private Translator translateService;

    @PostMapping
    public ResponseEntity<TranslateResponse> translate(@Valid @RequestBody TranslateRequest request, HttpServletRequest httpRequest) {
            request.setRemoteAddress(httpRequest.getRemoteAddr());
            String translatedText = translateService.translate(request);
            return ResponseEntity.ok(new TranslateResponse(translatedText));
    }
}