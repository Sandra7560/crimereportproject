package com.example.crime;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;

public class reportCrimeActivity extends AppCompatActivity {

    private static final int LOCATION_REQUEST_CODE = 100;
    private double selectedLatitude = 0.0;
    private double selectedLongitude = 0.0;

    private AutoCompleteTextView crimeTypeDropdown;
    private EditText crimeDescriptionEditText;
    private Button selectLocationButton, submitCrimeReportButton;

    private String[] crimeTypes = {"Theft", "Assault", "Vandalism", "Fraud", "Robbery", "Arson", "Traffic"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_crime);

        // Initialize UI elements
        crimeTypeDropdown = findViewById(R.id.crimeTypeDropdown);
        crimeDescriptionEditText = findViewById(R.id.crimeDescriptionEditText);
        selectLocationButton = findViewById(R.id.selectLocationButton);
        submitCrimeReportButton = findViewById(R.id.submitCrimeReportButton);

        // Populate crime type dropdown
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                crimeTypes
        );
        crimeTypeDropdown.setAdapter(adapter);

        // Set location button listener
        selectLocationButton.setOnClickListener(v -> {
            if (selectedLatitude != 0.0 && selectedLongitude != 0.0) {
                Toast.makeText(this, "Location already selected: " + selectedLatitude + ", " + selectedLongitude, Toast.LENGTH_SHORT).show();
            } else {
                Intent mapIntent = new Intent(this, MapActivity.class);
                startActivityForResult(mapIntent, LOCATION_REQUEST_CODE);
            }
        });

        // Set submit button listener
        submitCrimeReportButton.setOnClickListener(v -> submitCrimeReport());
    }

    private void submitCrimeReport() {
        String crimeType = crimeTypeDropdown.getText().toString().trim();
        String description = crimeDescriptionEditText.getText().toString().trim();

        // Validate input
        if (!Arrays.asList(crimeTypes).contains(crimeType)) {
            Toast.makeText(this, "Please select a valid crime type", Toast.LENGTH_SHORT).show();
            return;
        }
        if (description.isEmpty()) {
            Toast.makeText(this, "Please provide a description of the crime", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedLatitude == 0.0 || selectedLongitude == 0.0) {
            Toast.makeText(this, "Please select a location for the crime", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the current user's UID or use "Anonymous" if not authenticated
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : "Anonymous";

        // Firebase database reference
        DatabaseReference database = FirebaseDatabase.getInstance()
                .getReference("crime_reports")
                .child(userId); // Group reports by user

        // Create a new CrimeReport object
        CrimeReport newReport = new CrimeReport(
                crimeType,
                description,
                selectedLatitude,
                selectedLongitude,
                userId,
                "Pending"
        );

        // Disable submit button during submission
        submitCrimeReportButton.setEnabled(false);
        Toast.makeText(this, "Submitting your report...", Toast.LENGTH_SHORT).show();

        // Push the report to Firebase
        database.push().setValue(newReport)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Crime report submitted successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to submit crime report. Please try again.", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                })
                .addOnCompleteListener(task -> submitCrimeReportButton.setEnabled(true));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LOCATION_REQUEST_CODE && resultCode == RESULT_OK) {
            String location = data.getStringExtra("location");

            if (location != null) {
                try {
                    // Parse the latitude and longitude from the location string
                    String[] latLng = location.split(",");
                    selectedLatitude = Double.parseDouble(latLng[0]);
                    selectedLongitude = Double.parseDouble(latLng[1]);
                    Toast.makeText(this, "Location selected: " + location, Toast.LENGTH_SHORT).show();
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    Toast.makeText(this, "Error parsing location", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "No location selected", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
