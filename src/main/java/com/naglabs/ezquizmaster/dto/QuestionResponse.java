package com.naglabs.ezquizmaster.dto;

import lombok.Data;

@Data
public class QuestionResponse {
    private String status;
    private Question question;
    private String message;

    public QuestionResponse(String status, Question question, String message) {
        this.status = status;
        this.question = question;
        this.message = message;
    }

}
