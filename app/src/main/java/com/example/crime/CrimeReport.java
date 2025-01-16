package com.example.crime;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class CrimeReport implements Parcelable {

    private String crimeType;
    private String description;
    private String remarks;
    private String username;
    private Double latitude;  // Keep as Double
    private Double longitude; // Keep as Double

    // Default constructor
    public CrimeReport() {}

    // Constructor to handle latitude and longitude as String or Double
    public CrimeReport( String crimeType, String description, Object latitude, Object longitude, String username, String remarks) {

        this.crimeType = crimeType;
        this.description = description;
        this.username = username;
        this.remarks = remarks;

        // Handle latitude conversion to Double
        if (latitude instanceof String) {
            try {
                this.latitude = Double.parseDouble((String) latitude);
            } catch (NumberFormatException e) {
                this.latitude = null;  // Handle invalid number format
            }
        } else if (latitude instanceof Double) {
            this.latitude = (Double) latitude;
        }

        // Handle longitude conversion to Double
        if (longitude instanceof String) {
            try {
                this.longitude = Double.parseDouble((String) longitude);
            } catch (NumberFormatException e) {
                this.longitude = null;  // Handle invalid number format
            }
        } else if (longitude instanceof Double) {
            this.longitude = (Double) longitude;
        }
    }

    // Parcelable implementation
    protected CrimeReport(Parcel in) {
        crimeType = in.readString();
        description = in.readString();
        remarks = in.readString();
        username = in.readString();
        latitude = in.readDouble();  // Read Double
        longitude = in.readDouble(); // Read Double

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

    // Getters and Setters
    public String getCrimeType() {
        return crimeType;
    }

    public void setCrimeType(String crimeType) {
        this.crimeType = crimeType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }





    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Object latitude) {
        // Handle latitude conversion to Double
        if (latitude instanceof String) {
            try {
                this.latitude = Double.parseDouble((String) latitude);
            } catch (NumberFormatException e) {
                this.latitude = null;
            }
        } else if (latitude instanceof Double) {
            this.latitude = (Double) latitude;
        }
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Object longitude) {
        // Handle longitude conversion to Double
        if (longitude instanceof String) {
            try {
                this.longitude = Double.parseDouble((String) longitude);
            } catch (NumberFormatException e) {
                this.longitude = null;
            }
        } else if (longitude instanceof Double) {
            this.longitude = (Double) longitude;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(crimeType);
        dest.writeString(description);
        dest.writeString(remarks);
        dest.writeString(username);
        dest.writeDouble(latitude != null ? latitude : 0.0);  // Handle null
        dest.writeDouble(longitude != null ? longitude : 0.0); // Handle null

     }



}
