package com.example.crime;

public class SOSAlert {
    private String userId;
    private String username;
    private String emergencyMessage;
    private String emergencyContact;
    private String location;
    private long timestamp;
    private String status;
    private String deviceInfo;
    private String emergencyContactEmail;

    // No-argument constructor required by Firebase
    public SOSAlert() {
    }

    // Parameterized constructor
    public SOSAlert(String userId, String username, String emergencyMessage, String emergencyContact, String location, long timestamp, String status, String deviceInfo, String emergencyContactEmail) {
        this.userId = userId;
        this.username = username;
        this.emergencyMessage = emergencyMessage;
        this.emergencyContact = emergencyContact;
        this.location = location;
        this.timestamp = timestamp;
        this.status = status;
        this.deviceInfo = deviceInfo;
        this.emergencyContactEmail = emergencyContactEmail;
    }

    // Getters and setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmergencyMessage() {
        return emergencyMessage;
    }

    public void setEmergencyMessage(String emergencyMessage) {
        this.emergencyMessage = emergencyMessage;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public String getEmergencyContactEmail() {
        return emergencyContactEmail;
    }

    public void setEmergencyContactEmail(String emergencyContactEmail) {
        this.emergencyContactEmail = emergencyContactEmail;
    }

    @Override
    public String toString() {
        return "SOSAlert{" +
                "userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", emergencyMessage='" + emergencyMessage + '\'' +
                ", emergencyContact='" + emergencyContact + '\'' +
                ", location='" + location + '\'' +
                ", timestamp=" + timestamp +
                ", status='" + status + '\'' +
                ", deviceInfo='" + deviceInfo + '\'' +
                ", emergencyContactEmail='" + emergencyContactEmail + '\'' +
                '}';
    }
}
