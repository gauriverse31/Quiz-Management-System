package util;

import javax.swing.*;
import java.awt.*;

/**
 * 🎨 UITheme — Pastel College Theme
 * Three pastel colors: Soft Blue, Soft Green, Warm Off-White/Cream
 */
public class UITheme {

    // ── Pastel Color Palette ──────────────────────────────────────────────────
    public static final Color PASTEL_BLUE       = new Color(173, 216, 230);
    public static final Color PASTEL_BLUE_DARK  = new Color(80,  150, 190);
    public static final Color PASTEL_GREEN      = new Color(180, 230, 190);
    public static final Color PASTEL_GREEN_DARK = new Color(70,  160, 100);
    public static final Color CREAM             = new Color(255, 253, 240);
    public static final Color CREAM_CARD        = new Color(245, 245, 232);

    // ── Semantic aliases (keep old names so other files compile) ──────────────
    public static final Color BG_DARK       = CREAM;
    public static final Color BG_CARD       = CREAM_CARD;
    public static final Color BG_INPUT      = Color.WHITE;
    public static final Color ACCENT_BLUE   = PASTEL_BLUE_DARK;
    public static final Color ACCENT_GREEN  = PASTEL_GREEN_DARK;
    public static final Color ACCENT_RED    = new Color(205, 70,  70);
    public static final Color ACCENT_ORANGE = new Color(210, 130, 40);
    public static final Color ACCENT_PURPLE = new Color(140, 100, 195);
    public static final Color TEXT_PRIMARY  = new Color(35,  35,  55);
    public static final Color TEXT_MUTED    = new Color(100, 105, 130);
    public static final Color BORDER_COLOR  = new Color(195, 210, 220);

    // ── Fonts ─────────────────────────────────────────────────────────────────
    public static final Font FONT_TITLE    = new Font("SansSerif", Font.BOLD,  24);
    public static final Font FONT_SUBTITLE = new Font("SansSerif", Font.BOLD,  16);
    public static final Font FONT_BODY     = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font FONT_SMALL    = new Font("SansSerif", Font.PLAIN, 12);
    public static final Font FONT_MONO     = new Font("Monospaced", Font.PLAIN, 13);

    // ── Button Factories ──────────────────────────────────────────────────────

    public static JButton primaryButton(String text) {
        JButton btn = makeBtn(text, PASTEL_BLUE_DARK, new Color(55, 120, 165));
        return btn;
    }

    public static JButton dangerButton(String text) {
        return makeBtn(text, ACCENT_RED, new Color(175, 45, 45));
    }

    public static JButton successButton(String text) {
        return makeBtn(text, PASTEL_GREEN_DARK, new Color(45, 130, 75));
    }

    private static JButton makeBtn(String text, Color normal, Color hover) {
        JButton btn = new JButton(text);
        btn.setBackground(normal);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 22, 10, 22));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(hover); }
            public void mouseExited (java.awt.event.MouseEvent e) { btn.setBackground(normal); }
        });
        return btn;
    }

    public static JTextField textField(int columns) {
        JTextField tf = new JTextField(columns);
        tf.setBackground(BG_INPUT);
        tf.setForeground(TEXT_PRIMARY);
        tf.setCaretColor(TEXT_PRIMARY);
        tf.setFont(FONT_BODY);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        return tf;
    }

    public static JPasswordField passwordField(int columns) {
        JPasswordField pf = new JPasswordField(columns);
        pf.setBackground(BG_INPUT);
        pf.setForeground(TEXT_PRIMARY);
        pf.setCaretColor(TEXT_PRIMARY);
        pf.setFont(FONT_BODY);
        pf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        return pf;
    }

    public static JLabel titleLabel(String text) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(FONT_TITLE);
        lbl.setForeground(TEXT_PRIMARY);
        return lbl;
    }

    public static JPanel cardPanel() {
        JPanel p = new JPanel();
        p.setBackground(BG_CARD);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)));
        return p;
    }

    public static void styleFrame(JFrame frame) {
        frame.getContentPane().setBackground(BG_DARK);
    }

    public static JSeparator separator() {
        JSeparator sep = new JSeparator();
        sep.setForeground(BORDER_COLOR);
        return sep;
    }
}
