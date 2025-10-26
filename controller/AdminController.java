package controller;

import dao.DriverApplicationDAO;
import model.DriverApplication;

import java.sql.SQLException;
import java.util.List;

public class AdminController {
    private final DriverApplicationDAO dao = new DriverApplicationDAO();

    // Get only pending application 
    public List<DriverApplication> getPendingApplications() throws SQLException {
        return dao.findByStatus("pending");
    }

    // New method: Get ALL applications (approved , pending , rejected) 
    public List<DriverApplication> getAllApplications() throws SQLException {
        return dao.findAll();
    }

    // Approve or reject a driver application 
    public boolean updateApplicationStatus(String driverId, String status) throws SQLException {
        return dao.updateStatus(driverId, status);
    }
}
