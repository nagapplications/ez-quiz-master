package com.naglabs.ezquizmaster.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.naglabs.ezquizmaster.dto.OpenAiResponse;
import com.naglabs.ezquizmaster.dto.Question;
import com.naglabs.ezquizmaster.entity.UserSession;
import com.naglabs.ezquizmaster.exception.UserSessionNotFoundException;
import com.naglabs.ezquizmaster.repository.UserSessionRepository;
import com.naglabs.ezquizmaster.util.PromptGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Stream;

@Service
public class QuizService {

    @Autowired
    private OpenAiService openAiService;
    @Autowired
    private UserSessionRepository userSessionRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private LifelineService lifelineService;

    public String startQuiz(String email) throws Exception {
        // Fetch and parse questions
        String prompt = PromptGenerator.getPrompt(); // use earlier prompt
        OpenAiResponse openAiResponse = openAiService.generateQuestionsLocal(prompt);
        String content = openAiResponse.getChoices().getFirst().getMessage().getContent();
        List<Question> allQuestionsAI = objectMapper.readValue(content, new TypeReference<>() {
        });

        List<Question> orderedQstnList = Stream.of("easy", "medium", "hard", "evil").flatMap(difficulty -> allQuestionsAI.stream().filter(e -> e.getDifficultyLevel().equals(difficulty))).toList();

        Map<String, Question> lifeLineAlternateMap = new LinkedHashMap<>();
        Map<Integer, Question> uiDisplayQuestionMap = new LinkedHashMap<>();

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
        for (int i = 0; i <= uiDisplayQuestionList.size() - 1; i++) {
            Question eachQuestion = uiDisplayQuestionList.get(i);
            eachQuestion.setId(i);
            uiDisplayQuestionMap.put(i + 1, eachQuestion);
        }

        // Save session
        UserSession session = new UserSession();
        session.setSessionId(UUID.randomUUID().toString());
        session.setUserEmail(email);
        session.setCurrentQuestionIndex(1);
        session.setScore(1);
        session.setPrimaryQuestionsJson(objectMapper.writeValueAsString(uiDisplayQuestionMap));
        session.setAlternateQuestionsJson(objectMapper.writeValueAsString(lifeLineAlternateMap));
        userSessionRepository.save(session);

        return session.getSessionId(); // return to frontend
    }

    public Question getQuestion(String sessionId) throws JsonProcessingException {
        Question firstQuestion = getQuestionFromSession(sessionId, false);
        return Question.copyOnlyQstnAndOptions(firstQuestion);
    }

    public Question submitAnswer(String sessionId, String selectedOption) throws Exception {
        Question currentQuestion = getQuestionFromSession(sessionId, false);
        boolean isRightAnswerChosen = currentQuestion.getCorrectOption().equalsIgnoreCase(selectedOption);
        if (isRightAnswerChosen) {
            Question nextQuestionFromSession = getQuestionFromSession(sessionId, true);
            return Question.copyOnlyQstnAndOptions(nextQuestionFromSession);
        } else {
            //put custom expception WrongAnswerException
            throw new IllegalArgumentException("Incorrect answer selected.");
        }
    }

    public Question getQuestionFromSession(String sessionId, Boolean next) throws JsonProcessingException {
        UserSession session = userSessionRepository.findById(sessionId).orElseThrow(() -> new UserSessionNotFoundException("Session ID not found: " + sessionId));

        if (session.getCurrentAlternateQuestionWithDifficultyLevel() != null) {
            return lifelineService.getAlternateQuestionServedCurrently(session);
        }

        Map<Integer, Question> sessionOriginalQuestionMap = objectMapper.readValue(session.getPrimaryQuestionsJson(), new TypeReference<>() {
        });

        int qno = session.getCurrentQuestionIndex();

        if (next) {
            qno += 1;
            session.setCurrentQuestionIndex(qno);
            session.setScore(session.getScore() + 10);
            userSessionRepository.save(session); // Persist changes
        }

        return sessionOriginalQuestionMap.get(qno);
    }

    public List<String> useFiftyFifty(String sessionId) throws JsonProcessingException {
        Question currentQuestion = getQuestionFromSession(sessionId, false);

        List<String> wrongOptions = new ArrayList<>();
        for (String eachOption : currentQuestion.getOptions()) {
            if (!eachOption.equalsIgnoreCase(currentQuestion.getCorrectOption())) {
                wrongOptions.add(eachOption);
            }
        }

        // Shuffle and pick 2
        Collections.shuffle(wrongOptions);
        System.out.println(wrongOptions.get(0));
        System.out.println(wrongOptions.get(1));
        return Arrays.asList(wrongOptions.get(0), wrongOptions.get(1));
    }
}

