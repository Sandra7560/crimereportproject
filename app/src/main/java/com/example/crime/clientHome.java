package com.example.crime;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class clientHome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_home);
        TextView welcomeTextView = findViewById(R.id.welcomeTextView);

        // Retrieve the user information
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        if (user != null) {
//            String username = user.getDisplayName(); // You can also retrieve other details like email or uid
//            welcomeTextView.setText("Welcome!");
//        } else {
//            welcomeTextView.setText("Welcome, Guest!");
//        }
        Button viewProfileButton = findViewById(R.id.profileButton);
        viewProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(clientHome.this, Profile.class);
            startActivity(intent);
        });
    }
}