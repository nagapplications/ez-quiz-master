package com.naglabs.ezquizmaster.controller;

import com.naglabs.ezquizmaster.dto.Question;
import com.naglabs.ezquizmaster.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quiz")
public class QuizController {

    @Autowired
    private QuizService quizService;

    //frontend need to call the below with post including jsession id, till then its get
    @GetMapping("/start")
    public ResponseEntity<String> startQuiz(@AuthenticationPrincipal OAuth2User principal) throws Exception {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }

        String email = principal.getAttribute("email");
        if (email == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email not found in user details");
        }

        System.out.println("Starting quiz for user email: " + email);
        String sessionId = quizService.startQuiz(email);
        System.out.println("Quiz session created with ID: " + sessionId);
        return ResponseEntity.ok(sessionId);
    }



    @GetMapping("/question")
    public ResponseEntity<Question> getNextQuestion(@RequestParam String sessionId) throws Exception {
        Question q = quizService.getNextQuestion(sessionId);
        return q == null ? ResponseEntity.noContent().build() : ResponseEntity.ok(q);
    }

    @PostMapping("/answer")
    public ResponseEntity<Boolean> submitAnswer(@RequestParam String sessionId, @RequestParam String option) throws Exception {
        boolean correct = quizService.submitAnswer(sessionId, option);
        return ResponseEntity.ok(correct);
    }

    @PostMapping("/lifeline/alternate")
    public ResponseEntity<Question> useAlternate(@RequestParam String sessionId) {
        return ResponseEntity.ok(quizService.useAlternateQuestion(sessionId));
    }

    @PostMapping("/lifeline/fiftyfifty")
    public ResponseEntity<List<String>> useFiftyFifty(@RequestParam String sessionId) {
        return ResponseEntity.ok(quizService.useFiftyFifty(sessionId));
    }

    @PostMapping("/lifeline/second-chance")
    public ResponseEntity<Void> useSecondChance(@RequestParam String sessionId) {
        quizService.useSecondChance(sessionId);
        return ResponseEntity.ok().build();
    }

}

