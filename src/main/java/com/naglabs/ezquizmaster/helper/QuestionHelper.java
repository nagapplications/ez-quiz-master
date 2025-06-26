package com.naglabs.ezquizmaster.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.naglabs.ezquizmaster.dto.Question;
import com.naglabs.ezquizmaster.entity.UserSession;
import com.naglabs.ezquizmaster.exception.UserSessionNotFoundException;

import java.util.Map;

public class QuestionHelper {

//    public Question getQuestionFromSession(String sessionId, Boolean next) throws JsonProcessingException {
//        UserSession session = userSessionRepository.findById(sessionId).orElseThrow(() -> new UserSessionNotFoundException("Session ID not found: " + sessionId));
//
//        if (session.getCurrentAlternateQuestionWithDifficultyLevel() != null) {
//            return lifelineService.getAlternateQuestionServedCurrently(session);
//        }
//
//        Map<Integer, Question> sessionOriginalQuestionMap = objectMapper.readValue(session.getPrimaryQuestionsJson(), new TypeReference<>() {
//        });
//
//        int qno = session.getCurrentQuestionIndex();
//
//        if (next) {
//            qno += 1;
//            session.setCurrentQuestionIndex(qno);
//            session.setScore(session.getScore() + 10);
//            userSessionRepository.save(session); // Persist changes
//        }
//
//        return sessionOriginalQuestionMap.get(qno);
//    }
}
