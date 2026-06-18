package ui;

import model.Question;
import service.DataStore;
import util.UITheme;

import javax.swing.*;
import java.awt.*;

/**
 * Dialog for teacher to add a new question.
 * FIXED: Pastel theme applied — cream background, visible components.
 */
public class AddQuestionDialog extends JDialog {

    private final TeacherDashboard parent;
    private JTextField questionField;
    private JTextField[] optionFields = new JTextField[4];
    private JComboBox<String> correctBox;
    private JComboBox<Question.Difficulty> difficultyBox;
    private JTextField topicField;

    public AddQuestionDialog(TeacherDashboard parent) {
        super(parent, "Add New Question", true);
        this.parent = parent;
        setSize(560, 530);
        setLocationRelativeTo(parent);
        setResizable(false);
        getContentPane().setBackground(UITheme.BG_DARK);
        buildUI();
    }

    private void buildUI() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(UITheme.BG_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JLabel title = UITheme.titleLabel("➕ Add Question");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createVerticalStrut(15));

        panel.add(label("Question Text:"));
        questionField = UITheme.textField(30);
        questionField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        panel.add(questionField);
        panel.add(Box.createVerticalStrut(10));

        String[] optLabels = {"Option A:", "Option B:", "Option C:", "Option D:"};
        for (int i = 0; i < 4; i++) {
            panel.add(label(optLabels[i]));
            optionFields[i] = UITheme.textField(30);
            optionFields[i].setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
            panel.add(optionFields[i]);
            panel.add(Box.createVerticalStrut(6));
        }

        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        row.setBackground(UITheme.BG_DARK);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        correctBox = new JComboBox<>(new String[]{"A", "B", "C", "D"});
        styleCombo(correctBox);
        difficultyBox = new JComboBox<>(Question.Difficulty.values());
        styleCombo(difficultyBox);
        topicField = UITheme.textField(10);
        topicField.setText("Java");

        row.add(label("Correct:"));    row.add(correctBox);
        row.add(label("Difficulty:")); row.add(difficultyBox);
        row.add(label("Topic:"));      row.add(topicField);
        panel.add(row);
        panel.add(Box.createVerticalStrut(18));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        btnRow.setBackground(UITheme.BG_DARK);
        JButton saveBtn   = UITheme.successButton("Save Question");
        JButton cancelBtn = UITheme.dangerButton("Cancel");
        saveBtn.addActionListener(e -> saveQuestion());
        cancelBtn.addActionListener(e -> dispose());
        btnRow.add(saveBtn);
        btnRow.add(cancelBtn);
        panel.add(btnRow);

        JScrollPane sp = new JScrollPane(panel);
        sp.setBorder(null);
        sp.getViewport().setBackground(UITheme.BG_DARK);
        add(sp);
    }

    private void saveQuestion() {
        String qText = questionField.getText().trim();
        String topic = topicField.getText().trim();

        if (qText.isEmpty() || topic.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in the question and topic.");
            return;
        }
        String[] opts = new String[4];
        for (int i = 0; i < 4; i++) {
            opts[i] = optionFields[i].getText().trim();
            if (opts[i].isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all 4 options.");
                return;
            }
        }
        int correctIdx = correctBox.getSelectedIndex();
        Question.Difficulty diff = (Question.Difficulty) difficultyBox.getSelectedItem();
        Question q = new Question(qText, opts, correctIdx, diff, topic);
        DataStore.getInstance().addQuestion(q);
        parent.refreshTable();
        JOptionPane.showMessageDialog(this, "✅ Question added successfully!");
        dispose();
    }

    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(UITheme.TEXT_MUTED);
        l.setFont(UITheme.FONT_SMALL);
        return l;
    }

    private void styleCombo(JComboBox<?> box) {
        box.setBackground(UITheme.BG_INPUT);
        box.setForeground(UITheme.TEXT_PRIMARY);
        box.setFont(UITheme.FONT_BODY);
    }
}
