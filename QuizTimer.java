package ui;

import model.Question;
import service.DataStore;
import service.QuestionSuggestion;
import util.UITheme;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 🤖 Question Suggestion Dialog
 * FIXED: Difficulty combo now visible.
 * FIXED: "Get Suggestions" button always visible.
 * FIXED: Subject filter actually filters — Maths shows Maths questions only.
 * FIXED: Filter panel uses proper layout so nothing is clipped.
 */
public class QuestionSuggestionDialog extends JDialog {

    private final TeacherDashboard  parent;
    private final QuestionSuggestion suggestionService = new QuestionSuggestion();

    private JComboBox<String>            topicBox;
    private JComboBox<String>            difficultyBox;
    private JPanel                       resultsPanel;
    private List<Question>               suggestedList = new ArrayList<>();
    private List<JCheckBox>              checkBoxes    = new ArrayList<>();

    public QuestionSuggestionDialog(TeacherDashboard parent) {
        super(parent, "🤖 Question Suggestion Engine", true);
        this.parent = parent;
        setSize(660, 580);
        setLocationRelativeTo(parent);
        setResizable(true);
        getContentPane().setBackground(UITheme.BG_DARK);
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout(0, 0));

        // ── Top Filter Panel ──────────────────────────────────────────────────
        // FIXED: Use GridBagLayout so all components fit and nothing is clipped
        JPanel filterPanel = new JPanel(new GridBagLayout());
        filterPanel.setBackground(UITheme.PASTEL_BLUE);
        filterPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, UITheme.BORDER_COLOR),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(4, 6, 4, 6);
        gc.anchor = GridBagConstraints.WEST;

        // Row 0: Title
        gc.gridx = 0; gc.gridy = 0; gc.gridwidth = 6;
        JLabel title = new JLabel("🤖 Suggest Questions");
        title.setFont(UITheme.FONT_SUBTITLE);
        title.setForeground(UITheme.TEXT_PRIMARY);
        filterPanel.add(title, gc);

        // Row 1: Topic label + combo + Difficulty label + combo + Button
        gc.gridwidth = 1;
        gc.gridy = 1;

        // Topic label
        gc.gridx = 0;
        filterPanel.add(filterLabel("Topic:"), gc);

        // Topic combo — dynamically loaded from suggestion service
        List<String> topics = suggestionService.getAllTopics();
        topics.add(0, "Any Topic");
        topicBox = new JComboBox<>(topics.toArray(new String[0]));
        styleCombo(topicBox, 140);
        gc.gridx = 1;
        filterPanel.add(topicBox, gc);

        // Difficulty label
        gc.gridx = 2;
        filterPanel.add(filterLabel("Difficulty:"), gc);

        // FIXED: Difficulty combo — was missing/clipped before
        String[] diffOptions = {"Any Difficulty", "EASY", "MEDIUM", "HARD"};
        difficultyBox = new JComboBox<>(diffOptions);
        styleCombo(difficultyBox, 130);
        gc.gridx = 3;
        filterPanel.add(difficultyBox, gc);

        // FIXED: Get Suggestions button — always visible in its own cell
        JButton fetchBtn = UITheme.primaryButton("Get Suggestions");
        fetchBtn.addActionListener(e -> loadSuggestions());
        gc.gridx = 4;
        gc.insets = new Insets(4, 14, 4, 6);
        filterPanel.add(fetchBtn, gc);

        // ── Results Panel (scrollable) ────────────────────────────────────────
        resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
        resultsPanel.setBackground(UITheme.BG_DARK);
        resultsPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel placeholder = new JLabel("Click 'Get Suggestions' to see recommended questions.");
        placeholder.setForeground(UITheme.TEXT_MUTED);
        placeholder.setFont(UITheme.FONT_BODY);
        placeholder.setAlignmentX(Component.CENTER_ALIGNMENT);
        resultsPanel.add(Box.createVerticalStrut(60));
        resultsPanel.add(placeholder);

        JScrollPane scrollPane = new JScrollPane(resultsPanel);
        scrollPane.setBackground(UITheme.BG_DARK);
        scrollPane.getViewport().setBackground(UITheme.BG_DARK);
        scrollPane.setBorder(null);

        // ── Bottom Action Bar ─────────────────────────────────────────────────
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        bottomBar.setBackground(UITheme.PASTEL_BLUE);
        bottomBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.BORDER_COLOR));

        JButton addSelectedBtn = UITheme.successButton("✅ Add Selected to Bank");
        JButton cancelBtn      = UITheme.dangerButton("Close");

        addSelectedBtn.addActionListener(e -> addSelectedToBank());
        cancelBtn.addActionListener(e -> dispose());

        bottomBar.add(addSelectedBtn);
        bottomBar.add(cancelBtn);

        add(filterPanel, BorderLayout.NORTH);
        add(scrollPane,  BorderLayout.CENTER);
        add(bottomBar,   BorderLayout.SOUTH);
    }

    /** Fetches suggestions based on selected filters. */
    private void loadSuggestions() {
        resultsPanel.removeAll();
        checkBoxes.clear();
        suggestedList.clear();

        // Parse difficulty filter
        String diffStr = (String) difficultyBox.getSelectedItem();
        Question.Difficulty diff = null;
        if (diffStr != null && !diffStr.startsWith("Any")) {
            diff = Question.Difficulty.valueOf(diffStr);
        }

        // Parse topic filter
        String topic = (String) topicBox.getSelectedItem();
        if (topic != null && topic.startsWith("Any")) topic = null;

        // FIXED: If topic is selected (e.g. "Maths"), only Maths questions are returned
        suggestedList = suggestionService.suggest(diff, topic, 10);

        if (suggestedList.isEmpty()) {
            JLabel noResult = new JLabel("No suggestions found for these filters.");
            noResult.setForeground(UITheme.ACCENT_RED);
            noResult.setFont(UITheme.FONT_BODY);
            noResult.setAlignmentX(Component.CENTER_ALIGNMENT);
            resultsPanel.add(Box.createVerticalStrut(20));
            resultsPanel.add(noResult);
        } else {
            String topicDisplay = (topic == null) ? "All Topics" : topic;
            String diffDisplay  = (diff  == null) ? "All Difficulties" : diff.name();
            JLabel countLbl = new JLabel("Found " + suggestedList.size()
                + " suggestion(s) for: " + topicDisplay + " · " + diffDisplay);
            countLbl.setForeground(UITheme.ACCENT_GREEN);
            countLbl.setFont(UITheme.FONT_SUBTITLE);
            countLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
            resultsPanel.add(countLbl);
            resultsPanel.add(Box.createVerticalStrut(10));

            for (Question q : suggestedList) {
                resultsPanel.add(buildQuestionCard(q));
                resultsPanel.add(Box.createVerticalStrut(8));
            }
        }

        resultsPanel.revalidate();
        resultsPanel.repaint();
    }

    /** Builds a card UI for one suggested question with a checkbox. */
    private JPanel buildQuestionCard(Question q) {
        JPanel card = new JPanel(new BorderLayout(10, 5));
        card.setBackground(UITheme.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER_COLOR),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));

        // Checkbox (left) — pre-selected
        JCheckBox cb = new JCheckBox();
        cb.setBackground(UITheme.BG_CARD);
        cb.setSelected(true);
        checkBoxes.add(cb);

        // Question text
        JLabel qText = new JLabel("<html><b>" + q.getQuestionText() + "</b></html>");
        qText.setForeground(UITheme.TEXT_PRIMARY);
        qText.setFont(UITheme.FONT_BODY);

        // FIXED: Show all 4 options clearly
        StringBuilder opts = new StringBuilder("<html><small style='color:#555'>");
        char letter = 'A';
        for (String opt : q.getOptions()) {
            opts.append(letter++).append(") ").append(opt).append(" &nbsp;&nbsp; ");
        }
        opts.append("</small></html>");
        JLabel optsLbl = new JLabel(opts.toString());

        // Badges (right)
        JPanel badges = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        badges.setBackground(UITheme.BG_CARD);
        badges.add(badge(q.getDifficulty().name(), diffColor(q.getDifficulty())));
        badges.add(badge(q.getTopic(), UITheme.ACCENT_BLUE));

        JPanel center = new JPanel(new GridLayout(2, 1, 0, 4));
        center.setBackground(UITheme.BG_CARD);
        center.add(qText);
        center.add(optsLbl);

        card.add(cb,     BorderLayout.WEST);
        card.add(center, BorderLayout.CENTER);
        card.add(badges, BorderLayout.EAST);

        return card;
    }

    /** Adds all checked suggestions to the DataStore. */
    private void addSelectedToBank() {
        int count = 0;
        for (int i = 0; i < checkBoxes.size(); i++) {
            if (checkBoxes.get(i).isSelected()) {
                DataStore.getInstance().addQuestion(suggestedList.get(i));
                count++;
            }
        }
        parent.refreshTable();
        JOptionPane.showMessageDialog(this,
            "✅ " + count + " question(s) added to the question bank.");
        dispose();
    }

    private JLabel badge(String text, Color color) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(UITheme.FONT_SMALL);
        lbl.setForeground(Color.WHITE);
        lbl.setBackground(color);
        lbl.setOpaque(true);
        lbl.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
        return lbl;
    }

    private Color diffColor(Question.Difficulty d) {
        return switch (d) {
            case EASY   -> new Color(50, 160, 80);
            case MEDIUM -> new Color(190, 120, 0);
            case HARD   -> new Color(190, 50,  50);
        };
    }

    private JLabel filterLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.BOLD, 13));
        l.setForeground(UITheme.TEXT_PRIMARY);
        return l;
    }

    private void styleCombo(JComboBox<?> box, int width) {
        box.setBackground(UITheme.BG_INPUT);
        box.setForeground(UITheme.TEXT_PRIMARY);
        box.setFont(UITheme.FONT_BODY);
        box.setPreferredSize(new Dimension(width, 32));
    }
}
