package com.example.crime;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Profile extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    private ImageView profileImageView;
    private TextView profileName, profileEmail, profileAddress, profileCell;
    private Button editProfileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Firebase Authentication and Database
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());

        // Initialize UI components
        profileImageView = findViewById(R.id.profileImageView);
        profileName = findViewById(R.id.profileName);
        profileEmail = findViewById(R.id.profileEmail);
        profileAddress = findViewById(R.id.profileAddress);
        profileCell = findViewById(R.id.profileCell); // Replacing SIN with Cell Number
        editProfileButton = findViewById(R.id.editProfileButton);

        // Populate the user profile
        if (user != null) {
            profileEmail.setText("Email: " + user.getEmail());
            profileName.setText("Name: " + user.getDisplayName());

            // Fetch additional data from Firebase Realtime Database
            databaseReference.get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    DataSnapshot snapshot = task.getResult();
                    String address = snapshot.child("address").getValue(String.class);
                    String cellNumber = snapshot.child("cell").getValue(String.class); // Updated to fetch cell number

                    profileAddress.setText("Address: " + (address != null ? address : "N/A"));
                    profileCell.setText("Cell Number: " + (cellNumber != null ? cellNumber : "N/A")); // Display cell number
                }
            });
        }

        // Navigate to Edit Profile
        editProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(Profile.this, EditProfile.class);
            startActivity(intent);
        });

        // Back Button
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> onBackPressed()); // Go back to the previous activity
    }
}
