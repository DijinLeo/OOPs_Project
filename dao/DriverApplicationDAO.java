package dao;

import model.DriverApplication;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles CRUD operations for Driver Applications.
 */
public class DriverApplicationDAO {

    // ✅ Insert new driver application
	public boolean createApplication(DriverApplication app) throws SQLException {
	    Connection conn = DBConnection.getConnection();

	    String sql = "INSERT INTO driver_applications " +
	                 "(id, name, vehicle_model, license_number, license_file, status) " +
	                 "VALUES (?, ?, ?, ?, ?, ?)";

	    try (PreparedStatement ps = conn.prepareStatement(sql)) {
	        ps.setString(1, app.getId());
	        ps.setString(2, app.getName());
	        ps.setString(3, app.getVehicleModel());
	        ps.setString(4, app.getLicenseNumber());
	        ps.setString(5, app.getLicenseFile());
	        ps.setString(6, app.getStatus());

	        int rows = ps.executeUpdate();
	        return rows > 0;
	    } catch (SQLException e) {
	        System.err.println("❌ Error creating driver application: " + e.getMessage());
	        return false;
	    }
	}


    // ✅ Get all applications (Admin dashboard)
    public List<DriverApplication> findAll() throws SQLException {
        List<DriverApplication> list = new ArrayList<>();
        Connection conn = DBConnection.getConnection();
        String sql = "SELECT * FROM driver_applications ORDER BY name ASC";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                DriverApplication da = new DriverApplication();
                da.setId(rs.getString("id"));
                da.setName(rs.getString("name"));
                da.setVehicleModel(rs.getString("vehicle_model"));
                da.setLicenseNumber(rs.getString("license_number"));
                da.setLicenseFile(rs.getString("license_file"));
                da.setStatus(rs.getString("status"));
                list.add(da);
            }
        } catch (SQLException e) {
            System.err.println(" Error fetching all applications: " + e.getMessage());
        }
        return list;
    }

    // ✅ Find by status (Approved / Pending / Rejected)
    public List<DriverApplication> findByStatus(String status) throws SQLException {
        List<DriverApplication> list = new ArrayList<>();
        Connection conn = DBConnection.getConnection();
        String sql = "SELECT * FROM driver_applications WHERE status = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                DriverApplication da = new DriverApplication();
                da.setId(rs.getString("id"));
                da.setName(rs.getString("name"));
                da.setVehicleModel(rs.getString("vehicle_model"));
                da.setLicenseNumber(rs.getString("license_number"));
                da.setLicenseFile(rs.getString("license_file"));
                da.setStatus(rs.getString("status"));
                list.add(da);
            }
        } catch (SQLException e) {
            System.err.println(" Error fetching by status: " + e.getMessage());
        }
        return list;
    }

    // ✅ Find one by ID (used in DriverApplicationPanel)
    public DriverApplication findById(String id) throws SQLException {
        Connection conn = DBConnection.getConnection();
        String sql = "SELECT * FROM driver_applications WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                DriverApplication da = new DriverApplication();
                da.setId(rs.getString("id"));
                da.setName(rs.getString("name"));
                da.setVehicleModel(rs.getString("vehicle_model"));
                da.setLicenseNumber(rs.getString("license_number"));
                da.setLicenseFile(rs.getString("license_file"));
                da.setStatus(rs.getString("status"));
                return da;
            }
        } catch (SQLException e) {
            System.err.println(" Error fetching application by ID: " + e.getMessage());
        }
        return null;
    }

    // ✅ Update status (Approve or Reject)
    public boolean updateStatus(String id, String status) throws SQLException {
        Connection conn = DBConnection.getConnection();
        String sql = "UPDATE driver_applications SET status = ? WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, id);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println(" Error updating driver status: " + e.getMessage());
            return false;
        }
    }
}
