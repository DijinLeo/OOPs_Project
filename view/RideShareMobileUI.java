package view;

import javax.swing.*;
import java.awt.*;
import java.util.Stack;

// === Import all panels ===
import view.LoginPage;
import view.SignUpChoicePanel;
import view.DriverApplicationPanel;
import view.RiderSignUpPanel;
import view.UserHomePanel;
import view.ProviderHome;
import view.OfferRidePanel;
import view.AdminDashboardPanel;
import view.SearchPanel;
import view.RiderTripsPanel;
import view.ProviderTripsPanel;
import view.NavigationBarPanel;

/**
 * Main UI Frame for Campus RideShare MVC
 * Handles navigation, login/logout states, and dynamic navbar greetings.
 */
public class RideShareMobileUI extends JFrame {
    private final CardLayout cardLayout;
    private final JPanel mainPanel;
    private final NavigationBarPanel navBar;

    private String currentScreen = "login";
    private String currentUserId;
    private String currentUserRole;
    private String currentUsername;  
    private boolean loggedIn = false;

    private final Stack<String> screenHistory = new Stack<>();

    private RiderTripsPanel riderTrips;
    private ProviderTripsPanel providerTrips;
    private DriverApplicationPanel driverApplication;

    public RideShareMobileUI() {
        setTitle("Campus RideShare — MVC");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // === Create Panels ===
        LoginPage login = new LoginPage(this);
        SignUpChoicePanel signupChoice = new SignUpChoicePanel(this);
        driverApplication = new DriverApplicationPanel(this);
        RiderSignUpPanel riderSignup = new RiderSignUpPanel(this);
        UserHomePanel userHome = new UserHomePanel(this);
        ProviderHome providerHome = new ProviderHome(this);
        OfferRidePanel offer = new OfferRidePanel(this);
        AdminDashboardPanel admin = new AdminDashboardPanel(this);
        SearchPanel search = new SearchPanel(this);
        riderTrips = new RiderTripsPanel(this);
        providerTrips = new ProviderTripsPanel(this);

        // === Add to layout ===
        mainPanel.add(login, "login");
        mainPanel.add(signupChoice, "signup_choice");
        mainPanel.add(driverApplication, "driver_application");
        mainPanel.add(riderSignup, "rider_signup");
        mainPanel.add(userHome, "userhome");
        mainPanel.add(providerHome, "providerhome");
        mainPanel.add(offer, "offer");
        mainPanel.add(admin, "admin");
        mainPanel.add(search, "search");
        mainPanel.add(riderTrips, "riderTrips");
        mainPanel.add(providerTrips, "providerTrips");

        navBar = new NavigationBarPanel(this);
        add(mainPanel, BorderLayout.CENTER);
        add(navBar, BorderLayout.SOUTH);

        showScreen("login");
    }

    // === Screen Control ===
    public void showScreen(String name) {
        if (!currentScreen.equals(name)) {
            screenHistory.push(currentScreen);
        }
        currentScreen = name;

        if ("riderTrips".equals(name) && riderTrips != null) {
            riderTrips.refreshData();
        } else if ("providerTrips".equals(name) && providerTrips != null) {
            try {
                providerTrips.refreshData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error loading provider trips: " + e.getMessage());
            }
        

        } else if ("driver_application".equals(name) && driverApplication != null) {
            driverApplication.checkExistingApplication(getCurrentUserId());
        }

        cardLayout.show(mainPanel, name);
        navBar.update(name);
    }

    // === Navigation ===
    public void goBack() {
        if (!screenHistory.isEmpty()) {
            String previous = screenHistory.pop();
            showScreen(previous);
        }
    }

    // === Logout: Reset Session ===
    public void resetUserSession() {
        currentUserId = null;
        currentUserRole = null;
        currentUsername = null;
        loggedIn = false;
        screenHistory.clear();
        showScreen("login");
        navBar.update("login");
    }

    public void showHomePage() {
        if ("driver".equalsIgnoreCase(currentUserRole)) {
            showScreen("providerhome");
        } else if ("rider".equalsIgnoreCase(currentUserRole)) {
            showScreen("userhome");
        } else if ("admin".equalsIgnoreCase(currentUserRole)) {
            showScreen("admin");
        } else {
            showScreen("search");
        }
    }

    // === Getters & Setters ===
    public String getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(String id) {
        this.currentUserId = id;
    }

    public String getCurrentUserRole() {
        return currentUserRole;
    }

    public void setCurrentUserRole(String role) {
        this.currentUserRole = role;
    }

    public String getCurrentUsername() {
        return currentUsername;
    }

    public void setCurrentUsername(String username) {
        this.currentUsername = username;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
        navBar.setLoggedIn(loggedIn);
    }

    // === Main Entry Point ===
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RideShareMobileUI().setVisible(true));
    }
}
