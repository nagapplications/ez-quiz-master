package com.naglabs.ezquizmaster.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.naglabs.ezquizmaster.dto.Question;
import com.naglabs.ezquizmaster.service.LifelineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping("/api/lifeline")
public class LifeLineController {

    @Autowired
    private LifelineService lifelineService;

    @GetMapping("/alternate")
    public ResponseEntity<Question> getAlternateQuestion(@RequestParam("sessionId") String sessionId) throws JsonProcessingException {
        return ResponseEntity.ok(lifelineService.getAlternateQuestion(sessionId));
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
