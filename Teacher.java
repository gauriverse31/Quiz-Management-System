package model;

/**
 * Abstract base class for all users in the system.
 * Demonstrates ABSTRACTION and INHERITANCE.
 * Both Teacher and Student extend this class.
 */
public abstract class User {

    private String name;
    private String username;
    private String password;
    private String role; // "TEACHER" or "STUDENT"

    public User(String name, String username, String password, String role) {
        this.name     = name;
        this.username = username;
        this.password = password;
        this.role     = role;
    }

    // ── Getters ──────────────────────────────────────────────────────────────
    public String getName()     { return name; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole()     { return role; }

    /**
     * Abstract method — each subclass defines its own dashboard launch.
     * Demonstrates POLYMORPHISM via method overriding.
     */
    public abstract void openDashboard();

    @Override
    public String toString() {
        return role + ": " + name + " (@" + username + ")";
    }
}
