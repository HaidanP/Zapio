package com.zapio;

/**
 * Represents a single flashcard with a question (front) and answer (back)
 */
public class Flashcard {
    private String question;
    private String answer;
    
    public Flashcard(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }
    
    public String getQuestion() {
        return question;
    }
    
    public String getAnswer() {
        return answer;
    }
    
    @Override
    public String toString() {
        return "Flashcard{" +
                "question='" + question + '\'' +
                ", answer='" + answer + '\'' +
                '}';
    }
}
