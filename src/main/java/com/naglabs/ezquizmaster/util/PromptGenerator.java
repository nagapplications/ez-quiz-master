package com.naglabs.ezquizmaster.util;

public class PromptGenerator {
    public static String getPrompt() {
        return """
                Generate 19 multiple-choice quiz questions in the following JSON format:

                [
                  {
                    "question": "What is the capital of France?",
                    "options": {
                      "A": "London",
                      "B": "Berlin",
                      "C": "Paris",
                      "D": "Rome"
                    },
                    "correctOption": "C",
                    "difficulty": "easy"
                  },
                  ...
                ]

                Requirements:
                - Exactly 19 questions
                - Difficulty breakdown:
                  - 6 easy (last one will be alternate)
                  - 6 medium (last one will be alternate)
                  - 5 hard (last one will be alternate)
                  - 2 evil (last one will be alternate)
                - Ensure the output is strictly valid JSON with no comments or extra text.
                """;
    }
}

