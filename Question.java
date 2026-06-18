package model;

import ui.StudentDashboard;

/**
 * Represents a Student who can attempt quizzes.
 * Extends User — demonstrates INHERITANCE.
 */
public class Student extends User {

    public Student(String name, String username, String password) {
        super(name, username, password, "STUDENT");
    }

    /**
     * Polymorphic implementation — opens StudentDashboard.
     */
    @Override
    public void openDashboard() {
        new StudentDashboard(this).setVisible(true);
    }
}
