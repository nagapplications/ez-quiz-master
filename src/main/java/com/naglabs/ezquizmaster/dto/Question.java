package com.naglabs.ezquizmaster.dto;

import lombok.Data;

import java.util.List;

@Data
public class Question {
    private String id;
    private String difficulty; // easy, medium, hard, evil
    private String questionText;
    private List<String> options;
    private String correctAnswer;
    private String alternateQuestionId;
}

//"question": "What is the capital of France?",
//        "options": {
//        "A": "London",
//        "B": "Berlin",
//        "C": "Paris",
//        "D": "Rome"
//        },
//        "correctOption": "C",
//        "difficulty": "easy"
