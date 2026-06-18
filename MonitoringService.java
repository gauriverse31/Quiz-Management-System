package ui;

import model.User;
import model.Teacher;
import model.Student;
import service.DataStore;
import util.UITheme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * 🔐 Login Screen — Full-screen split layout with pastel theme.
 * LEFT  : Soft pastel lavender/pink gradient branding panel.
 * RIGHT : Pastel mint/cream login form.
 */
public class LoginScreen extends JFrame {

    // ── Pastel colour palette ─────────────────────────────────────────────────
    // Left panel
    private static final Color PASTEL_LEFT_TOP    = new Color(180, 160, 220); // soft lavender
    private static final Color PASTEL_LEFT_BOT    = new Color(255, 182, 193); // soft pink
    private static final Color PASTEL_CIRCLE      = new Color(255, 255, 255);
    private static final Color LEFT_TEXT_MAIN     = new Color(80,  50, 100);  // deep purple-ish
    private static final Color LEFT_TEXT_SUB      = new Color(120, 80, 140);
    private static final Color LEFT_TEXT_FEAT     = new Color(100, 60, 120);
    private static final Color LEFT_SEP           = new Color(200, 170, 220, 80);
    private static final Color LEFT_QUOTE         = new Color(140, 100, 160);

    // Right panel
    private static final Color RIGHT_BG           = new Color(240, 255, 248); // very light mint
    private static final Color CARD_BG            = new Color(255, 253, 245); // warm cream
    private static final Color CARD_BORDER        = new Color(210, 230, 215);
    private static final Color FIELD_BG           = new Color(250, 255, 252); // near-white mint
    private static final Color FIELD_BORDER       = new Color(195, 225, 205);
    private static final Color FIELD_FOCUS_BORDER = new Color(150, 200, 170); // mint green
    private static final Color LABEL_COLOR        = new Color(70,  110,  85);
    private static final Color HEADING_COLOR      = new Color(60,   90,  75);
    private static final Color SUBHEAD_COLOR      = new Color(110, 140, 120);
    private static final Color ERROR_COLOR        = new Color(200,  80,  80);

    // Button
    private static final Color BTN_NORMAL         = new Color(160, 200, 175); // pastel green
    private static final Color BTN_HOVER          = new Color(190, 160, 210); // pastel lavender
    private static final Color BTN_TEXT           = new Color(40,   70,  55);

    // Demo hint box
    private static final Color HINT_BG            = new Color(255, 245, 250); // pastel blush
    private static final Color HINT_BORDER        = new Color(220, 195, 210);
    private static final Color HINT_TITLE         = new Color(160,  80, 120);
    private static final Color HINT_TEXT          = new Color(120,  90, 110);

    // ── Fields ────────────────────────────────────────────────────────────────
    private JTextField     usernameField;
    private JPasswordField passwordField;
    private JTextField     displayNameField;
    private JTextField     subjectField;
    private JLabel         subjectLabel;
    private JLabel         errorLabel;
    private JComboBox<String> roleBox;

