package ui;

import model.*;
import service.DataStore;
import util.UITheme;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * 📊 Result Screen
 * FIXED: Accepts displayName, passes it to StudentDashboard on return.
 * FIXED: Pastel theme applied.
 */
public class ResultScreen extends JFrame {

    private final Student        student;
    private final String         displayName;
    private final QuizResult     result;
    private final int[]          userAnswers;
    private final List<Question> questions;
    private final DataStore      dataStore = DataStore.getInstance();

    // FIXED: New constructor accepts displayName
    public ResultScreen(Student student, String displayName, QuizResult result,
                        int[] userAnswers, List<Question> questions) {
        this.student     = student;
        this.displayName = displayName;
        this.result      = result;
        this.userAnswers = userAnswers;
        this.questions   = questions;

        setTitle("QuizPro — Your Results");
        setSize(720, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        UITheme.styleFrame(this);
        buildUI();
    }

    // Backward-compatible
    public ResultScreen(Student student, QuizResult result,
                        int[] userAnswers, List<Question> questions) {
        this(student, student.getName(), result, userAnswers, questions);
    }

    private void buildUI() {
        setLayout(new BorderLayout(0, 0));

        // ── Score Header ──────────────────────────────────────────────────────
        add(buildScoreHeader(), BorderLayout.NORTH);

        // ── Question Review ───────────────────────────────────────────────────
        JPanel reviewPanel = new JPanel();
        reviewPanel.setLayout(new BoxLayout(reviewPanel, BoxLayout.Y_AXIS));
        reviewPanel.setBackground(UITheme.BG_DARK);
        reviewPanel.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));

        JLabel reviewTitle = new JLabel("📋 Question Review");
        reviewTitle.setFont(UITheme.FONT_SUBTITLE);
        reviewTitle.setForeground(UITheme.TEXT_PRIMARY);
        reviewPanel.add(reviewTitle);
        reviewPanel.add(Box.createVerticalStrut(10));

        for (int i = 0; i < questions.size(); i++) {
            reviewPanel.add(buildQuestionReview(i));
            reviewPanel.add(Box.createVerticalStrut(8));
        }

        JScrollPane scrollPane = new JScrollPane(reviewPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(UITheme.BG_DARK);
        add(scrollPane, BorderLayout.CENTER);

        // ── Bottom Buttons ────────────────────────────────────────────────────
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 12));
        bottomBar.setBackground(UITheme.PASTEL_BLUE);
        bottomBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.BORDER_COLOR));

        JButton leaderBtn    = UITheme.primaryButton("🏆 View Leaderboard");
        JButton dashboardBtn = UITheme.successButton("🏠 Dashboard");
        JButton loginBtn     = UITheme.dangerButton("🔓 Back to Login");

        leaderBtn.addActionListener(e -> new LeaderboardDialog(this).setVisible(true));
        dashboardBtn.addActionListener(e -> {
            dispose();
            new StudentDashboard(student, displayName).setVisible(true);
        });
        loginBtn.addActionListener(e -> {
            dispose();
            new LoginScreen();
        });

        bottomBar.add(leaderBtn);
        bottomBar.add(dashboardBtn);
        bottomBar.add(loginBtn);
        add(bottomBar, BorderLayout.SOUTH);
    }

    private JPanel buildScoreHeader() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 12, 0));
        panel.setBackground(UITheme.PASTEL_GREEN);
        panel.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));

        double pct = result.getPercentage();
        Color scoreColor = pct >= 70 ? UITheme.ACCENT_GREEN
                         : pct >= 40 ? UITheme.ACCENT_ORANGE
                         : UITheme.ACCENT_RED;

        panel.add(statCard("Score",
            result.getScore() + "/" + result.getTotalQuestions(), scoreColor));
        panel.add(statCard("Percentage",
            String.format("%.1f%%", pct), scoreColor));
        panel.add(statCard("Time Taken",
            result.getFormattedTime(), UITheme.ACCENT_BLUE));
        panel.add(statCard("Your Rank",
            "#" + calculateRank(), UITheme.ACCENT_PURPLE));
        return panel;
    }

    private JPanel buildQuestionReview(int idx) {
        Question q       = questions.get(idx);
        int      userAns = userAnswers[idx];
        boolean  correct = (userAns != -1) && q.isCorrect(userAns);
        boolean  skipped = (userAns == -1);

        Color borderColor = correct ? UITheme.ACCENT_GREEN
                          : skipped ? UITheme.ACCENT_ORANGE
                          : UITheme.ACCENT_RED;

        JPanel card = new JPanel(new BorderLayout(8, 5));
        card.setBackground(UITheme.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 4, 0, 0, borderColor),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        String statusIcon = correct ? "✅" : skipped ? "⏭" : "❌";
        JLabel statusLbl  = new JLabel(statusIcon + " Q" + (idx + 1));
        statusLbl.setFont(new Font("SansSerif", Font.BOLD, 13));
        statusLbl.setForeground(borderColor);
        statusLbl.setPreferredSize(new Dimension(55, 20));

        JLabel qText = new JLabel("<html>" + q.getQuestionText() + "</html>");
        qText.setFont(UITheme.FONT_BODY);
        qText.setForeground(UITheme.TEXT_PRIMARY);

        String correctAns = (char)('A' + q.getCorrectOptionIndex()) + ") "
            + q.getOptions()[q.getCorrectOptionIndex()];
        String yourAns = skipped ? "Skipped" :
            (char)('A' + userAns) + ") " + q.getOptions()[userAns];
        String ansText = correct
            ? "✅ " + yourAns
            : (skipped ? "⏭ Skipped | Correct: " + correctAns
                       : "❌ Your: " + yourAns + " | ✅ Correct: " + correctAns);

        JLabel ansLbl = new JLabel("<html><small style='color:#888'>" + ansText + "</small></html>");

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 3));
        textPanel.setBackground(UITheme.BG_CARD);
        textPanel.add(qText);
        textPanel.add(ansLbl);

        card.add(statusLbl,  BorderLayout.WEST);
        card.add(textPanel,  BorderLayout.CENTER);
        return card;
    }

    private int calculateRank() {
        List<QuizResult> leaderboard = dataStore.getLeaderboard();
        for (int i = 0; i < leaderboard.size(); i++) {
            QuizResult r = leaderboard.get(i);
            if (r.getStudentUsername().equals(student.getUsername())
                    && r.getScore() == result.getScore()) {
                return i + 1;
            }
        }
        return leaderboard.size();
    }

    private JPanel statCard(String title, String value, Color accent) {
        JPanel card = new JPanel(new GridLayout(2, 1, 0, 4));
        card.setBackground(UITheme.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(accent, 1),
            BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));
        JLabel valueLbl = new JLabel(value, SwingConstants.CENTER);
        valueLbl.setFont(new Font("SansSerif", Font.BOLD, 20));
        valueLbl.setForeground(accent);
        JLabel titleLbl = new JLabel(title, SwingConstants.CENTER);
        titleLbl.setFont(UITheme.FONT_SMALL);
        titleLbl.setForeground(UITheme.TEXT_MUTED);
        card.add(valueLbl);
        card.add(titleLbl);
        return card;
    }
}
