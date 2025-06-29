package com.naglabs.ezquizmaster.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.naglabs.ezquizmaster.dto.Question;
import com.naglabs.ezquizmaster.entity.UserSession;
import com.naglabs.ezquizmaster.exception.LifelineAlreadyUsedException;
import com.naglabs.ezquizmaster.exception.UserSessionNotFoundException;
import com.naglabs.ezquizmaster.helper.QuestionHelper;
import com.naglabs.ezquizmaster.repository.UserSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class LifelineService {

    @Autowired
    private UserSessionRepository userSessionRepository;

    @Autowired
    QuestionHelper questionHelper;

    @Autowired
    private ObjectMapper objectMapper;

    public Question getAlternateQuestion(String sessionId) throws JsonProcessingException {
        UserSession session = userSessionRepository.findById(sessionId)
                .orElseThrow(() -> new UserSessionNotFoundException("Session ID not found: " + sessionId));

        Question currentQuestion = questionHelper.getQuestion(session, session.getCurrentQuestionIndex());

        Map<String, Question> sessionAlternateMap = objectMapper.readValue(session.getAlternateQuestionsJson(), new TypeReference<>() {
        });

        if (session.isUsedAlternate()) {
            throw new LifelineAlreadyUsedException("Alternate lifeline already used.");
        }

        Question alternateQuestion = sessionAlternateMap.get(currentQuestion.getDifficultyLevel());

        session.setUsedAlternate(true);
        session.setRemainingLifelines(session.getRemainingLifelines() - 1);
        userSessionRepository.save(session);
        return Question.copyOnlyQstnAndOptions(alternateQuestion);
    }

    public Question evaluateAnswerForAlternate(String sessionId, String option) throws JsonProcessingException {
        UserSession session = userSessionRepository.findById(sessionId)
                .orElseThrow(() -> new UserSessionNotFoundException("Session ID not found: " + sessionId));

        Map<String, Question> sessionAlternateMap = objectMapper.readValue(session.getAlternateQuestionsJson(), new TypeReference<>() {
        });

        Question question = questionHelper.getQuestion(session, session.getCurrentQuestionIndex());

        question = sessionAlternateMap.get(question.getDifficultyLevel());

        if (option.equalsIgnoreCase(question.getCorrectOption())) {
            session.setCurrentQuestionIndex(session.getCurrentQuestionIndex() + 1);
            return questionHelper.getQuestion(session, session.getCurrentQuestionIndex());
        }
        throw new IllegalArgumentException("Incorrect answer selected.");
    }

    public List<String> useFiftyFifty(String sessionId, Integer qno) throws JsonProcessingException {
        UserSession session = userSessionRepository.findById(sessionId)
                .orElseThrow(() -> new UserSessionNotFoundException("Session ID not found: " + sessionId));

        Question currentQuestion = questionHelper.getQuestion(session, qno);

        List<String> wrongOptions = new ArrayList<>();
        for (String eachOption : currentQuestion.getOptions()) {
            if (!eachOption.equalsIgnoreCase(currentQuestion.getCorrectOption())) {
                wrongOptions.add(eachOption);
            }
        }

        // Shuffle and pick 2
        Collections.shuffle(wrongOptions);
        return Arrays.asList(wrongOptions.get(0), wrongOptions.get(1));
    }

    public boolean useSecondChance(String sessionId, Integer qno, String option) throws JsonProcessingException {
        UserSession session = userSessionRepository.findById(sessionId).orElseThrow(() -> new UserSessionNotFoundException("Session ID not found: " + sessionId));
        if (session.isUsedSecondChance()) {
            throw new LifelineAlreadyUsedException("Second chance lifeline already used.");
        }
        session.setUsedSecondChance(true);
        return questionHelper.isRightAnswerChosen(session, qno, option);
    }
}
