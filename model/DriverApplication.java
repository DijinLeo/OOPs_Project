package model;

public class DriverApplication {
    private String id;
    private String name;
    private String vehicleModel;
    private String licenseNumber;
    private String licenseFile;
    private String status;

    // Default constructor (needed for DAO use)
    public DriverApplication() {}

    //  Full constructor (matches what you use in DriverApplicationPanel)
    public DriverApplication(String id, String name, String vehicleModel,
                             String licenseNumber, String licenseFile, String status) {
        this.id = id;
        this.name = name;
        this.vehicleModel = vehicleModel;
        this.licenseNumber = licenseNumber;
        this.licenseFile = licenseFile;
        this.status = status;
    }

    // === Getters and Setters ===
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getVehicleModel() { return vehicleModel; }
    public void setVehicleModel(String vehicleModel) { this.vehicleModel = vehicleModel; }

    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }

    public String getLicenseFile() { return licenseFile; }
    public void setLicenseFile(String licenseFile) { this.licenseFile = licenseFile; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
