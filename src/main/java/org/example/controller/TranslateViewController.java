package org.example.controller;

import org.example.model.TranslateRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("/translate")
public class TranslateViewController {

    @GetMapping
    public String showTranslatePage(Model model) {
        // Здесь можно добавить список поддерживаемых языков
        model.addAttribute("languages", Map.of(
                "en", "English",
                "es", "Spanish",
                "fr", "French",
                "de", "German",
                "ru", "Russian"
        ));
        model.addAttribute("translateRequest", new TranslateRequest());
        return "translate";
    }
}