package com.naglabs.ezquizmaster.dto;

import java.util.List;

public class ResultRequest {
    private String userEmail;
    private int totalScore;
    private int correctAnswers;
    private int lifelinesUsed;
    private List<String> questionSummaries;
}
