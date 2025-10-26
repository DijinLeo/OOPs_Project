	package model;
	
	/**
	 * Ride model — Represents a single ride in the system.
	 */
	public class Ride {
	    private int id;
	    private String driverId;
	    private String driverName;
	    private String fromLocation;
	    private String toLocation;
	    private String date;
	    private String time;
	    private String vehicleType;
	    private int seatsAvailable;
	    private String status;
	
	    public Ride() {}
	
	    public Ride(String driverId, String fromLocation, String toLocation, String date,
	                String time, String vehicleType, int seatsAvailable, String status) {
	        this.driverId = driverId;
	        this.fromLocation = fromLocation;
	        this.toLocation = toLocation;
	        this.date = date;
	        this.time = time;
	        this.vehicleType = vehicleType;
	        this.seatsAvailable = seatsAvailable;
	        this.status = status;
	    }
	
	    // ✅ Getters & Setters
	    public int getId() { return id; }
	    public void setId(int id) { this.id = id; }
	
	    public int getRideId() { return id; } // Alias for compatibility
	    public void setRideId(int id) { this.id = id; }
	
	    public String getDriverId() { return driverId; }
	    public void setDriverId(String driverId) { this.driverId = driverId; }
	
	    public String getDriverName() { return driverName; }
	    public void setDriverName(String driverName) { this.driverName = driverName; }
	
	    public String getFromLocation() { return fromLocation; }
	    public void setFromLocation(String fromLocation) { this.fromLocation = fromLocation; }
	
	    public String getToLocation() { return toLocation; }
	    public void setToLocation(String toLocation) { this.toLocation = toLocation; }
	
	    public String getDate() { return date; }
	    public void setDate(String date) { this.date = date; }
	
	    public String getTime() { return time; }
	    public void setTime(String time) { this.time = time; }
	
	    public String getVehicleType() { return vehicleType; }
	    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }
	
	    public int getSeatsAvailable() { return seatsAvailable; }
	    public void setSeatsAvailable(int seatsAvailable) { this.seatsAvailable = seatsAvailable; }
	
	    public String getStatus() { return status; }
	    public void setStatus(String status) { this.status = status; }
	
	    @Override
	    public String toString() {
	        return String.format("%s → %s | %s %s | %s seats | %s",
	                fromLocation, toLocation, date, time, seatsAvailable, status);
	    }
	}
