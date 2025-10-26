	package view;
	
	import controller.AuthController;
	import dao.DriverApplicationDAO;
	import model.DriverApplication;
	import model.User;
	import util.UIStyleHelper;
	
	import javax.swing.*;
	import java.awt.*;
	import java.io.File;
	
	/**
	 * DriverApplicationPanel — driver application form that integrates with existing DAO & AuthController
	 */
	public class DriverApplicationPanel extends JPanel {
	    private final RideShareMobileUI ui;
	    private final DriverApplicationDAO applicationDAO = new DriverApplicationDAO();
	    private final AuthController authController = new AuthController();
	
	    private JTextField idField;
	    private JTextField nameField;
	    private JTextField vehicleField;
	    private JTextField licenseField;
	    private JPasswordField passwordField;
	    private JLabel statusLabel;
	    private File selectedFile;
	
	    public DriverApplicationPanel(RideShareMobileUI ui) {
	        this.ui = ui;
	        setLayout(new BorderLayout());
	        setBackground(UIStyleHelper.BG_COLOR);
	
	        JLabel title = UIStyleHelper.createTitle(" Driver Application");
	        add(title, BorderLayout.NORTH);
	
	        JPanel formPanel = UIStyleHelper.createContentPanel("");
	        formPanel.setLayout(new GridBagLayout());
	        formPanel.setBackground(Color.WHITE);
	
	        GridBagConstraints gbc = new GridBagConstraints();
	        gbc.insets = new Insets(10, 10, 10, 10);
	        gbc.fill = GridBagConstraints.HORIZONTAL;
	
	        idField = UIStyleHelper.styleTextField(new JTextField(15));
	        nameField = UIStyleHelper.styleTextField(new JTextField(15));
	        vehicleField = UIStyleHelper.styleTextField(new JTextField(15));
	        licenseField = UIStyleHelper.styleTextField(new JTextField(15));
	        passwordField = UIStyleHelper.stylePasswordField(new JPasswordField(15));
	
	        JButton uploadButton = UIStyleHelper.styleButton(new JButton(" Upload License"), UIStyleHelper.SECONDARY_COLOR);
	        JButton submitButton = UIStyleHelper.styleButton(new JButton("Submit Application"), UIStyleHelper.PRIMARY_COLOR);
	        JButton backButton = UIStyleHelper.styleButton(new JButton(" Back"), UIStyleHelper.PRIMARY_COLOR);
	
	        statusLabel = UIStyleHelper.createInfoLabel("");
	        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
	
	        int row = 0;
	        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Driver ID:"), gbc);
	        gbc.gridx = 1; formPanel.add(idField, gbc);
	        row++;
	        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Full Name:"), gbc);
	        gbc.gridx = 1; formPanel.add(nameField, gbc);
	        row++;
	        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Vehicle Model:"), gbc);
	        gbc.gridx = 1; formPanel.add(vehicleField, gbc);
	        row++;
	        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("License Number:"), gbc);
	        gbc.gridx = 1; formPanel.add(licenseField, gbc);
	        row++;
	        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Password:"), gbc);
	        gbc.gridx = 1; formPanel.add(passwordField, gbc);
	        row++;
	        gbc.gridx = 1; gbc.gridy = row; formPanel.add(uploadButton, gbc);
	        row++;
	        gbc.gridx = 1; gbc.gridy = row; formPanel.add(submitButton, gbc);
	        row++;
	        gbc.gridx = 1; gbc.gridy = row; formPanel.add(backButton, gbc);
	        row++;
	        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2; formPanel.add(statusLabel, gbc);
	
	        add(formPanel, BorderLayout.CENTER);
	
	        uploadButton.addActionListener(e -> {
	            JFileChooser chooser = new JFileChooser();
	            int r = chooser.showOpenDialog(this);
	            if (r == JFileChooser.APPROVE_OPTION) {
	                selectedFile = chooser.getSelectedFile();
	                JOptionPane.showMessageDialog(this, "File selected: " + selectedFile.getName());
	            }
	        });
	
	        submitButton.addActionListener(e -> submitApplication());
	        backButton.addActionListener(e -> ui.goBack());
	    }
	
	    private void submitApplication() {
	        String id = idField.getText().trim();
	        String name = nameField.getText().trim();
	        String vehicle = vehicleField.getText().trim();
	        String license = licenseField.getText().trim();
	        String password = new String(passwordField.getPassword()).trim();
	
	        if (id.isEmpty() || name.isEmpty() || vehicle.isEmpty() || license.isEmpty() || password.isEmpty()) {
	            JOptionPane.showMessageDialog(this, " Please fill all fields.", "Warning", JOptionPane.WARNING_MESSAGE);
	            return;
	        }
	
	        try {
	            // 1️⃣ Create user account
	            User newUser = new User(id, name, password, "driver");
	            authController.registerDriver(newUser);
	
	            // 2️⃣ Create driver application
	            DriverApplication da = new DriverApplication(id, name, vehicle, license,
	                    selectedFile == null ? "" : selectedFile.getAbsolutePath(),
	                    "pending");
	
	            boolean ok = applicationDAO.createApplication(da);
	
	            if (ok) {
	                JOptionPane.showMessageDialog(this, " Application submitted successfully!\nWait for admin approval.");
	                clearForm();
	                ui.showScreen("login");
	            } else {
	                JOptionPane.showMessageDialog(this, " Application submission failed (maybe already exists).",
	                        "Error", JOptionPane.ERROR_MESSAGE);
	            }
	
	        } catch (Exception ex) {
	            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
	                    "Database Error", JOptionPane.ERROR_MESSAGE);
	            ex.printStackTrace();
	        }
	    }
	
	    private void clearForm() {
	        idField.setText("");
	        nameField.setText("");
	        vehicleField.setText("");
	        licenseField.setText("");
	        passwordField.setText("");
	        selectedFile = null;
	    }
	
	    public void checkExistingApplication(String loggedUserId) {
	        if (loggedUserId == null || loggedUserId.isEmpty()) return;
	
	        try {
	            DriverApplication existing = applicationDAO.findById(loggedUserId);
	            if (existing != null) {
	                String s = existing.getStatus().toLowerCase();
	                switch (s) {
	                    case "pending" -> {
	                        statusLabel.setText(" Your application is still pending.");
	                        statusLabel.setForeground(Color.ORANGE);
	                    }
	                    case "approved" -> {
	                        statusLabel.setText(" Approved! You can log in now.");
	                        statusLabel.setForeground(new Color(0, 150, 0));
	                    }
	                    case "rejected" -> {
	                        statusLabel.setText(" Rejected. You can reapply.");
	                        statusLabel.setForeground(Color.RED);
	                    }
	                }
	            } else {
	                statusLabel.setText(" You haven’t applied yet.");
	                statusLabel.setForeground(Color.GRAY);
	            }
	        } catch (Exception e) {
	            statusLabel.setText(" Error checking application: " + e.getMessage());
	            statusLabel.setForeground(Color.RED);
	        }
	    }
	}
