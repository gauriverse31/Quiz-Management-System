package ui;

import model.*;
import service.*;
import util.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * 👨‍🏫 Teacher Dashboard
 * FIXED: Accepts displayName and subject from LoginScreen.
 * FIXED: Shows teacher name + subject in header.
 * FIXED: Pastel theme applied throughout.
 * FIXED: All buttons visible and properly laid out.
 */
public class TeacherDashboard extends JFrame {

    private final Teacher  teacher;
    private final String   displayName;
    private final String   subject;
    private final DataStore dataStore = DataStore.getInstance();
    private DefaultTableModel tableModel;
    private JTable questionTable;

    // FIXED: New constructor accepting displayName and subject
    public TeacherDashboard(Teacher teacher, String displayName, String subject) {
        this.teacher     = teacher;
        this.displayName = displayName.isEmpty() ? teacher.getName() : displayName;
        this.subject     = subject.isEmpty() ? "General" : subject;

        setTitle("QuizPro — Teacher Dashboard");
        setSize(960, 660);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        UITheme.styleFrame(this);
        buildUI();
    }

    // Keep backward-compatible constructor
    public TeacherDashboard(Teacher teacher) {
        this(teacher, teacher.getName(), "");
    }

    private void buildUI() {
        setLayout(new BorderLayout(0, 0));

        // ── Header ────────────────────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout(10, 0));
        header.setBackground(UITheme.PASTEL_BLUE);
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // FIXED: Show teacher name AND subject in header
        JLabel titleLbl = new JLabel("👨‍🏫 Teacher Dashboard");
        titleLbl.setFont(UITheme.FONT_TITLE);
        titleLbl.setForeground(UITheme.TEXT_PRIMARY);

        JLabel infoLbl = new JLabel("Welcome, " + displayName + "  |  Subject: " + subject);
        infoLbl.setFont(UITheme.FONT_BODY);
        infoLbl.setForeground(UITheme.PASTEL_BLUE_DARK);

        JPanel headerLeft = new JPanel(new GridLayout(2, 1, 0, 2));
        headerLeft.setBackground(UITheme.PASTEL_BLUE);
        headerLeft.add(titleLbl);
        headerLeft.add(infoLbl);

        JButton logoutBtn      = UITheme.dangerButton("Logout");
        JButton leaderboardBtn = UITheme.primaryButton("🏆 Leaderboard");

        logoutBtn.addActionListener(e -> { dispose(); new LoginScreen().setVisible(true); });
        leaderboardBtn.addActionListener(e -> new LeaderboardDialog(this).setVisible(true));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setBackground(UITheme.PASTEL_BLUE);
        btnPanel.add(leaderboardBtn);
        btnPanel.add(logoutBtn);

        header.add(headerLeft, BorderLayout.WEST);
        header.add(btnPanel,   BorderLayout.EAST);

        // ── Question Table ────────────────────────────────────────────────────
        String[] cols = {"#", "Question", "Topic", "Difficulty", "Correct Ans"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        questionTable = new JTable(tableModel);
        questionTable.setBackground(UITheme.BG_CARD);
        questionTable.setForeground(UITheme.TEXT_PRIMARY);
        questionTable.setGridColor(UITheme.BORDER_COLOR);
        questionTable.setFont(UITheme.FONT_BODY);
        questionTable.setRowHeight(28);
        questionTable.setSelectionBackground(UITheme.PASTEL_BLUE);
        questionTable.setSelectionForeground(UITheme.TEXT_PRIMARY);
        questionTable.getTableHeader().setBackground(UITheme.PASTEL_BLUE_DARK);
        questionTable.getTableHeader().setForeground(Color.WHITE);
        questionTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));

        questionTable.getColumnModel().getColumn(0).setMaxWidth(40);
        questionTable.getColumnModel().getColumn(2).setMaxWidth(160);
        questionTable.getColumnModel().getColumn(3).setMaxWidth(100);
        questionTable.getColumnModel().getColumn(4).setMaxWidth(120);

        refreshTable();

        JScrollPane scrollPane = new JScrollPane(questionTable);
        scrollPane.setBackground(UITheme.BG_DARK);
        scrollPane.getViewport().setBackground(UITheme.BG_CARD);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR));

        // ── Action Buttons Bar ────────────────────────────────────────────────
        // FIXED: Buttons fully visible with proper FlowLayout and explicit sizing
        JButton addBtn     = UITheme.successButton("+ Add Question");
        JButton deleteBtn  = UITheme.dangerButton("🗑 Delete Selected");
        JButton suggestBtn = UITheme.primaryButton("🤖 Suggest Questions");

        addBtn.addActionListener(e -> new AddQuestionDialog(this).setVisible(true));
        deleteBtn.addActionListener(e -> deleteSelected());
        suggestBtn.addActionListener(e -> new QuestionSuggestionDialog(this).setVisible(true));

        JPanel actionBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        actionBar.setBackground(UITheme.CREAM_CARD);
        actionBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.BORDER_COLOR));
        actionBar.add(addBtn);
        actionBar.add(suggestBtn);
        actionBar.add(deleteBtn);

        // ── Layout ────────────────────────────────────────────────────────────
        add(header,     BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(actionBar,  BorderLayout.SOUTH);

        // FIXED: revalidate after adding all components
        revalidate();
        repaint();
    }

    public void refreshTable() {
        tableModel.setRowCount(0);
        List<Question> questions = dataStore.getQuestions();
        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            String ans = (char)('A' + q.getCorrectOptionIndex()) + ") "
                + q.getOptions()[q.getCorrectOptionIndex()];
            tableModel.addRow(new Object[]{
                i + 1, q.getQuestionText(), q.getTopic(),
                q.getDifficulty().name(), ans
            });
        }
    }

    private void deleteSelected() {
        int row = questionTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a question to delete.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete question " + (row + 1) + "?", "Confirm Delete",
            JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dataStore.removeQuestion(row);
            refreshTable();
        }
    }
}
