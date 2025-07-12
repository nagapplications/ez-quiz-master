package com.naglabs.ezquizmaster.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.naglabs.ezquizmaster.dto.Question;
import com.naglabs.ezquizmaster.service.LifelineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/lifeline")
public class LifeLineController {

    @Autowired
    private LifelineService lifelineService;

    @PostMapping("/alternate")
    public ResponseEntity<Question> getAlternateQuestion(@RequestParam("sessionId") String sessionId) throws JsonProcessingException {
        return ResponseEntity.ok(lifelineService.getAlternateQuestion(sessionId));
    }

    @PostMapping("/evaluateAlternate")
    public ResponseEntity<Question> evaluateAlternate(@RequestParam("sessionId") String sessionId, @RequestParam("option") String option) throws JsonProcessingException {
        return ResponseEntity.ok(lifelineService.evaluateAnswerForAlternate(sessionId, option));
    }

    @GetMapping("/fiftyfifty")
    public ResponseEntity<List<String>> useFiftyFifty(@RequestParam("sessionId") String sessionId, @RequestParam("qno") Integer qno) throws JsonProcessingException {
        return ResponseEntity.ok(lifelineService.useFiftyFifty(sessionId, qno));
    }

    @GetMapping("/secondchance")
    public ResponseEntity<Boolean> useSecondChance(@RequestParam("sessionId") String sessionId, @RequestParam("qno") Integer qno, @RequestParam("option") String option) throws JsonProcessingException {
        return ResponseEntity.ok(lifelineService.useSecondChance(sessionId, qno, option));
    }
}
