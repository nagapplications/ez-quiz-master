package com.naglabs.ezquizmaster.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Question {

    private Integer id;
    private String question;
    private List<String> options;
    private String difficultyLevel; // easy, medium, hard, evil
    private String correctOption;

    public Question() {
    }

    public Question(Integer id,String question, List<String> options) {
        this.id = id;
        this.question = question;
        this.options = new ArrayList<>(options); // defensive copy even if caller forgets
    }

    public static Question copyOnlyQstnAndOptions(Question other) {
        return new Question(other.id, other.question, new ArrayList<>(other.options));
    }
}
