package com.naglabs.ezquizmaster.dto;

import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class QuestionResponse {

    private List<Question> questions;
}
