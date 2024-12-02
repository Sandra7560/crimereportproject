package com.example.crime;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Profile extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    private ImageView profileImageView;
    private TextView profileName, profileEmail, profileAddress, profileSIN;
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
        profileSIN = findViewById(R.id.profileSIN);
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
                    String sin = snapshot.child("sin").getValue(String.class);

                    profileAddress.setText("Address: " + (address != null ? address : "N/A"));
                    profileSIN.setText("SIN: " + (sin != null ? sin : "N/A"));
                }
            });
        }

        // Navigate to Edit Profile
        editProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(Profile.this, EditProfile.class);
            startActivity(intent);
        });

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> onBackPressed()); // Go back to the previous activity
    }
    }
