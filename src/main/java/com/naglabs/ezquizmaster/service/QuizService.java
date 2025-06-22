package com.naglabs.ezquizmaster.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.naglabs.ezquizmaster.dto.OpenAiResponse;
import com.naglabs.ezquizmaster.dto.Question;
import com.naglabs.ezquizmaster.entity.UserSession;
import com.naglabs.ezquizmaster.exception.LifelineAlreadyUsedException;
import com.naglabs.ezquizmaster.exception.OpenAiResponseParseException;
import com.naglabs.ezquizmaster.exception.UserSessionNotFoundException;
import com.naglabs.ezquizmaster.repository.UserSessionRepository;
import com.naglabs.ezquizmaster.util.PromptGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuizService {

    @Autowired
    private OpenAiService openAiService;
    @Autowired
    private UserSessionRepository userSessionRepository;
    @Autowired
    private ObjectMapper objectMapper;

    public String startQuiz(String email) throws Exception {
        // Fetch and parse questions
        String prompt = PromptGenerator.getPrompt(); // use earlier prompt
        openAiService.generateQuestions(prompt);
       // String content = response.getChoices().get(0).message.content;
        List<Question> allQuestions = new ArrayList<>();//objectMapper.readValue(response.getChoices().get(0)), new TypeReference<>() {
        //});

        // Group by difficulty
        Map<String, List<Question>> grouped = allQuestions.stream()
                .collect(Collectors.groupingBy(Question::getDifficulty));

        // Separate primary & alternate
        List<Question> alternate = new ArrayList<>();
        List<Question> primary = new ArrayList<>();
        for (List<Question> group : grouped.values()) {
            alternate.add(group.get(group.size() - 1));
            primary.addAll(group.subList(0, group.size() - 1));
        }

        // Save session
        UserSession session = new UserSession();
        session.setSessionId(UUID.randomUUID().toString());
        session.setUserEmail(email);
        session.setPrimaryQuestionsJson(objectMapper.writeValueAsString(primary));
        session.setAlternateQuestionsJson(objectMapper.writeValueAsString(alternate));
        userSessionRepository.save(session);

        return session.getSessionId(); // return to frontend
    }

    public Question getNextQuestion(String sessionId) throws Exception {
        UserSession session = userSessionRepository.findById(sessionId)
                .orElseThrow(() -> new UserSessionNotFoundException("Session ID not found: " + sessionId));


        List<Question> primary = objectMapper.readValue(session.getPrimaryQuestionsJson(), new TypeReference<>() {
        });
        int index = session.getCurrentQuestionIndex();

        if (index >= primary.size()) return null; // no more questions

        return primary.get(index);
    }

    public boolean submitAnswer(String sessionId, String selectedOption) throws Exception {
        UserSession session = userSessionRepository.findById(sessionId)
                .orElseThrow(() -> new UserSessionNotFoundException("Session ID not found: " + sessionId));

        List<Question> primary = objectMapper.readValue(session.getPrimaryQuestionsJson(), new TypeReference<>() {
        });

        int index = session.getCurrentQuestionIndex();
        Question current = primary.get(index);

        Boolean isRightAnswerChoosen = current.getCorrectAnswer().equalsIgnoreCase(selectedOption);

        if (isRightAnswerChoosen) {
            // Handle correct
            session.setScore(session.getScore() + 10);
            session.setCurrentQuestionIndex(index + 1);
            session.setUsedSecondChance(false);
        } else if (session.isUsedSecondChance()) {
            // Just consume the second chance
            session.setUsedSecondChance(false); // Next wrong = game over
        } else {
            // Lose a life or game over
            session.setRemainingLifelines(session.getRemainingLifelines() - 1);
        }

        return isRightAnswerChoosen;
    }

    public Question useAlternateQuestion(String sessionId) {
        UserSession session = userSessionRepository.findById(sessionId)
                .orElseThrow(() -> new UserSessionNotFoundException("Session ID not found: " + sessionId));


        if (session.isUsedAlternate()) {
            throw new LifelineAlreadyUsedException("Alternate lifeline already used.");
        }

        List<Question> alternate;
        List<Question> primary;
        try {
            alternate = objectMapper.readValue(session.getAlternateQuestionsJson(), new TypeReference<>() {
            });
            primary = objectMapper.readValue(session.getPrimaryQuestionsJson(), new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new OpenAiResponseParseException("Failed to parse OpenAI response", e);
        }

        Question alternateQ = alternate.stream()
                .filter(q -> q.getDifficulty().equalsIgnoreCase(primary.get(session.getCurrentQuestionIndex()).getDifficulty()))
                .findFirst()
                .orElseThrow();

        session.setUsedAlternate(true);
        userSessionRepository.save(session);
        return alternateQ;
    }

    public List<String> useFiftyFifty(String sessionId) {
        UserSession session = userSessionRepository.findById(sessionId)
                .orElseThrow(() -> new UserSessionNotFoundException("Session ID not found: " + sessionId));


        if (session.isUsedFiftyFifty()) throw new IllegalStateException("50-50 lifeline already used");

        List<Question> primary;
        try {
            primary = objectMapper.readValue(session.getPrimaryQuestionsJson(), new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new OpenAiResponseParseException("Failed to parse OpenAI response", e);
        }
        Question current = primary.get(session.getCurrentQuestionIndex());

        List<String> options = new ArrayList<>(current.getOptions());

        // Remove two incorrect answers
        options.removeIf(opt -> !opt.equalsIgnoreCase(current.getCorrectAnswer()));
        Random random = new Random();

        while (options.size() < 2) {
            String randomIncorrect = current.getOptions().get(random.nextInt(4));
            if (!randomIncorrect.equalsIgnoreCase(current.getCorrectAnswer()) && !options.contains(randomIncorrect)) {
                options.add(randomIncorrect);
            }
        }

        session.setUsedFiftyFifty(true);
        userSessionRepository.save(session);

        return options;
    }

    public void useSecondChance(String sessionId) {
        UserSession session = userSessionRepository.findById(sessionId)
                .orElseThrow(() -> new UserSessionNotFoundException("Session ID not found: " + sessionId));


        if (session.isUsedSecondChance()) throw new IllegalStateException("Second chance already used");

        session.setUsedSecondChance(true);
        session.setUsedSecondChance(true); // Will be reset after this question
        userSessionRepository.save(session);
    }

}

