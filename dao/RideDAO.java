package dao;

import model.Ride;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *  DAO for rides table — handles DB operations.
 */
public class RideDAO {

    //  Create new ride with duplicate prevention
    public boolean createRide(Ride ride) throws SQLException {
        Connection conn = DBConnection.getConnection();

        String checkSql = "SELECT COUNT(*) FROM rides WHERE driver_id = ? AND date = ? AND to_location = ? AND status = 'Open'";
        try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
            checkPs.setString(1, ride.getDriverId());
            checkPs.setString(2, ride.getDate());
            checkPs.setString(3, ride.getToLocation());
            ResultSet rs = checkPs.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("⚠️ Duplicate ride detected for driver: " + ride.getDriverId());
                return false;
            }
        }

        String sql = "INSERT INTO rides (driver_id, from_location, to_location, date, time, vehicle_type, available_seats, status) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ride.getDriverId());
            ps.setString(2, ride.getFromLocation());
            ps.setString(3, ride.getToLocation());
            ps.setString(4, ride.getDate());
            ps.setString(5, ride.getTime());
            ps.setString(6, ride.getVehicleType());
            ps.setInt(7, ride.getSeatsAvailable());
            ps.setString(8, ride.getStatus());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ SQL Error in createRide(): " + e.getMessage());
            return false;
        }
    }

    // Retrieve all rides
    public List<Ride> findAllOpenRides() throws SQLException {
        List<Ride> rides = new ArrayList<>();
        Connection conn = DBConnection.getConnection();
        String sql = "SELECT * FROM rides ORDER BY id DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Ride ride = new Ride(
                        rs.getString("driver_id"),
                        rs.getString("from_location"),
                        rs.getString("to_location"),
                        rs.getString("date"),
                        rs.getString("time"),
                        rs.getString("vehicle_type"),
                        rs.getInt("available_seats"),
                        rs.getString("status")
                );
                rides.add(ride);
            }
        }
        return rides;
    }
    public boolean updateRideStatus(int rideId, String status) throws SQLException {
        String sql = "UPDATE rides SET status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, rideId);
            return ps.executeUpdate() > 0;
        }
    }

    public Ride findById(int rideId) throws SQLException {
        String sql = "SELECT * FROM rides WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, rideId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Ride(
                        rs.getString("driver_id"),
                        rs.getString("from_location"),
                        rs.getString("to_location"),
                        rs.getString("date"),
                        rs.getString("time"),
                        rs.getString("vehicle_type"),
                        rs.getInt("available_seats"),
                        rs.getString("status")
                    );
                }
            }
        }
        return null; 
    }


    public List<Ride> findRidesById(String id) throws SQLException {
        List<Ride> rides = new ArrayList<>();
        String sql = "SELECT * FROM rides WHERE driver_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Ride ride = new Ride(
                        rs.getString("driver_id"),
                        rs.getString("from_location"),
                        rs.getString("to_location"),
                        rs.getString("date"),
                        rs.getString("time"),
                        rs.getString("vehicle_type"),
                        rs.getInt("available_seats"),
                        rs.getString("status")
                    );
                    rides.add(ride);
                }
            }
        }
        return rides;
    }

}
