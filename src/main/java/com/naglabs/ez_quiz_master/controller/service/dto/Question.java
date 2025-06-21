package com.naglabs.ez_quiz_master.controller.service.dto;

import java.util.List;

public class Question {
    private String id;
    private String difficulty; // easy, medium, hard, evil
    private String questionText;
    private List<String> options;
    private String correctAnswer;
    private String alternateQuestionId;
}

