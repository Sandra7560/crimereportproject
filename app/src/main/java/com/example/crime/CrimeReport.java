package com.example.crime;

import android.os.Parcel;
import android.os.Parcelable;

public class CrimeReport implements Parcelable {
    private String crimeType;
    private String description;
    private String status;
    private double latitude;
    private double longitude;
    private String username;

    public CrimeReport(String crimeType, String description, String status, double latitude, double longitude, String username) {
        this.crimeType = crimeType;
        this.description = description;
        this.status = status;
        this.latitude = latitude;
        this.longitude = longitude;
        this.username = username;
    }

    protected CrimeReport(Parcel in) {
        crimeType = in.readString();
        description = in.readString();
        status = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        username = in.readString();
    }

    public static final Creator<CrimeReport> CREATOR = new Creator<CrimeReport>() {
        @Override
        public CrimeReport createFromParcel(Parcel in) {
            return new CrimeReport(in);
        }

        @Override
        public CrimeReport[] newArray(int size) {
            return new CrimeReport[size];
        }
    };

    public CrimeReport(String crimeType, String description, double selectedLatitude, double selectedLongitude, String userId, String pending) {
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(crimeType);
        dest.writeString(description);
        dest.writeString(status);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(username);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // Getters
    public String getCrimeType() {
        return crimeType;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getUsername() {
        return username;
    }
}
