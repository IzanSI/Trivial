package com.example.trivial;

import java.util.List;

public class Question {
    private String question;
    private String correctAnswer;
    private List<String> options;

    public Question(String question, String correctAnswer, List<String> options) {
        this.question = question;
        this.correctAnswer = correctAnswer;
        this.options = options;
    }

    public String getQuestion() {
        return question;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public List<String> getOptions() {
        return options;
    }
}
