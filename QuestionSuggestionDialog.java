package ui;

import model.QuizResult;
import service.DataStore;
import util.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * 🏆 Leaderboard Dialog
 * FIXED: Shows actual student names (no hardcoded "Student1").
 * FIXED: Pastel theme applied.
 * Names come from QuizResult.getStudentName() which is now the displayName.
 */
public class LeaderboardDialog extends JDialog {

    private final DataStore dataStore = DataStore.getInstance();

    public LeaderboardDialog(Frame parent) {
        super(parent, "🏆 Leaderboard", true);
        setSize(680, 520);
        setLocationRelativeTo(parent);
        setResizable(true);
        getContentPane().setBackground(UITheme.BG_DARK);
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout(0, 0));

        // ── Header ────────────────────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.PASTEL_BLUE);
        header.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        JLabel title = new JLabel("🏆 Leaderboard");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(new Color(160, 100, 0)); // Gold-ish on pastel
        header.add(title, BorderLayout.WEST);

        // ── Stats Row ─────────────────────────────────────────────────────────
        double[] stats = dataStore.getStats();
        JPanel statsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 8));
        statsRow.setBackground(UITheme.BG_DARK);
        statsRow.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
        statsRow.add(miniStat("Highest Score", String.format("%.1f%%", stats[0]), UITheme.ACCENT_GREEN));
        statsRow.add(miniStat("Average Score", String.format("%.1f%%", stats[1]), UITheme.ACCENT_BLUE));
        statsRow.add(miniStat("Total Attempts", String.valueOf((int) stats[2]), UITheme.ACCENT_PURPLE));

        // ── Table ─────────────────────────────────────────────────────────────
        String[] cols = {"Rank", "Name", "Score", "Percentage", "Time", "Submitted"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        List<QuizResult> leaderboard = dataStore.getLeaderboard();
        for (int i = 0; i < leaderboard.size(); i++) {
            QuizResult r = leaderboard.get(i);
            String rankStr;
            if      (i == 0) rankStr = "🥇 1st";
            else if (i == 1) rankStr = "🥈 2nd";
            else if (i == 2) rankStr = "🥉 3rd";
            else             rankStr = "  " + (i + 1) + "th";

            // FIXED: r.getStudentName() now returns the actual display name
            model.addRow(new Object[]{
                rankStr,
                r.getStudentName(),          // actual name, not hardcoded
                r.getScore() + "/" + r.getTotalQuestions(),
                String.format("%.1f%%", r.getPercentage()),
                r.getFormattedTime(),
                r.getTimestamp()
            });
        }

        JTable table = new JTable(model);
        table.setBackground(UITheme.BG_CARD);
        table.setForeground(UITheme.TEXT_PRIMARY);
        table.setGridColor(UITheme.BORDER_COLOR);
        table.setFont(UITheme.FONT_BODY);
        table.setRowHeight(30);
        table.getTableHeader().setBackground(UITheme.PASTEL_BLUE_DARK);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        table.setSelectionBackground(UITheme.PASTEL_BLUE);
        table.setSelectionForeground(UITheme.TEXT_PRIMARY);

        table.getColumnModel().getColumn(0).setMaxWidth(80);
        table.getColumnModel().getColumn(2).setMaxWidth(80);
        table.getColumnModel().getColumn(3).setMaxWidth(90);
        table.getColumnModel().getColumn(4).setMaxWidth(80);
        table.getColumnModel().getColumn(5).setMaxWidth(130);

        // Highlight top 3 rows
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            private final Color[] topColors = {
                new Color(255, 235, 150),  // Gold pastel
                new Color(220, 220, 220),  // Silver pastel
                new Color(230, 190, 160)   // Bronze pastel
            };
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean focus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, val, sel, focus, row, col);
                setHorizontalAlignment(col == 0 ? CENTER : LEFT);
                setForeground(UITheme.TEXT_PRIMARY);
                if (!sel) {
                    setBackground(row < 3 ? topColors[row] : UITheme.BG_CARD);
                }
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR));
        scrollPane.getViewport().setBackground(UITheme.BG_CARD);

        if (leaderboard.isEmpty()) {
            JLabel empty = new JLabel("No quiz attempts yet. Be the first!", SwingConstants.CENTER);
            empty.setForeground(UITheme.TEXT_MUTED);
            empty.setFont(UITheme.FONT_BODY);
            add(empty, BorderLayout.CENTER);
        } else {
            JPanel wrapper = new JPanel(new BorderLayout(0, 0));
            wrapper.setBackground(UITheme.BG_DARK);
            wrapper.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));
            wrapper.add(statsRow,   BorderLayout.NORTH);
            wrapper.add(scrollPane, BorderLayout.CENTER);
            add(wrapper, BorderLayout.CENTER);
        }

        JButton closeBtn = UITheme.primaryButton("Close");
        closeBtn.addActionListener(e -> dispose());
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        bottomBar.setBackground(UITheme.BG_DARK);
        bottomBar.add(closeBtn);

        add(header,    BorderLayout.NORTH);
        add(bottomBar, BorderLayout.SOUTH);
    }

    private JPanel miniStat(String title, String value, Color color) {
        JPanel p = new JPanel(new GridLayout(2, 1, 0, 2));
        p.setBackground(UITheme.BG_CARD);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 1),
            BorderFactory.createEmptyBorder(6, 14, 6, 14)
        ));
        JLabel valLbl = new JLabel(value, SwingConstants.CENTER);
        valLbl.setFont(new Font("SansSerif", Font.BOLD, 18));
        valLbl.setForeground(color);
        JLabel titLbl = new JLabel(title, SwingConstants.CENTER);
        titLbl.setFont(UITheme.FONT_SMALL);
        titLbl.setForeground(UITheme.TEXT_MUTED);
        p.add(valLbl);
        p.add(titLbl);
        return p;
    }
}
