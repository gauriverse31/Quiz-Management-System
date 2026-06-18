package ui;

import model.*;
import service.*;
import util.UITheme;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * ⏱️ Quiz Screen
 * FIXED: Accepts displayName so leaderboard shows actual student name.
 * FIXED: Submit Quiz button is always visible (proper layout).
 * FIXED: Pastel theme applied.
 */
public class QuizScreen extends JFrame {

    private final Student student;
    private final String  displayName;   // FIXED: actual name for leaderboard
    private final DataStore dataStore = DataStore.getInstance();
    private List<Question> questions;

    private int   currentIndex = 0;
    private int[] userAnswers;

    private QuizTimer         quizTimer;
    private MonitoringService monitoring;

    private JLabel       timerLabel;
    private JLabel       questionCountLabel;
    private JLabel       questionTextLabel;
    private JRadioButton[] optionButtons = new JRadioButton[4];
    private ButtonGroup  buttonGroup;
    private JButton      prevBtn, nextBtn, submitBtn;
    private JPanel       monitorPanel;

    private long quizStartTime;

    // FIXED: Constructor now accepts displayName
    public QuizScreen(Student student, String displayName) {
        this.student      = student;
        this.displayName  = displayName.isEmpty() ? student.getName() : displayName;
        this.questions    = dataStore.getQuestions();
        this.userAnswers  = new int[questions.size()];
        java.util.Arrays.fill(userAnswers, -1);

        setTitle("QuizPro — Quiz in Progress");
        setSize(760, 620);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        UITheme.styleFrame(this);

        buildUI();
        startServices();
        showQuestion(0);

        // FIXED: revalidate to ensure all components are visible
        revalidate();
        repaint();
    }

    // Backward-compatible
    public QuizScreen(Student student) {
        this(student, student.getName());
    }

