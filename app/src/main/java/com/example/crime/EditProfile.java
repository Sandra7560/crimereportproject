package com.example.crime;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;



public class EditProfile extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText editName, editEmail, editAddress, editSIN;
    private ImageView profileImage;
    private Button uploadPhotoButton, updateButton;

    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());

        // Initialize UI components
        editName = findViewById(R.id.editName);
        editEmail = findViewById(R.id.editEmail);
        editAddress = findViewById(R.id.editAddress);
        editSIN = findViewById(R.id.editSIN);
        profileImage = findViewById(R.id.profileImage);
        uploadPhotoButton = findViewById(R.id.uploadPhotoButton);
        updateButton = findViewById(R.id.updateButton);

        // Prefill fields with current user data if available
        if (user != null) {
            editName.setText(user.getDisplayName());
            editEmail.setText(user.getEmail());

            // Fetch additional data from Firebase Realtime Database
            databaseReference.get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    editAddress.setText(task.getResult().child("address").getValue(String.class));
                    editSIN.setText(task.getResult().child("sin").getValue(String.class));
                }
            });
        }

        // Photo upload functionality (placeholder)
        uploadPhotoButton.setOnClickListener(v -> {
            Toast.makeText(this, "Photo upload functionality coming soon!", Toast.LENGTH_SHORT).show();
        });

        // Update profile
        updateButton.setOnClickListener(v -> {
            String newName = editName.getText().toString().trim();
            String newEmail = editEmail.getText().toString().trim();
            String newAddress = editAddress.getText().toString().trim();
            String newSIN = editSIN.getText().toString().trim();

            if (newName.isEmpty() || newEmail.isEmpty() || newAddress.isEmpty() || newSIN.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Update name in Firebase Authentication
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(newName)
                    .build();

            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(EditProfile.this, "Profile updated!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(EditProfile.this, "Update failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

            // Update email in Firebase Authentication
            user.updateEmail(newEmail)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(EditProfile.this, "Email updated!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(EditProfile.this, "Email update failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

            // Save additional info to Firebase Realtime Database
            databaseReference.child("address").setValue(newAddress);
            databaseReference.child("sin").setValue(newSIN);

            // Update Firebase Authentication user profile
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                currentUser.updateProfile(
                        new UserProfileChangeRequest.Builder().setDisplayName(newName).build()
                ).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();

                        // Navigate to ProfileActivity
                        Intent intent = new Intent(EditProfile.this, Profile.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, "Error updating profile: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}