package view;

import controller.AdminController;
import dao.DBConnection;
import model.DriverApplication;
import util.UIStyleHelper;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.List;

public class AdminDashboardPanel extends JPanel {
    private final AdminController adminController = new AdminController();
    private JPanel appListPanel, userListPanel, rideListPanel;
    private Timer autoRefreshTimer;

    private String currentAppFilter = "all";
    private String currentUserRoleFilter = "all";
    private String currentRideStatusFilter = "all";

    public AdminDashboardPanel(RideShareMobileUI ui) {
        setLayout(new BorderLayout());
        setBackground(UIStyleHelper.BG_COLOR);

        JLabel title = UIStyleHelper.createTitle("👑 Admin Dashboard");
        add(title, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP);
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 14));

        tabs.add("Driver Applications", createApplicationsTab());
        tabs.add("Users", createUsersTab());
        tabs.add("Rides", createRidesTab());

        add(tabs, BorderLayout.CENTER);

        // Auto refresh all tabs every 5 seconds
        autoRefreshTimer = new Timer(5000, e -> {
            refreshApplicationsList();
            refreshUsersList();
            refreshRidesList();
        });
        autoRefreshTimer.start();
    }

    // ==================== DRIVER APPLICATIONS ====================
    private JPanel createApplicationsTab() {
        JPanel panel = UIStyleHelper.createContentPanel("Driver Applications");
        panel.setLayout(new BorderLayout());

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        filterPanel.setBackground(Color.WHITE);
        JButton allBtn = UIStyleHelper.styleButton(new JButton(" All"), new Color(100, 149, 237));
        JButton approvedBtn = UIStyleHelper.styleButton(new JButton(" Approved"), new Color(46, 204, 113));
        JButton pendingBtn = UIStyleHelper.styleButton(new JButton(" Pending"), new Color(241, 196, 15));
        JButton rejectedBtn = UIStyleHelper.styleButton(new JButton(" Rejected"), new Color(231, 76, 60));

        allBtn.addActionListener(e -> { currentAppFilter = "all"; refreshApplicationsList(); });
        approvedBtn.addActionListener(e -> { currentAppFilter = "approved"; refreshApplicationsList(); });
        pendingBtn.addActionListener(e -> { currentAppFilter = "pending"; refreshApplicationsList(); });
        rejectedBtn.addActionListener(e -> { currentAppFilter = "rejected"; refreshApplicationsList(); });

        filterPanel.add(allBtn);
        filterPanel.add(approvedBtn);
        filterPanel.add(pendingBtn);
        filterPanel.add(rejectedBtn);

        appListPanel = new JPanel();
        appListPanel.setLayout(new BoxLayout(appListPanel, BoxLayout.Y_AXIS));
        appListPanel.setBackground(Color.WHITE);

        JScrollPane scroll = new JScrollPane(appListPanel);
        scroll.setBorder(null);

        panel.add(filterPanel, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        refreshApplicationsList();
        return panel;
    }

    private void refreshApplicationsList() {
        if (appListPanel == null) return;
        appListPanel.removeAll();

        try {
            List<DriverApplication> apps = adminController.getAllApplications();
            for (DriverApplication da : apps) {
                if (currentAppFilter.equals("all") || da.getStatus().equalsIgnoreCase(currentAppFilter)) {
                    appListPanel.add(createApplicationCard(da));
                    appListPanel.add(Box.createVerticalStrut(8));
                }
            }

            if (appListPanel.getComponentCount() == 0)
                appListPanel.add(UIStyleHelper.createInfoLabel("No applications found."));
        } catch (Exception e) {
            appListPanel.add(UIStyleHelper.createInfoLabel("Error loading applications: " + e.getMessage()));
        }

        appListPanel.revalidate();
        appListPanel.repaint();
    }

    private JPanel createApplicationCard(DriverApplication da) {
        JPanel card = UIStyleHelper.createContentPanel("");
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        // === Left: Application Info ===
        JLabel info = new JLabel("<html><b>" + da.getName() + "</b> — " + da.getVehicleModel() +
                "<br/>License: " + da.getLicenseNumber() +
                "<br/>File: " + da.getLicenseFile() + "</html>");
        info.setFont(UIStyleHelper.TEXT_FONT);
        card.add(info, BorderLayout.CENTER);

        // === Right: Status + Buttons ===
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setOpaque(false);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 5));

        // ===== Compact Status Badge =====
        JLabel statusLabel = new JLabel();
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setOpaque(true);
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
        statusLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        String status = da.getStatus().toLowerCase();
        switch (status) {
            case "approved" -> {
                statusLabel.setText("✅ APPROVED");
                statusLabel.setBackground(new Color(46, 204, 113));
            }
            case "rejected" -> {
                statusLabel.setText("❌ REJECTED");
                statusLabel.setBackground(new Color(231, 76, 60));
            }
            default -> {
                statusLabel.setText("🕓 PENDING");
                statusLabel.setBackground(new Color(241, 196, 15));
                addBlinkEffect(statusLabel);
            }
        }

        statusLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, 1, true),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));

        // ===== Buttons Panel =====
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 5));
        btnPanel.setOpaque(false);

        JButton approveBtn = UIStyleHelper.styleButton(new JButton("Approve"), UIStyleHelper.SUCCESS_COLOR);
        JButton rejectBtn = UIStyleHelper.styleButton(new JButton("Reject"), Color.RED);

        if (status.equals("approved")) {
            JButton changeReject = UIStyleHelper.styleButton(new JButton("Change to Reject"), new Color(255, 77, 77));
            changeReject.addActionListener(e -> updateStatus(da.getId(), "rejected"));
            btnPanel.add(changeReject);

        } else if (status.equals("rejected")) {
            JButton changeApprove = UIStyleHelper.styleButton(new JButton("Change to Approve"), new Color(88, 214, 141));
            changeApprove.addActionListener(e -> updateStatus(da.getId(), "approved"));
            btnPanel.add(changeApprove);

        } else {
            approveBtn.addActionListener(e -> updateStatus(da.getId(), "approved"));
            rejectBtn.addActionListener(e -> updateStatus(da.getId(), "rejected"));
            btnPanel.add(approveBtn);
            btnPanel.add(rejectBtn);
        }

        // Combine badge and buttons in right panel
        rightPanel.add(statusLabel);
        rightPanel.add(Box.createVerticalStrut(8));
        rightPanel.add(btnPanel);

        card.add(rightPanel, BorderLayout.EAST);
        return card;
    }

    /** 🌟 Makes pending status label gently blink between two shades of yellow */
    private void addBlinkEffect(JLabel label) {
        Color bright = new Color(255, 215, 0);
        Color dim = new Color(241, 196, 15);
        Timer blinkTimer = new Timer(500, null);
        blinkTimer.addActionListener(e -> {
            Color current = label.getBackground();
            label.setBackground(current.equals(bright) ? dim : bright);
        });
        blinkTimer.start();
    }

    private void updateStatus(String id, String status) {
        try {
            if (adminController.updateApplicationStatus(id, status)) {
                refreshApplicationsList();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating status: " + e.getMessage());
        }
    }

    // ==================== USERS TAB ====================
    private JPanel createUsersTab() {
        JPanel panel = UIStyleHelper.createContentPanel("Registered Users");
        panel.setLayout(new BorderLayout());

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        filterPanel.setBackground(Color.WHITE);
        JButton allBtn = UIStyleHelper.styleButton(new JButton(" All"), new Color(100, 149, 237));
        JButton riderBtn = UIStyleHelper.styleButton(new JButton(" Rider"), new Color(52, 152, 219));
        JButton driverBtn = UIStyleHelper.styleButton(new JButton(" Driver"), new Color(46, 204, 113));
        JButton adminBtn = UIStyleHelper.styleButton(new JButton(" Admin"), new Color(155, 89, 182));

        allBtn.addActionListener(e -> { currentUserRoleFilter = "all"; refreshUsersList(); });
        riderBtn.addActionListener(e -> { currentUserRoleFilter = "rider"; refreshUsersList(); });
        driverBtn.addActionListener(e -> { currentUserRoleFilter = "driver"; refreshUsersList(); });
        adminBtn.addActionListener(e -> { currentUserRoleFilter = "admin"; refreshUsersList(); });

        filterPanel.add(allBtn);
        filterPanel.add(riderBtn);
        filterPanel.add(driverBtn);
        filterPanel.add(adminBtn);

        userListPanel = new JPanel();
        userListPanel.setLayout(new BoxLayout(userListPanel, BoxLayout.Y_AXIS));
        userListPanel.setBackground(Color.WHITE);

        JScrollPane scroll = new JScrollPane(userListPanel);
        scroll.setBorder(null);

        panel.add(filterPanel, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        refreshUsersList();
        return panel;
    }

    private void refreshUsersList() {
        if (userListPanel == null) return;
        userListPanel.removeAll();

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM users")) {

            while (rs.next()) {
                String id = rs.getString("id");
                String username = rs.getString("username");
                String role = rs.getString("role");

                if (currentUserRoleFilter.equals("all") || currentUserRoleFilter.equalsIgnoreCase(role)) {
                    JPanel card = new JPanel(new BorderLayout());
                    card.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

                    Color bgColor = switch (role.toLowerCase()) {
                        case "admin" -> new Color(155, 89, 182);
                        case "driver" -> new Color(46, 204, 113);
                        case "rider" -> new Color(52, 152, 219);
                        default -> new Color(149, 165, 166);
                    };

                    card.setBackground(bgColor);

                    JLabel lbl = new JLabel("<html><font color='white'><b>" + username + "</b> — (" + role + ")" +
                            "<br/>🆔 ID: " + id + "</font></html>");
                    lbl.setFont(UIStyleHelper.TEXT_FONT);

                    card.add(lbl, BorderLayout.CENTER);
                    userListPanel.add(card);
                    userListPanel.add(Box.createVerticalStrut(8));
                }
            }

            if (userListPanel.getComponentCount() == 0)
                userListPanel.add(UIStyleHelper.createInfoLabel("No users found."));

        } catch (SQLException e) {
            userListPanel.add(UIStyleHelper.createInfoLabel("Error loading users: " + e.getMessage()));
        }

        userListPanel.revalidate();
        userListPanel.repaint();
    }

    // ==================== RIDES TAB ====================
    private JPanel createRidesTab() {
        JPanel panel = UIStyleHelper.createContentPanel("All Rides");
        panel.setLayout(new BorderLayout());

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        filterPanel.setBackground(Color.WHITE);
        JButton allBtn = UIStyleHelper.styleButton(new JButton(" All"), new Color(100, 149, 237));
        JButton openBtn = UIStyleHelper.styleButton(new JButton(" Open"), new Color(46, 204, 113));
        JButton doneBtn = UIStyleHelper.styleButton(new JButton(" Completed"), new Color(52, 152, 219));
        JButton cancelledBtn = UIStyleHelper.styleButton(new JButton(" Cancelled"), new Color(231, 76, 60));

        allBtn.addActionListener(e -> { currentRideStatusFilter = "all"; refreshRidesList(); });
        openBtn.addActionListener(e -> { currentRideStatusFilter = "open"; refreshRidesList(); });
        doneBtn.addActionListener(e -> { currentRideStatusFilter = "completed"; refreshRidesList(); });
        cancelledBtn.addActionListener(e -> { currentRideStatusFilter = "cancelled"; refreshRidesList(); });

        filterPanel.add(allBtn);
        filterPanel.add(openBtn);
        filterPanel.add(doneBtn);
        filterPanel.add(cancelledBtn);

        rideListPanel = new JPanel();
        rideListPanel.setLayout(new BoxLayout(rideListPanel, BoxLayout.Y_AXIS));
        rideListPanel.setBackground(Color.WHITE);

        JScrollPane scroll = new JScrollPane(rideListPanel);
        scroll.setBorder(null);

        panel.add(filterPanel, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        refreshRidesList();
        return panel;
    }

    private void refreshRidesList() {
        if (rideListPanel == null) return;
        rideListPanel.removeAll();

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM rides")) {

            while (rs.next()) {
                String status = rs.getString("status");
                if (currentRideStatusFilter.equals("all") || currentRideStatusFilter.equalsIgnoreCase(status)) {
                    String from = rs.getString("from_location");
                    String to = rs.getString("to_location");
                    String driver = rs.getString("driver_name");
                    Date date = rs.getDate("date");

                    JLabel lbl = new JLabel("<html><b>" + from + " → " + to + "</b><br/>Driver: "
                            + driver + " | " + date + " | " + status + "</html>");
                    lbl.setFont(UIStyleHelper.TEXT_FONT);

                    JPanel card = UIStyleHelper.createContentPanel("");
                    card.add(lbl, BorderLayout.CENTER);
                    rideListPanel.add(card);
                    rideListPanel.add(Box.createVerticalStrut(8));
                }
            }

            if (rideListPanel.getComponentCount() == 0)
                rideListPanel.add(UIStyleHelper.createInfoLabel("No rides found."));

        } catch (SQLException e) {
            rideListPanel.add(UIStyleHelper.createInfoLabel("Error loading rides: " + e.getMessage()));
        }

        rideListPanel.revalidate();
        rideListPanel.repaint();
    }
}
