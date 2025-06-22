package com.naglabs.ezquizmaster.controller;

import com.naglabs.ezquizmaster.controller.service.OpenAiService;
import com.naglabs.ezquizmaster.controller.service.dto.Question;
import com.naglabs.ezquizmaster.controller.service.dto.QuestionResponse;
import com.naglabs.ezquizmaster.controller.service.dto.ResultRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quiz")
public class QuizController {

    @Autowired
    private OpenAiService openAiService;

//    @PostMapping("/start")
//    public ResponseEntity<QuestionResponse> startGame() {
//        List<Question> questions = openAiService.generateQuestions();
//        return ResponseEntity.ok(new QuestionResponse(questions));
//    }

    @GetMapping("/start")
    public void startGame() {
       String res = openAiService.generateQuestions("Generate 2 easy Science questions with 4 options and right answer?");
        System.out.println(res);
    }

    @PostMapping("/submit")
    public ResponseEntity<String> submitResult(@RequestBody ResultRequest result) {
        // Call emailService.sendResultEmail(result)
        return ResponseEntity.ok("Result submitted and emailed!");
    }
}

