package model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Stores the result of a student's quiz attempt.
 * Used in leaderboard and result comparison.
 */
public class QuizResult implements Serializable, Comparable<QuizResult> {

    private static final long serialVersionUID = 2L;

    private String studentName;
    private String studentUsername;
    private int score;
    private int totalQuestions;
    private long timeTakenSeconds; // How long the student took
    private String timestamp;

    public QuizResult(String studentName, String studentUsername,
                      int score, int totalQuestions, long timeTakenSeconds) {
        this.studentName     = studentName;
        this.studentUsername = studentUsername;
        this.score           = score;
        this.totalQuestions  = totalQuestions;
        this.timeTakenSeconds = timeTakenSeconds;
        // Record submission timestamp
        this.timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
    }

    // ── Getters ──────────────────────────────────────────────────────────────
    public String getStudentName()     { return studentName; }
    public String getStudentUsername() { return studentUsername; }
    public int getScore()              { return score; }
    public int getTotalQuestions()     { return totalQuestions; }
    public long getTimeTakenSeconds()  { return timeTakenSeconds; }
    public String getTimestamp()       { return timestamp; }

    /** Returns score as a percentage (0–100). */
    public double getPercentage() {
        if (totalQuestions == 0) return 0;
        return (score * 100.0) / totalQuestions;
    }

    /** Returns formatted time string, e.g. "1m 45s" */
    public String getFormattedTime() {
        long mins = timeTakenSeconds / 60;
        long secs = timeTakenSeconds % 60;
        return mins + "m " + secs + "s";
    }

    /**
     * Natural sort: higher score first; if equal, faster time wins.
     */
    @Override
    public int compareTo(QuizResult other) {
        if (other.score != this.score)
            return Integer.compare(other.score, this.score);
        return Long.compare(this.timeTakenSeconds, other.timeTakenSeconds);
    }

    @Override
    public String toString() {
        return studentName + " | " + score + "/" + totalQuestions
                + " (" + String.format("%.1f", getPercentage()) + "%) | " + getFormattedTime();
    }
}
