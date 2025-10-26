package controller;

import dao.DriverApplicationDAO;
import model.DriverApplication;
import java.sql.SQLException;

/**
  Controller that handles driver application logic.
 * Acts as the bridge between the DAO and UI.
 */
public class DriverApplicationController {

    private final DriverApplicationDAO dao = new DriverApplicationDAO();

    /**
     * ✅ Submits a driver application (no password here — password is handled by AuthController).
     * @param app DriverApplication object
     * @return boolean indicating success
     */
    public boolean submitApplication(DriverApplication app) throws SQLException {
        return dao.createApplication(app);
    }

    /**
     * ✅ Fetches a driver application by driver ID.
     * @param id driver ID
     * @return DriverApplication object or null if not found
     */
    public DriverApplication getApplicationById(String id) throws SQLException {
        return dao.findById(id);
    }

    /**
     * ✅ Updates the application status (Approve or Reject)
     * @param id driver ID
     * @param status new status ("approved" or "rejected")
     * @return true if update succeeded
     */
    public boolean updateStatus(String id, String status) throws SQLException {
        return dao.updateStatus(id, status);
    }
}
