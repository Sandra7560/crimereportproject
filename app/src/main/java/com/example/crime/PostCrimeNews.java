package com.example.crime;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PostCrimeNews extends AppCompatActivity {

    private EditText titleEditText, contentEditText;
    private Button postNewsButton;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_crime_news);

        // Initialize views
        titleEditText = findViewById(R.id.titleEditText);
        contentEditText = findViewById(R.id.contentEditText);
        postNewsButton = findViewById(R.id.postNewsButton);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("news");

        // Handle post news button click
        postNewsButton.setOnClickListener(v -> postNews());
    }

    private void postNews() {
        // Get input values
        String title = titleEditText.getText().toString().trim();
        String content = contentEditText.getText().toString().trim();

        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generate a unique key for the news post
        String newsId = databaseReference.push().getKey();

        if (newsId != null) {
            // Create a timestamp
            String timestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).format(new Date());

            // Create a news object
            News news = new News(title, content, timestamp, "Admin");

            // Save to Firebase
            databaseReference.child(newsId).setValue(news).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(PostCrimeNews.this, "News posted successfully", Toast.LENGTH_SHORT).show();

                    // Clear fields
                    titleEditText.setText("");
                    contentEditText.setText("");

                    // Navigate to Admin Home Page
                    startActivity(new Intent(PostCrimeNews.this, AdminHome.class));
                    finish(); // Close the current activity
                } else {
                    Toast.makeText(PostCrimeNews.this, "Failed to post news", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}
