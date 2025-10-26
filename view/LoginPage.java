package view;

import controller.AuthController;
import dao.DriverApplicationDAO;
import model.DriverApplication;
import model.User;
import util.UIStyleHelper;

import javax.swing.*;
import java.awt.*;

public class LoginPage extends JPanel {
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final AuthController auth = new AuthController();

    public LoginPage(RideShareMobileUI ui) {
        setLayout(new BorderLayout());
        setBackground(UIStyleHelper.BG_COLOR);

        JLabel title = UIStyleHelper.createTitle("🚗 RideShare Login");
        title.setFont(UIStyleHelper.TITLE_FONT.deriveFont(Font.BOLD, 28f));
        title.setBorder(BorderFactory.createEmptyBorder(20,0,10,0));
        add(title, BorderLayout.NORTH);

        JPanel cardPanel = UIStyleHelper.createContentPanel("");
        cardPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12,12,12,12);
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(UIStyleHelper.TEXT_FONT);
        userLabel.setForeground(UIStyleHelper.TEXT_COLOR);
        usernameField = UIStyleHelper.styleTextField(new JTextField(18));

        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(UIStyleHelper.TEXT_FONT);
        passLabel.setForeground(UIStyleHelper.TEXT_COLOR);
        passwordField = UIStyleHelper.stylePasswordField(new JPasswordField(18));

        JButton loginButton = UIStyleHelper.createGradientButton("Login");
        JButton signUpButton = UIStyleHelper.createGradientButton("Create an Account");

        gbc.gridx = 0; gbc.gridy = 0;
        cardPanel.add(userLabel, gbc);
        gbc.gridx = 1;
        cardPanel.add(usernameField, gbc);
        gbc.gridy++; gbc.gridx = 0;
        cardPanel.add(passLabel, gbc);
        gbc.gridx = 1;
        cardPanel.add(passwordField, gbc);
        gbc.gridy++; gbc.gridx = 1; gbc.insets = new Insets(25,12,12,12);
        cardPanel.add(loginButton, gbc);
        gbc.gridy++; gbc.insets = new Insets(10,12,12,12);
        cardPanel.add(signUpButton, gbc);

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(UIStyleHelper.BG_COLOR);
        wrapper.add(cardPanel);
        add(wrapper, BorderLayout.CENTER);
        /*cardPanel.setBorder(BorderFactory.createCompoundBorder(
        	    cardPanel.getBorder(),
        	    BorderFactory.createMatteBorder(2, 2, 8, 2, new Color(0, 0, 0, 30))
        	));*/


        loginButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Please enter both username and password.",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                User user = auth.login(username, password);
                if (user == null) {
                    JOptionPane.showMessageDialog(this,
                        "Invalid credentials. Try again.",
                        "Login Failed",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                ui.setCurrentUserId(user.getId());
                ui.setCurrentUsername(user.getUsername());
                ui.setCurrentUserRole(user.getRole());
                ui.setLoggedIn(true);

                if ("driver".equalsIgnoreCase(user.getRole())) {
                    DriverApplicationDAO dao = new DriverApplicationDAO();
                    DriverApplication da = dao.findById(user.getId());
                    if (da == null) {
                        JOptionPane.showMessageDialog(this,
                            "You have not applied as a driver yet.\nPlease complete the driver application form.",
                            "Access Denied",
                            JOptionPane.WARNING_MESSAGE);
                        ui.showScreen("driver_application");
                        return;
                    }
                    switch (da.getStatus().toLowerCase()) {
                        case "pending"  -> {
                            JOptionPane.showMessageDialog(this,
                                "⏳ Your driver application is still pending approval by the admin.\nPlease try again later.",
                                "Not Approved Yet",
                                JOptionPane.INFORMATION_MESSAGE);
                            return;
                        }
                        case "rejected" -> {
                            JOptionPane.showMessageDialog(this,
                                "❌ Your driver application has been rejected.\nContact admin for clarification.",
                                "Access Denied",
                                JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        case "approved" -> {
                            ui.showScreen("providerhome");
                            JOptionPane.showMessageDialog(this,
                                "✅ Welcome, " + user.getUsername() + "!\nYour driver account is approved.",
                                "Login Successful",
                                JOptionPane.INFORMATION_MESSAGE);
                            return;
                        }
                        default -> {
                            JOptionPane.showMessageDialog(this,
                                "Unknown driver application status: " + da.getStatus(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    }
                }
                switch (user.getRole().toLowerCase()) {
                    case "admin" -> {
                        ui.showScreen("admin");
                        JOptionPane.showMessageDialog(this,
                            "👑 Welcome Admin " + user.getUsername() + "!");
                    }
                    case "rider" -> {
                        ui.showScreen("userhome");
                        JOptionPane.showMessageDialog(this,
                            "🚗 Welcome " + user.getUsername() + "!");
                    }
                    default -> {
                        JOptionPane.showMessageDialog(this,
                            "Unknown role. Contact admin.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                        ui.showScreen("login");
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Error: " + ex.getMessage(),
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        signUpButton.addActionListener(e -> ui.showScreen("signup_choice"));
    }
}
