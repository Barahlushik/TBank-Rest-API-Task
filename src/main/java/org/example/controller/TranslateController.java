package org.example.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.example.req.TranslateRequest;
import org.example.resp.TranslateResponse;
import org.example.service.TranslateService;
import org.springframework.beans.factory.annotation.Autowired;
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
    private TranslateService translateService;



    @PostMapping
    public ResponseEntity<TranslateResponse> translate(@Valid @RequestBody TranslateRequest request, HttpServletRequest httpRequest) {
        try {
            String translatedText = translateService.translate(request, httpRequest);
            return ResponseEntity.ok(new TranslateResponse(translatedText));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new TranslateResponse("Не найден язык исходного сообщения"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new TranslateResponse("Ошибка доступа к ресурсу перевода"));
        }
    }
}