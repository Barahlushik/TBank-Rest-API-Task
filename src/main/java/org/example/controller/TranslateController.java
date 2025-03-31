package org.example.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.example.exception.ErrorResponse;
import org.example.model.TranslateRequest;
import org.example.model.TranslateResponse;

import org.example.service.translate.Translator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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


    private static final Logger logger = LoggerFactory.getLogger(TranslateController.class);

    @Autowired
    @Qualifier("cachedYandexTranslateService")
    private Translator translateService;

    @PostMapping
    public ResponseEntity<?> translate(@Valid @RequestBody TranslateRequest request, HttpServletRequest httpRequest) {
           try {
               request.setRemoteAddress(httpRequest.getRemoteAddr());
               logger.info("Received translation request from IP: {}, SL: {}, TL: {}",
                       httpRequest.getRemoteAddr(), request.getSourceLang(), request.getTargetLang());
               String translatedText = translateService.translate(request);
               return ResponseEntity.ok(new TranslateResponse(translatedText));
           }  catch (Exception e) {
               logger.error("Translation failed for request from IP: {}", httpRequest.getRemoteAddr(), e);
               return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                       .body(new ErrorResponse("Translation failed: " + e.getMessage()));
           }
    }

}