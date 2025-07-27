package com.naglabs.ezquizmaster.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.naglabs.ezquizmaster.dto.OpenAiResponse;
import com.naglabs.ezquizmaster.dto.Question;
import com.naglabs.ezquizmaster.entity.UserSession;
import com.naglabs.ezquizmaster.repository.UserSessionRepository;
import com.naglabs.ezquizmaster.util.PromptGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Stream;

@Service
public class GameLaunchService {
    @Autowired
    private OpenAiService openAiService;
    @Autowired
    private UserSessionRepository userSessionRepository;
    @Autowired
    private ObjectMapper objectMapper;

    public String startQuiz(String email) throws Exception {
        // Fetch and parse questions
        String prompt = PromptGenerator.getPrompt();
        OpenAiResponse openAiResponse = openAiService.generateQuestionsLocal(prompt);
        String content = openAiResponse.getChoices().getFirst().getMessage().getContent();
        List<Question> allQuestionsAI = objectMapper.readValue(content, new TypeReference<>() {
        });

        Map<Integer, Question> uiDisplayQuestionMap = new LinkedHashMap<>();
        Map<String, Question> lifeLineAlternateMap = new LinkedHashMap<>();

        organizeGameQuestions(allQuestionsAI, uiDisplayQuestionMap, lifeLineAlternateMap);

        UserSession session = saveAndGetSession(email, uiDisplayQuestionMap, lifeLineAlternateMap);

        return session.getSessionId();
    }

    private static void organizeGameQuestions(List<Question> allQuestionsAI, Map<Integer, Question> uiDisplayQuestionMap, Map<String, Question> lifeLineAlternateMap) {
        List<Question> orderedQstnList = Stream.of("easy", "medium", "hard", "evil")
                .flatMap(difficulty -> allQuestionsAI.stream()
                        .filter(e -> e.getDifficultyLevel().equals(difficulty))).toList();

        // Add easy
        List<Question> uiDisplayQuestionList = new ArrayList<>(orderedQstnList.subList(0, 5));
        Question altQuestion = orderedQstnList.get(5);
        lifeLineAlternateMap.put(altQuestion.getDifficultyLevel(), altQuestion);

        // Add medium
        uiDisplayQuestionList.addAll(orderedQstnList.subList(6, 11));
        altQuestion = orderedQstnList.get(11);
        lifeLineAlternateMap.put(altQuestion.getDifficultyLevel(), altQuestion);

        // Add hard
        uiDisplayQuestionList.addAll(orderedQstnList.subList(12, 16));
        altQuestion = orderedQstnList.get(16);
        lifeLineAlternateMap.put(altQuestion.getDifficultyLevel(), altQuestion);

        // Add evil
        uiDisplayQuestionList.add(orderedQstnList.get(17));
        altQuestion = orderedQstnList.get(18);
        lifeLineAlternateMap.put(altQuestion.getDifficultyLevel(), altQuestion);

        //Add Qno to questions and Map
        for (int i = 0; i <= uiDisplayQuestionList.size()-1; i++) {
            Question eachQuestion = uiDisplayQuestionList.get(i);
            eachQuestion.setId(i+1);
            uiDisplayQuestionMap.put(i+1, eachQuestion);
        }
    }

    private UserSession saveAndGetSession(String email, Map<Integer, Question> uiDisplayQuestionMap, Map<String, Question> lifeLineAlternateMap) throws JsonProcessingException {
        UserSession session = new UserSession();
        session.setSessionId(UUID.randomUUID().toString());
        session.setUserEmail(email);
        session.setCurrentQuestionIndex(1);
        session.setScore(1);
        session.setPrimaryQuestionsJson(objectMapper.writeValueAsString(uiDisplayQuestionMap));
        session.setAlternateQuestionsJson(objectMapper.writeValueAsString(lifeLineAlternateMap));
        userSessionRepository.save(session);
        return session;
    }

}
