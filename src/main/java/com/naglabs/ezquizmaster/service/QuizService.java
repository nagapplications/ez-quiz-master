package com.naglabs.ezquizmaster.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.naglabs.ezquizmaster.dto.Question;
import com.naglabs.ezquizmaster.entity.UserSession;
import com.naglabs.ezquizmaster.exception.UserSessionNotFoundException;
import com.naglabs.ezquizmaster.helper.QuestionHelper;
import com.naglabs.ezquizmaster.repository.UserSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuizService {

    @Autowired
    QuestionHelper questionHelper;

    @Autowired
    private UserSessionRepository userSessionRepository;

    public Question getFirstQuestion(String sessionId) throws JsonProcessingException {
        UserSession session = userSessionRepository.findById(sessionId).orElseThrow(() -> new UserSessionNotFoundException("Session ID not found: " + sessionId));

        Question firstQuestion = questionHelper.getQuestion(session, 1);
        return Question.copyOnlyQstnAndOptions(firstQuestion);
    }

    public Question submitAnswer(String sessionId, Integer qno, String option) throws Exception {
        UserSession session = userSessionRepository.findById(sessionId).orElseThrow(() -> new UserSessionNotFoundException("Session ID not found: " + sessionId));

        Question nextQuestion = questionHelper.evaluateAnswer(session, qno, option);
        session.setCurrentQuestionIndex(nextQuestion.getId());
        session.setScore(session.getScore() + 10);
        userSessionRepository.save(session); // Persist changes

        return Question.copyOnlyQstnAndOptions(nextQuestion);
    }
}