    public LoginScreen() {
        setTitle("QuizPro — College Quiz System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(800, 500));
        setLocationRelativeTo(null);
        setResizable(true);
        buildUI();
    }

    private void buildUI() {
        JPanel root = new JPanel(new GridLayout(1, 2, 0, 0));
        root.add(buildBrandPanel());
        root.add(buildFormPanel());
        setContentPane(root);
    }

    // ── LEFT: Pastel gradient branding panel ──────────────────────────────────
    private JPanel buildBrandPanel() {
        JPanel panel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                // Pastel lavender → pastel pink gradient
                GradientPaint gp = new GradientPaint(
                    0, 0,            PASTEL_LEFT_TOP,
                    getWidth(), getHeight(), PASTEL_LEFT_BOT);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Soft circle decorations
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.12f));
                g2.setColor(PASTEL_CIRCLE);
                g2.fillOval(-100, -100, 380, 380);
                g2.fillOval(getWidth() - 180, getHeight() - 220, 340, 340);
                g2.fillOval(getWidth() / 2 - 80, getHeight() - 120, 230, 230);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.08f));
                g2.fillOval(getWidth() - 80, 40, 200, 200);
                g2.dispose();
            }
        };
        panel.setOpaque(false);

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(8, 48, 8, 48);

        // Emoji logo
        JLabel logo = new JLabel("\uD83C\uDF93", SwingConstants.CENTER);
        logo.setFont(new Font("SansSerif", Font.PLAIN, 80));
        g.gridy = 0; g.insets = new Insets(0, 48, 0, 48);
        panel.add(logo, g);

        // App title
        JLabel title = new JLabel("QuizPro", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 54));
        title.setForeground(LEFT_TEXT_MAIN);
        g.gridy = 1; g.insets = new Insets(4, 48, 4, 48);
        panel.add(title, g);

        // Subtitle
        JLabel sub = new JLabel(
            "<html><div style='text-align:center'>College Quiz Management System</div></html>",
            SwingConstants.CENTER);
        sub.setFont(new Font("SansSerif", Font.PLAIN, 15));
        sub.setForeground(LEFT_TEXT_SUB);
        g.gridy = 2; g.insets = new Insets(2, 48, 22, 48);
        panel.add(sub, g);

        // Divider
        JSeparator sep = new JSeparator();
        sep.setForeground(LEFT_SEP);
        g.gridy = 3; g.insets = new Insets(0, 48, 22, 48);
        panel.add(sep, g);

        // Feature rows
        String[][] feats = {
            {"\uD83D\uDCDD", "Create & manage quizzes easily"},
            {"\u23F1",       "Timed sessions with auto-submit"},
            {"\uD83C\uDFC6", "Live leaderboard & rankings"},
            {"\uD83D\uDCCA", "Detailed per-question analysis"},
        };
        for (int i = 0; i < feats.length; i++) {
            g.gridy = 4 + i; g.insets = new Insets(7, 68, 7, 48);
            panel.add(featureRow(feats[i][0], feats[i][1]), g);
        }

        // Quote
        JLabel quote = new JLabel(
            "<html><div style='text-align:center;font-style:italic'>" +
            "\"Education is the passport to the future\"</div></html>",
            SwingConstants.CENTER);
        quote.setFont(new Font("SansSerif", Font.ITALIC, 12));
        quote.setForeground(LEFT_QUOTE);
        g.gridy = 9; g.insets = new Insets(28, 48, 0, 48);
        panel.add(quote, g);

        return panel;
    }

    private JPanel featureRow(String icon, String text) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        row.setOpaque(false);
        JLabel il = new JLabel(icon);
        il.setFont(new Font("SansSerif", Font.PLAIN, 18));
        JLabel tl = new JLabel(text);
        tl.setFont(new Font("SansSerif", Font.PLAIN, 14));
        tl.setForeground(LEFT_TEXT_FEAT);
        row.add(il); row.add(tl);
        return row;
    }

    // ── RIGHT: Pastel mint form panel ─────────────────────────────────────────
    private JPanel buildFormPanel() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(RIGHT_BG);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(CARD_BORDER, 1),
            BorderFactory.createEmptyBorder(36, 42, 36, 42)));
        card.setPreferredSize(new Dimension(400, card.getPreferredSize().height));

        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0; gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1; gc.insets = new Insets(4, 0, 4, 0);

        // Heading
        JLabel welcome = new JLabel("Welcome Back! \uD83D\uDC4B");
        welcome.setFont(new Font("SansSerif", Font.BOLD, 28));
        welcome.setForeground(HEADING_COLOR);
        gc.gridy = 0; gc.insets = new Insets(0, 0, 2, 0);
        card.add(welcome, gc);

        JLabel hintLbl = new JLabel("Sign in to continue to QuizPro");
        hintLbl.setFont(new Font("SansSerif", Font.PLAIN, 13));
        hintLbl.setForeground(SUBHEAD_COLOR);
        gc.gridy = 1; gc.insets = new Insets(0, 0, 22, 0);
        card.add(hintLbl, gc);

        // Role
        gc.gridy = 2; gc.insets = new Insets(0, 0, 2, 0);
        card.add(label("Login As:"), gc);
        roleBox = new JComboBox<>(new String[]{"Student", "Teacher"});
        styleCombo(roleBox);
        roleBox.addActionListener(e -> toggleRoleFields());
        gc.gridy = 3; gc.insets = new Insets(0, 0, 14, 0);
        card.add(roleBox, gc);

        // Full name
        gc.gridy = 4; gc.insets = new Insets(0, 0, 2, 0);
        card.add(label("Your Full Name:"), gc);
        displayNameField = field();
        displayNameField.setToolTipText("Shown on quiz & leaderboard");
        gc.gridy = 5; gc.insets = new Insets(0, 0, 14, 0);
        card.add(displayNameField, gc);

        // Subject (teacher only)
        subjectLabel = label("Subject You Teach:");
        gc.gridy = 6; gc.insets = new Insets(0, 0, 2, 0);
        card.add(subjectLabel, gc);
        subjectField = field();
        subjectField.setToolTipText("e.g. Java, Data Structures");
        gc.gridy = 7; gc.insets = new Insets(0, 0, 14, 0);
        card.add(subjectField, gc);

        // Username
        gc.gridy = 8; gc.insets = new Insets(0, 0, 2, 0);
        card.add(label("Username:"), gc);
        usernameField = field();
        usernameField.setText("student1");
        gc.gridy = 9; gc.insets = new Insets(0, 0, 14, 0);
        card.add(usernameField, gc);

        // Password
        gc.gridy = 10; gc.insets = new Insets(0, 0, 2, 0);
        card.add(label("Password:"), gc);
        passwordField = new JPasswordField();
        styleField(passwordField);
        passwordField.setText("pass123");
        gc.gridy = 11; gc.insets = new Insets(0, 0, 8, 0);
        card.add(passwordField, gc);

        // Error
        errorLabel = new JLabel(" ", SwingConstants.CENTER);
        errorLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        errorLabel.setForeground(ERROR_COLOR);
        gc.gridy = 12; gc.insets = new Insets(0, 0, 6, 0);
        card.add(errorLabel, gc);

        // Login button
        JButton loginBtn = buildLoginBtn();
        loginBtn.addActionListener(e -> handleLogin());
        getRootPane().setDefaultButton(loginBtn);
        gc.gridy = 13; gc.insets = new Insets(0, 0, 18, 0);
        card.add(loginBtn, gc);

        // Demo hint
        gc.gridy = 14; gc.insets = new Insets(0, 0, 0, 0);
        card.add(buildDemoHint(), gc);

        toggleRoleFields();
        outer.add(card);
        return outer;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.BOLD, 12));
        l.setForeground(LABEL_COLOR);
        return l;
    }

    private JTextField field() {
        JTextField tf = new JTextField();
        styleField(tf);
        return tf;
    }

    private void styleField(JComponent c) {
        c.setFont(new Font("SansSerif", Font.PLAIN, 14));
        c.setForeground(new Color(40, 70, 55));
        c.setBackground(FIELD_BG);
        c.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(FIELD_BORDER, 1),
            BorderFactory.createEmptyBorder(9, 11, 9, 11)));
        c.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                c.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(FIELD_FOCUS_BORDER, 2),
                    BorderFactory.createEmptyBorder(8, 10, 8, 10)));
            }
            public void focusLost(FocusEvent e) {
                c.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(FIELD_BORDER, 1),
                    BorderFactory.createEmptyBorder(9, 11, 9, 11)));
            }
        });
    }

    private void styleCombo(JComboBox<String> cb) {
        cb.setFont(new Font("SansSerif", Font.PLAIN, 14));
        cb.setBackground(FIELD_BG);
        cb.setForeground(new Color(40, 70, 55));
        cb.setBorder(BorderFactory.createLineBorder(FIELD_BORDER, 1));
    }

    private JButton buildLoginBtn() {
        JButton btn = new JButton("Sign In  \u2192");
        btn.setFont(new Font("SansSerif", Font.BOLD, 15));
        btn.setForeground(BTN_TEXT);
        btn.setBackground(BTN_NORMAL);
        btn.setOpaque(true);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(13, 24, 13, 24));
        btn.setPreferredSize(new Dimension(Integer.MAX_VALUE, 48));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(BTN_HOVER); }
            public void mouseExited (MouseEvent e) { btn.setBackground(BTN_NORMAL); }
        });
        return btn;
    }

    private JPanel buildDemoHint() {
        JPanel p = new JPanel(new GridLayout(3, 1, 0, 3));
        p.setBackground(HINT_BG);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(HINT_BORDER, 1),
            BorderFactory.createEmptyBorder(10, 14, 10, 14)));
        JLabel t0 = new JLabel("\uD83D\uDCA1 Demo Credentials");
        t0.setFont(new Font("SansSerif", Font.BOLD, 12));
        t0.setForeground(HINT_TITLE);
        JLabel t1 = new JLabel("\uD83D\uDC68\u200D\uD83C\uDFEB Teacher: teacher1 / pass123");
        t1.setFont(new Font("SansSerif", Font.PLAIN, 12));
        t1.setForeground(HINT_TEXT);
        JLabel t2 = new JLabel("\uD83C\uDF93 Student: student1 / pass123");
        t2.setFont(new Font("SansSerif", Font.PLAIN, 12));
        t2.setForeground(HINT_TEXT);
        p.add(t0); p.add(t1); p.add(t2);
        return p;
    }

    private void toggleRoleFields() {
        boolean isTeacher = "Teacher".equals(roleBox.getSelectedItem());
        subjectLabel.setVisible(isTeacher);
        subjectField.setVisible(isTeacher);
        usernameField.setText(isTeacher ? "teacher1" : "student1");
        displayNameField.setText("");
    }

    private void handleLogin() {
        String role        = (String) roleBox.getSelectedItem();
        String displayName = displayNameField.getText().trim();
        String username    = usernameField.getText().trim();
        String password    = new String(passwordField.getPassword()).trim();
        String subject     = subjectField.getText().trim();

        if (displayName.isEmpty()) {
            errorLabel.setText("Please enter your full name.");
            displayNameField.requestFocus(); return;
        }
        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please fill in username and password."); return;
        }
        if ("Teacher".equals(role) && subject.isEmpty()) {
            errorLabel.setText("Please enter the subject you teach.");
            subjectField.requestFocus(); return;
        }

        User user = DataStore.getInstance().authenticate(username, password);
        if (user == null) {
            errorLabel.setText("Invalid username or password.");
            passwordField.setText(""); return;
        }
        if ("Teacher".equals(role) && !user.getRole().equals("TEACHER")) {
            errorLabel.setText("This account is not a Teacher account."); return;
        }
        if ("Student".equals(role) && !user.getRole().equals("STUDENT")) {
            errorLabel.setText("This account is not a Student account."); return;
        }

        errorLabel.setText(" ");
        dispose();
        if (user instanceof Teacher)
            new TeacherDashboard((Teacher) user, displayName, subject).setVisible(true);
        else if (user instanceof Student)
            new StudentDashboard((Student) user, displayName).setVisible(true);
    }
}
