package model;

import ui.TeacherDashboard;

/**
 * Represents a Teacher who can add/manage questions.
 * Extends User — demonstrates INHERITANCE.
 */
public class Teacher extends User {

    public Teacher(String name, String username, String password) {
        super(name, username, password, "TEACHER");
    }

    /**
     * Polymorphic implementation — opens TeacherDashboard.
     */
    @Override
    public void openDashboard() {
        new TeacherDashboard(this).setVisible(true);
    }
}
