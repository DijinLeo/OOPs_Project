package view;

import javax.swing.*;
import java.awt.*;
import util.UIStyleHelper;

/**
 * Enhanced Navigation Bar with gradient buttons and consistent spacing.
 * Solid blue background (kept from original version).
 */
public class NavigationBarPanel extends JPanel {
    private final JButton backBtn;
    private final JButton homeBtn;
    private final JButton logoutBtn;
    private boolean loggedIn = false;

    public void setLoggedIn(boolean status) {
        this.loggedIn = status;
    }

    public NavigationBarPanel(RideShareMobileUI ui) {
        // Keep your clean blue navbar background
        setLayout(new FlowLayout(FlowLayout.CENTER, 35, 14));
        setBackground(new Color(52, 152, 219));

        // Gradient Buttons (using your helper)
        backBtn = UIStyleHelper.createGradientButton("← Back");
        homeBtn = UIStyleHelper.createGradientButton("🏠 Home");
        logoutBtn = UIStyleHelper.createGradientButton("⎋ Logout");

        // Apply uniform font size for readability
        Font navFont = new Font("Segoe UI", Font.BOLD, 15);
        for (JButton btn : new JButton[]{backBtn, homeBtn, logoutBtn}) {
            btn.setFont(navFont);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.setFocusPainted(false);
        }

        // === Button Actions ===
        backBtn.addActionListener(e -> ui.goBack());

        homeBtn.addActionListener(e -> {
            if (!ui.isLoggedIn()) {
                JOptionPane.showMessageDialog(
                        this,
                        "<html><div style='text-align:center;'>Please login or sign up first!</div></html>",
                        "Access Denied",
                        JOptionPane.WARNING_MESSAGE
                );
                ui.showScreen("login");
                return;
            }

            String role = ui.getCurrentUserRole() != null ? ui.getCurrentUserRole() : "";
            switch (role) {
                case "rider" -> ui.showScreen("userhome");
                case "driver" -> ui.showScreen("providerhome");
                case "admin" -> ui.showScreen("admin");
                default -> ui.showScreen("login");
            }
        });

        logoutBtn.addActionListener(e -> {
            if (loggedIn) {
                loggedIn = false;
                ui.setLoggedIn(false);
                ui.showScreen("login");
            } else {
                JOptionPane.showMessageDialog(this,
                        "You are not logged in!",
                        "Logout Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        add(backBtn);
        add(homeBtn);
        add(logoutBtn);
    }

    // ===== Dynamic visibility control =====
    public void update(String currentScreen) {
        boolean isLogin = currentScreen.equals("login");
        boolean isSignup = currentScreen.contains("signup");
        boolean isHome = currentScreen.equals("userhome")
                || currentScreen.equals("providerhome")
                || currentScreen.equals("admin");

        if (isLogin) {
            backBtn.setVisible(false);
            homeBtn.setVisible(false);
            logoutBtn.setVisible(false);
        } else if (isSignup) {
            backBtn.setVisible(true);
            homeBtn.setVisible(false);
            logoutBtn.setVisible(false);
        } else if (isHome) {
            backBtn.setVisible(false);
            homeBtn.setVisible(false);
            logoutBtn.setVisible(true);
        } else {
            backBtn.setVisible(true);
            homeBtn.setVisible(true);
            logoutBtn.setVisible(true);
        }
    }
}