    private void buildUI() {
        setLayout(new BorderLayout(0, 0));

        // ── TOP BAR ───────────────────────────────────────────────────────────
        JPanel topBar = new JPanel(new BorderLayout(0, 0));
        topBar.setBackground(UITheme.PASTEL_BLUE);
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));

        timerLabel = new JLabel("⏱  02:00", SwingConstants.CENTER);
        timerLabel.setFont(new Font("Monospaced", Font.BOLD, 22));
        timerLabel.setForeground(UITheme.PASTEL_GREEN_DARK);
        timerLabel.setPreferredSize(new Dimension(150, 40));

        questionCountLabel = new JLabel("Question 1 / " + questions.size(), SwingConstants.CENTER);
        questionCountLabel.setFont(UITheme.FONT_SUBTITLE);
        questionCountLabel.setForeground(UITheme.TEXT_PRIMARY);

        monitoring   = new MonitoringService(this, this::autoSubmit);
        monitorPanel = monitoring.createStatusPanel();

        topBar.add(timerLabel,         BorderLayout.WEST);
        topBar.add(questionCountLabel, BorderLayout.CENTER);
        topBar.add(monitorPanel,       BorderLayout.EAST);

        // ── QUESTION CARD ─────────────────────────────────────────────────────
        JPanel questionCard = new JPanel(new BorderLayout(0, 15));
        questionCard.setBackground(UITheme.BG_CARD);
        questionCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));

        questionTextLabel = new JLabel("", SwingConstants.LEFT);
        questionTextLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        questionTextLabel.setForeground(UITheme.TEXT_PRIMARY);

        JPanel optionsPanel = new JPanel(new GridLayout(4, 1, 0, 10));
        optionsPanel.setBackground(UITheme.BG_CARD);
        buttonGroup = new ButtonGroup();

        for (int i = 0; i < 4; i++) {
            optionButtons[i] = new JRadioButton();
            optionButtons[i].setFont(UITheme.FONT_BODY);
            optionButtons[i].setForeground(UITheme.TEXT_PRIMARY);
            optionButtons[i].setBackground(UITheme.PASTEL_BLUE);
            optionButtons[i].setOpaque(true);
            optionButtons[i].setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
            optionButtons[i].setCursor(new Cursor(Cursor.HAND_CURSOR));
            final int idx = i;
            optionButtons[i].addActionListener(e -> userAnswers[currentIndex] = idx);
            buttonGroup.add(optionButtons[i]);
            optionsPanel.add(optionButtons[i]);
        }

        questionCard.add(questionTextLabel, BorderLayout.NORTH);
        questionCard.add(optionsPanel,      BorderLayout.CENTER);

        // ── NAVIGATION BAR ────────────────────────────────────────────────────
        // FIXED: Submit button uses FlowLayout so it's always visible
        JPanel navBar = new JPanel(new BorderLayout(10, 0));
        navBar.setBackground(UITheme.CREAM_CARD);
        navBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.BORDER_COLOR),
            BorderFactory.createEmptyBorder(12, 20, 12, 20)
        ));

        prevBtn   = UITheme.primaryButton("← Previous");
        nextBtn   = UITheme.primaryButton("Next →");
        submitBtn = UITheme.successButton("✔ Submit Quiz");  // FIXED: always added

        prevBtn.addActionListener(e   -> navigate(-1));
        nextBtn.addActionListener(e   -> navigate(1));
        submitBtn.addActionListener(e -> confirmSubmit());

        JPanel leftBtns  = new JPanel(new FlowLayout(FlowLayout.LEFT,  8, 0));
        JPanel rightBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        leftBtns.setBackground(UITheme.CREAM_CARD);
        rightBtns.setBackground(UITheme.CREAM_CARD);

        leftBtns.add(prevBtn);
        leftBtns.add(nextBtn);
        rightBtns.add(submitBtn);  // FIXED: submitBtn added to panel before setVisible

        navBar.add(leftBtns,  BorderLayout.WEST);
        navBar.add(rightBtns, BorderLayout.EAST);

        // ── CENTER WRAPPER ────────────────────────────────────────────────────
        JPanel centerWrapper = new JPanel(new BorderLayout(0, 12));
        centerWrapper.setBackground(UITheme.BG_DARK);
        centerWrapper.setBorder(BorderFactory.createEmptyBorder(15, 20, 0, 20));
        centerWrapper.add(questionCard, BorderLayout.CENTER);

        add(topBar,        BorderLayout.NORTH);
        add(centerWrapper, BorderLayout.CENTER);
        add(navBar,        BorderLayout.SOUTH);
    }

    private void startServices() {
        quizStartTime = System.currentTimeMillis();
        quizTimer = new QuizTimer(120, timerLabel, this::autoSubmit);
        quizTimer.start();
        monitoring.startMonitoring();
    }

    private void showQuestion(int index) {
        if (index < 0 || index >= questions.size()) return;
        currentIndex = index;
        Question q = questions.get(index);
        questionCountLabel.setText("Question " + (index + 1) + " / " + questions.size());
        questionTextLabel.setText("<html><body style='width:430px'>"
            + q.getQuestionText() + "</body></html>");

        buttonGroup.clearSelection();
        String[] opts  = q.getOptions();
        char[]   lbs   = {'A', 'B', 'C', 'D'};
        for (int i = 0; i < 4; i++) {
            optionButtons[i].setText(lbs[i] + ")  " + opts[i]);
            optionButtons[i].setSelected(userAnswers[index] == i);
        }
        prevBtn.setEnabled(index > 0);
        nextBtn.setEnabled(index < questions.size() - 1);
    }

    private void navigate(int delta) { showQuestion(currentIndex + delta); }

    private void confirmSubmit() {
        int unanswered = 0;
        for (int ans : userAnswers) if (ans == -1) unanswered++;
        String msg = unanswered > 0
            ? unanswered + " question(s) unanswered. Submit anyway?"
            : "Are you sure you want to submit the quiz?";
        int confirm = JOptionPane.showConfirmDialog(this, msg, "Submit Quiz",
            JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) submitQuiz();
    }

    private void autoSubmit() {
        JOptionPane.showMessageDialog(this,
            "⏱ Time's up! Your quiz has been auto-submitted.", "Auto Submit",
            JOptionPane.INFORMATION_MESSAGE);
        submitQuiz();
    }

    private void submitQuiz() {
        quizTimer.stop();
        monitoring.stopMonitoring();

        int score = 0;
        for (int i = 0; i < questions.size(); i++) {
            if (userAnswers[i] != -1 && questions.get(i).isCorrect(userAnswers[i]))
                score++;
        }
        long timeTaken = (System.currentTimeMillis() - quizStartTime) / 1000;

        // FIXED: Use displayName (actual student name) instead of username
        // This fixes the leaderboard showing same/wrong name
        QuizResult result = new QuizResult(
            displayName,              // FIXED: real name entered at login
            student.getUsername(),
            score, questions.size(), timeTaken
        );
        dataStore.addResult(result);

        dispose();
        new ResultScreen(student, displayName, result, userAnswers, questions).setVisible(true);
    }
}
