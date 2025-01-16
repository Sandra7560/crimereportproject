package com.example.crime;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdminHome extends AppCompatActivity {
    private DatabaseReference userDatabaseReference;
    private Button logoutButton, postCrimeNewsButton, viewReportsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        // Initialize buttons
        logoutButton = findViewById(R.id.logoutButton);
        postCrimeNewsButton = findViewById(R.id.postCrimeNewsButton);
        viewReportsButton = findViewById(R.id.viewReportsButton);

        // Set button listeners
        logoutButton.setOnClickListener(v -> handleLogout());
        postCrimeNewsButton.setOnClickListener(v -> handlePostCrimeNews());
        viewReportsButton.setOnClickListener(v -> fetchAllCrimeReports());
    }


    private void fetchAllCrimeReports() {
        DatabaseReference reportsRef = FirebaseDatabase.getInstance().getReference("crime_reports");

        reportsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ArrayList<CrimeReport> allCrimeReports = new ArrayList<>();
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) { // Loop through each user node
                        for (DataSnapshot reportSnapshot : userSnapshot.getChildren()) { // Loop through each report node
                            CrimeReport report = reportSnapshot.getValue(CrimeReport.class);
                            if (report != null) {
                                // Retrieve the key as reportId
                                String reportId = reportSnapshot.getKey();
                                Log.d("REPORT_ID", "Fetched reportId: " + reportId);

                                // Optionally attach the reportId to the remarks field for easier debugging
                                report.setRemarks("Report ID: " + reportId);
                                allCrimeReports.add(report);
                            }
                        }
                    }

                    // Pass data to ViewReportActivity
                    Intent intent = new Intent(AdminHome.this, ViewReportActivity.class);
                    intent.putParcelableArrayListExtra("reports", allCrimeReports);
                    startActivity(intent);
                } else {
                    Log.d("FIREBASE_DEBUG", "No crime reports found.");
                    Toast.makeText(AdminHome.this, "No crime reports available.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FIREBASE_DEBUG", "Database error: " + databaseError.getMessage());
                Toast.makeText(AdminHome.this, "Failed to fetch reports.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleLogout() {
        // Add logout logic here (e.g., clearing user data, Firebase auth sign out)
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();

        // Redirect to SignInActivity
        Intent intent = new Intent(this, signinActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear activity stack
        startActivity(intent);

        finish(); // Close the current activity
    }


    private void handlePostCrimeNews() {
        // Navigate to the PostCrimeNews activity
        startActivity(new Intent(AdminHome.this, PostCrimeNews.class));
    }
}
