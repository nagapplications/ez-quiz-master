package com.naglabs.ezquizmaster.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.naglabs.ezquizmaster.dto.Question;
import com.naglabs.ezquizmaster.dto.QuestionResponse;
import com.naglabs.ezquizmaster.entity.UserSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class QuestionHelper {

    @Autowired
    private ObjectMapper objectMapper;

    public Question getQuestion(UserSession session, Integer qno) throws JsonProcessingException {
        Map<Integer, Question> primayQuestionMap = objectMapper.readValue(session.getPrimaryQuestionsJson(), new TypeReference<>() {
        });
        return primayQuestionMap.get(qno);
    }

    public QuestionResponse evaluateAnswer(UserSession session, Integer qno, String option) throws JsonProcessingException {
        if (isRightAnswerChosen(session, qno, option)) {
            if (qno == 15) {
                return new QuestionResponse("win", null, "Congratulations, you have answered all the 15 questions. Game is ended.");
            }
            Question nextQuestion = getQuestion(session, qno + 1);
            return new QuestionResponse("next", Question.copyOnlyQstnAndOptions(nextQuestion), null);
        }
        return new QuestionResponse("fail", null, "Incorrect answer selected.");
    }

    public boolean isRightAnswerChosen(UserSession session, Integer qno, String selectedOption) throws JsonProcessingException {
        Question currentQuestion = getQuestion(session, qno);
        return selectedOption.equalsIgnoreCase(currentQuestion.getCorrectOption());
    }
}
