package model;

import java.io.Serializable;

/**
 * Represents a quiz question with options, answer, difficulty, and topic.
 * Implements Serializable for file-based persistence.
 */
public class Question implements Serializable {

    // Enum for difficulty levels — Abstraction via type safety
    public enum Difficulty {
        EASY, MEDIUM, HARD
    }

    private static final long serialVersionUID = 1L;

    private String questionText;
    private String[] options;       // Always 4 options (A, B, C, D)
    private int correctOptionIndex; // 0-based index
    private Difficulty difficulty;
    private String topic;

    // Constructor — full initialization (Encapsulation)
    public Question(String questionText, String[] options, int correctOptionIndex,
                    Difficulty difficulty, String topic) {
        if (options == null || options.length != 4)
            throw new IllegalArgumentException("A question must have exactly 4 options.");
        if (correctOptionIndex < 0 || correctOptionIndex > 3)
            throw new IllegalArgumentException("Correct option index must be 0-3.");

        this.questionText    = questionText;
        this.options         = options;
        this.correctOptionIndex = correctOptionIndex;
        this.difficulty      = difficulty;
        this.topic           = topic;
    }

    // ── Getters ──────────────────────────────────────────────────────────────
    public String getQuestionText()    { return questionText; }
    public String[] getOptions()       { return options; }
    public int getCorrectOptionIndex() { return correctOptionIndex; }
    public Difficulty getDifficulty()  { return difficulty; }
    public String getTopic()           { return topic; }

    // ── Setters ──────────────────────────────────────────────────────────────
    public void setQuestionText(String questionText) { this.questionText = questionText; }
    public void setTopic(String topic)               { this.topic = topic; }
    public void setDifficulty(Difficulty difficulty) { this.difficulty = difficulty; }

    /** Checks if the given answer index is correct. */
    public boolean isCorrect(int answerIndex) {
        return answerIndex == correctOptionIndex;
    }

    @Override
    public String toString() {
        return "[" + difficulty + "] (" + topic + ") " + questionText;
    }
}
