package com.naglabs.ezquizmaster.controller;

import com.naglabs.ezquizmaster.dto.Question;
import com.naglabs.ezquizmaster.service.GameLaunchService;
import com.naglabs.ezquizmaster.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/quiz")
public class QuizController {

    @Autowired
    private QuizService quizService;
    @Autowired
    private GameLaunchService gameLaunchService;

    //frontend need to call the below with post including jsession id, till then its get
    @PostMapping("/start")
    public ResponseEntity<String> startQuiz(@AuthenticationPrincipal Jwt jwt) throws Exception {
        String email = jwt.getClaimAsString("email");
        if (email == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email not found in token");
        }

        System.out.println("Starting quiz for user email: " + email);
        String sessionId = gameLaunchService.startQuiz(email);
        System.out.println("Quiz session created with ID: " + sessionId);
        return ResponseEntity.ok(sessionId);
    }


    //TODO : AFTER UI IS READY, CHANGE THE BELOW TO POST MAPPINGS

    @PostMapping("/getQuestion")
    public ResponseEntity<Question> getQuestion(@RequestParam("sessionId") String sessionId) throws Exception {
        Question firstQuestion = quizService.getFirstQuestion(sessionId);
        return firstQuestion == null ? ResponseEntity.noContent().build() : ResponseEntity.ok(firstQuestion);
    }

    @PostMapping("/submitAnswer")
    public ResponseEntity<Question> submitAnswer(@RequestParam("sessionId") String sessionId, @RequestParam("qno") Integer qno, @RequestParam("option") String option) throws Exception {
        Question nextQuestion = quizService.submitAnswer(sessionId, qno, option);
        return ResponseEntity.ok(nextQuestion);
    }
}
