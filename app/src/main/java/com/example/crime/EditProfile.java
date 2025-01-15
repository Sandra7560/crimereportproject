package com.example.crime;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditProfile extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText editName, editEmail, editAddress, editCellNumber, editEmergencyContact, editEmergencyEmail;
    private Button updateButton;
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
        editCellNumber = findViewById(R.id.editCellNumber);
        editEmergencyContact = findViewById(R.id.editEmergencyContact);
        editEmergencyEmail = findViewById(R.id.editEmergencyEmail);
        updateButton = findViewById(R.id.updateButton);

        // Prefill fields with current user data if available
        if (user != null) {
            editName.setText(user.getDisplayName());
            editEmail.setText(user.getEmail());

            // Fetch additional data from Firebase Realtime Database
            databaseReference.get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    editAddress.setText(task.getResult().child("address").getValue(String.class));
                    editCellNumber.setText(task.getResult().child("cell").getValue(String.class));
                    editEmergencyContact.setText(task.getResult().child("emergencyContact").getValue(String.class));
                    editEmergencyEmail.setText(task.getResult().child("emergencyEmail").getValue(String.class));
                }
            });
        }

        // Update profile when button is clicked
        updateButton.setOnClickListener(v -> updateProfile());
    }

    private void updateProfile() {
        String newName = editName.getText().toString().trim();
        String newEmail = editEmail.getText().toString().trim();
        String newAddress = editAddress.getText().toString().trim();
        String newCellNumber = editCellNumber.getText().toString().trim();
        String newEmergencyContact = editEmergencyContact.getText().toString().trim();
        String newEmergencyEmail = editEmergencyEmail.getText().toString().trim();

        if (newName.isEmpty() || newEmail.isEmpty() || newAddress.isEmpty() || newCellNumber.isEmpty() || newEmergencyContact.isEmpty() || newEmergencyEmail.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        // Update email in Firebase Authentication
        user.updateEmail(newEmail)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Email updated!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Email update failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        // Save additional info (including emergency contact) to Firebase Realtime Database
        databaseReference.child("address").setValue(newAddress);
        databaseReference.child("cell").setValue(newCellNumber);
        databaseReference.child("emergencyContact").setValue(newEmergencyContact);
        databaseReference.child("emergencyEmail").setValue(newEmergencyEmail);

        // Navigate back to Profile Activity
        Intent intent = new Intent(EditProfile.this, Profile.class);
        startActivity(intent);
        finish();
    }
}
