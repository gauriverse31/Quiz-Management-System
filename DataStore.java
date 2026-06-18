package ui;

import model.*;
import service.DataStore;
import util.UITheme;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * 🎓 Student Dashboard
 * FIXED: Accepts displayName from LoginScreen, passes it to QuizScreen.
 * FIXED: Start Quiz button clearly visible.
 * FIXED: Pastel theme applied.
 */
public class StudentDashboard extends JFrame {

    private final Student student;
    private final String  displayName;
    private final DataStore dataStore = DataStore.getInstance();

    // FIXED: New constructor accepting displayName
    public StudentDashboard(Student student, String displayName) {
        this.student     = student;
        this.displayName = displayName.isEmpty() ? student.getName() : displayName;

        setTitle("QuizPro — Student Portal");
        setSize(520, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        UITheme.styleFrame(this);
        buildUI();
    }

    // Backward-compatible constructor
    public StudentDashboard(Student student) {
        this(student, student.getName());
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(UITheme.BG_DARK);

        // ── Header ────────────────────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.PASTEL_GREEN);
        header.setBorder(BorderFactory.createEmptyBorder(16, 22, 16, 22));

        JLabel titleLbl = new JLabel("🎓 Student Portal");
        titleLbl.setFont(UITheme.FONT_TITLE);
        titleLbl.setForeground(UITheme.TEXT_PRIMARY);

        // FIXED: Uses displayName entered at login
        JLabel nameLbl = new JLabel("Hello, " + displayName + "!");
        nameLbl.setFont(UITheme.FONT_BODY);
        nameLbl.setForeground(UITheme.PASTEL_GREEN_DARK);

        JPanel headerLeft = new JPanel(new GridLayout(2, 1, 0, 2));
        headerLeft.setBackground(UITheme.PASTEL_GREEN);
        headerLeft.add(titleLbl);
        headerLeft.add(nameLbl);

        JButton logoutBtn = UITheme.dangerButton("Logout");
        logoutBtn.addActionListener(e -> { dispose(); new LoginScreen().setVisible(true); });

        header.add(headerLeft,  BorderLayout.WEST);
        header.add(logoutBtn,   BorderLayout.EAST);

        // ── Stats Row ─────────────────────────────────────────────────────────
        List<Question> questions = dataStore.getQuestions();
        double[] stats = dataStore.getStats();

        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        statsPanel.setBackground(UITheme.BG_DARK);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(18, 25, 8, 25));
        statsPanel.add(statCard("📚 Questions",    String.valueOf(questions.size()), UITheme.ACCENT_BLUE));
        statsPanel.add(statCard("🏆 My Attempts",  countMyAttempts(),               UITheme.ACCENT_GREEN));
        statsPanel.add(statCard("📊 Avg Score",    String.format("%.1f%%", stats[1]),UITheme.ACCENT_PURPLE));

        // ── Quiz Card ─────────────────────────────────────────────────────────
        JPanel quizCard = new JPanel();
        quizCard.setLayout(new BoxLayout(quizCard, BoxLayout.Y_AXIS));
        quizCard.setBackground(UITheme.PASTEL_BLUE);
        quizCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.PASTEL_BLUE_DARK, 2),
            BorderFactory.createEmptyBorder(18, 22, 18, 22)
        ));

        JLabel quizTitle = new JLabel("📝 Available Quiz");
        quizTitle.setFont(UITheme.FONT_SUBTITLE);
        quizTitle.setForeground(UITheme.TEXT_PRIMARY);
        quizTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel quizInfo = new JLabel(questions.size() + " questions · 2 minute timer · Auto-submit");
        quizInfo.setFont(UITheme.FONT_BODY);
        quizInfo.setForeground(UITheme.TEXT_MUTED);
        quizInfo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel rulesLbl = new JLabel("<html><center>⚠️ Anti-cheat monitoring is enabled.<br>"
            + "Do not minimize window or switch tabs.</center></html>");
        rulesLbl.setFont(UITheme.FONT_SMALL);
        rulesLbl.setForeground(UITheme.ACCENT_ORANGE);
        rulesLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        // FIXED: Start Quiz button — clearly visible, properly added
        JButton startBtn = UITheme.successButton("▶  Start Quiz");
        startBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        startBtn.setMaximumSize(new Dimension(220, 44));
        startBtn.setPreferredSize(new Dimension(220, 44));
        startBtn.addActionListener(e -> startQuiz());

        quizCard.add(quizTitle);
        quizCard.add(Box.createVerticalStrut(6));
        quizCard.add(quizInfo);
        quizCard.add(Box.createVerticalStrut(10));
        quizCard.add(rulesLbl);
        quizCard.add(Box.createVerticalStrut(16));
        quizCard.add(startBtn);

        // ── Bottom ────────────────────────────────────────────────────────────
        JButton leaderBtn = UITheme.primaryButton("🏆 View Leaderboard");
        leaderBtn.addActionListener(e -> new LeaderboardDialog(this).setVisible(true));

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 12));
        bottomPanel.setBackground(UITheme.BG_DARK);
        bottomPanel.add(leaderBtn);

        JPanel centerPanel = new JPanel(new BorderLayout(0, 12));
        centerPanel.setBackground(UITheme.BG_DARK);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 25));
        centerPanel.add(statsPanel,   BorderLayout.NORTH);
        centerPanel.add(quizCard,     BorderLayout.CENTER);
        centerPanel.add(bottomPanel,  BorderLayout.SOUTH);

        root.add(header,       BorderLayout.NORTH);
        root.add(centerPanel,  BorderLayout.CENTER);
        add(root);

        // FIXED: revalidate to ensure all components render
        revalidate();
        repaint();
    }

    private void startQuiz() {
        List<Question> questions = dataStore.getQuestions();
        if (questions.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "No questions available. Ask your teacher to add questions.");
            return;
        }
        dispose();
        // FIXED: Pass displayName into QuizScreen so leaderboard shows correct name
        new QuizScreen(student, displayName).setVisible(true);
    }

    private String countMyAttempts() {
        long count = dataStore.getResults().stream()
            .filter(r -> r.getStudentUsername().equals(student.getUsername()))
            .count();
        return String.valueOf(count);
    }

    private JPanel statCard(String title, String value, Color accent) {
        JPanel card = new JPanel(new GridLayout(2, 1, 0, 4));
        card.setBackground(UITheme.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(accent, 1),
            BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));
        JLabel valueLbl = new JLabel(value, SwingConstants.CENTER);
        valueLbl.setFont(new Font("SansSerif", Font.BOLD, 22));
        valueLbl.setForeground(accent);
        JLabel titleLbl = new JLabel(title, SwingConstants.CENTER);
        titleLbl.setFont(UITheme.FONT_SMALL);
        titleLbl.setForeground(UITheme.TEXT_MUTED);
        card.add(valueLbl);
        card.add(titleLbl);
        return card;
    }
}
