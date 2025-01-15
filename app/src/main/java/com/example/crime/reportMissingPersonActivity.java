package com.example.crime;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class reportMissingPersonActivity extends AppCompatActivity {

    private EditText nameEditText, descriptionEditText, locationEditText;
    private ImageView photoImageView;
    private Button uploadPhotoButton, submitButton;
    private Uri photoUri;

    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_missing_person);

        // Initialize Firebase components
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        // Initialize views
        nameEditText = findViewById(R.id.nameEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        locationEditText = findViewById(R.id.locationEditText);
        photoImageView = findViewById(R.id.photoImageView);
        uploadPhotoButton = findViewById(R.id.uploadPhotoButton);
        submitButton = findViewById(R.id.submitButton);

        // Initialize the ActivityResultLauncher for selecting image
        ActivityResultContracts.GetContent getContent = new ActivityResultContracts.GetContent();
        registerForActivityResult(getContent, result -> {
            if (result != null) {
                // Handle the selected image
                photoUri = result;
                photoImageView.setImageURI(photoUri);
            } else {
                Toast.makeText(reportMissingPersonActivity.this, "You haven't picked an image", Toast.LENGTH_SHORT).show();
            }
        });

        // Set up upload photo button to trigger image selection
        uploadPhotoButton.setOnClickListener(v -> openGallery());

        // Set up submit button to handle form submission
        submitButton.setOnClickListener(v -> submitReport());
    }

    @SuppressLint("IntentReset")
    private void openGallery() {
        // Open the gallery to select an image
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivity(intent);  // Make sure the onActivityResult gets triggered
    }

    private void submitReport() {
        // Get the report details
        String name = nameEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String location = locationEditText.getText().toString().trim();

        if (name.isEmpty() || description.isEmpty() || location.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (photoUri == null) {
            Toast.makeText(this, "Please upload a photo", Toast.LENGTH_SHORT).show();
            return;
        }

        // Upload the photo to Firebase Storage
        uploadPhotoToFirebase(name, description, location);
    }

    private void uploadPhotoToFirebase(String name, String description, String location) {
        // Get the current user's ID
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "You must be logged in to report a missing person", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = user.getUid();

        // Create a reference to store the image in Firebase Storage
        StorageReference photoRef = storageReference.child("missing_person_photos/" + userId + "_" + System.currentTimeMillis());

        // Upload the image
        photoRef.putFile(photoUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Get the download URL of the uploaded photo
                    photoRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Create the report data
                        MissingPersonReport report = new MissingPersonReport(
                                name,
                                description,
                                location,
                                uri.toString(), // Store the photo URL
                                userId
                        );

                        // Save the report to Firebase Realtime Database
                        DatabaseReference reportsRef = database.getReference("missing_person_reports");
                        String reportId = reportsRef.push().getKey();
                        if (reportId != null) {
                            reportsRef.child(reportId).setValue(report)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(reportMissingPersonActivity.this, "Report submitted successfully", Toast.LENGTH_SHORT).show();
                                        finish(); // Close the activity after submitting
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(reportMissingPersonActivity.this, "Failed to submit report", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(reportMissingPersonActivity.this, "Failed to upload photo", Toast.LENGTH_SHORT).show();
                });
    }

    // Model class to represent a missing person report
    public static class MissingPersonReport {
        private String name;
        private String description;
        private String location;
        private String photoUrl;
        private String userId;

        public MissingPersonReport(String name, String description, String location, String photoUrl, String userId) {
            this.name = name;
            this.description = description;
            this.location = location;
            this.photoUrl = photoUrl;
            this.userId = userId;
        }

        // Getters and setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getPhotoUrl() {
            return photoUrl;
        }

        public void setPhotoUrl(String photoUrl) {
            this.photoUrl = photoUrl;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }
    }
}
