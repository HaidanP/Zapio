package com.zapio;

import java.util.List;
import java.util.ArrayList;

/**
 * Represents a single quiz question with multiple choice options
 */
public class QuizQuestion {
    private String question;
    private List<String> options;
    private int correctOption;
    
    public QuizQuestion(String question, List<String> options, int correctOption) {
        this.question = question;
        this.options = new ArrayList<>(options);
        this.correctOption = correctOption;
    }
    
    // Simple constructor with just question and options
    public QuizQuestion(String question, List<String> options) {
        this.question = question;
        this.options = new ArrayList<>(options);
        this.correctOption = 0; // Default to first option
    }
    
    public String getQuestion() {
        return question;
    }
    
    public List<String> getOptions() {
        return options;
    }
    
    public int getCorrectOption() {
        return correctOption;
    }
    
    public void setCorrectOption(int correctOption) {
        this.correctOption = correctOption;
    }
    
    public String getOptionAt(int index) {
        if (index >= 0 && index < options.size()) {
            return options.get(index);
        }
        return "";
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(question).append("\n");
        for (int i = 0; i < options.size(); i++) {
            sb.append((i + 1)).append(". ").append(options.get(i)).append("\n");
        }
        return sb.toString();
    }
}
