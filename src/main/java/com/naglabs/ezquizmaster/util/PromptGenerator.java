package com.naglabs.ezquizmaster.util;

public class PromptGenerator {
    public static String getPrompt() {
        return """
                Generate 19 multiple-choice quiz questions in the following JSON format:
                [{"question": "What is the capital of France?","options": ["London","Berlin","Paris","Rome"],"correctOption": "Paris","difficultyLevel": "easy"},...]
                `Requirements:
                - Exactly 19 questions
                - DifficultyLevel breakdown:
                  - Exactly 6 easy questions
                  - Exactly 6 medium questions
                  - Exactly 5 hard questions
                  - Exactly 2 evil questions
                - After generating response, validate against above requirements or regenerate
                - Ensure the output is strictly valid JSON with no comments or extra text or markdown formatting such as triple backticks.
                """;
    }
}

