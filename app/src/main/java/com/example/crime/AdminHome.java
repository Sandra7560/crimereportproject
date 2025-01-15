package com.example.crime;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class AdminHome extends AppCompatActivity {

    private Button logoutButton, postCrimeNewsButton, viewReportsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        // Initialize Buttons
        logoutButton = findViewById(R.id.logoutButton);
        postCrimeNewsButton = findViewById(R.id.postCrimeNewsButton);
        viewReportsButton = findViewById(R.id.viewReportsButton);

        // Set button listeners
        logoutButton.setOnClickListener(v -> handleLogout());
        postCrimeNewsButton.setOnClickListener(v -> handlePostCrimeNews());
        viewReportsButton.setOnClickListener(v -> handleViewReports());
    }

    // Handle Logout Logic
    private void handleLogout() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(AdminHome.this, signinActivity.class));
        finish();
    }

    // Handle Post New Crime News Logic
    private void handlePostCrimeNews() {
        // Navigate to Post Crime News activity
        startActivity(new Intent(AdminHome.this, PostCrimeNews.class));
    }

    // Handle View Reports Logic
    private void handleViewReports() {
        try {
            Intent intent = new Intent(AdminHome.this, ViewReportsActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            Log.e("AdminHome", "Error starting ViewReportsActivity", e);
            Toast.makeText(this, "Failed to open reports page", Toast.LENGTH_SHORT).show();
        }
    }
}
