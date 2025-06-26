package com.naglabs.ezquizmaster.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.naglabs.ezquizmaster.dto.Question;
import com.naglabs.ezquizmaster.entity.UserSession;
import com.naglabs.ezquizmaster.exception.LifelineAlreadyUsedException;
import com.naglabs.ezquizmaster.exception.UserSessionNotFoundException;
import com.naglabs.ezquizmaster.repository.UserSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class LifelineService {

    @Autowired
    private UserSessionRepository userSessionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    public Question useAlternateQuestion(String sessionId, String difficultyLevel) throws JsonProcessingException {
        UserSession session = userSessionRepository.findById(sessionId)
                .orElseThrow(() -> new UserSessionNotFoundException("Session ID not found: " + sessionId));

        Map<String, Question> sessionAlternateMap = objectMapper.readValue(session.getAlternateQuestionsJson(), new TypeReference<>() {
        });

        if (session.isUsedAlternate()) {
            throw new LifelineAlreadyUsedException("Alternate lifeline already used.");
        }

        Question alternateQuestion = sessionAlternateMap.get(difficultyLevel);

        session.setUsedAlternate(true);
        session.setRemainingLifelines(session.getRemainingLifelines() - 1);
        session.setCurrentAlternateQuestionWithDifficultyLevel(difficultyLevel);
        userSessionRepository.save(session);
        return Question.copyOnlyQstnAndOptions(alternateQuestion);
    }

    public Question getAlternateQuestionServedCurrently(UserSession session) throws JsonProcessingException {
        Map<String, Question> sessionAlternateMap = objectMapper.readValue(session.getAlternateQuestionsJson(), new TypeReference<>() {
        });

        Question alternateQuestion = sessionAlternateMap.get(session.getCurrentAlternateQuestionWithDifficultyLevel());
        session.setCurrentAlternateQuestionWithDifficultyLevel(null);
        return alternateQuestion;
    }
}
