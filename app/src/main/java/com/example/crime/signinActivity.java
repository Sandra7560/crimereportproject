package com.example.crime;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;



public class signinActivity extends AppCompatActivity {
    private static final String ADMIN_EMAIL = "admin@gmail.com";
    private static final String ADMIN_PASSWORD = "admin123";
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin); // Make sure your XML layout matches this

        mAuth = FirebaseAuth.getInstance();

        EditText emailField = findViewById(R.id.emailFieldSignIn);
        EditText passwordField = findViewById(R.id.passwordFieldSignIn);
        Button signInButton = findViewById(R.id.signInButton);
        TextView registerLink = findViewById(R.id.registerLink);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView forgotPasswordLink = findViewById(R.id.forgotPasswordLink);

        // Forgot password logic
        forgotPasswordLink.setOnClickListener(v -> {
            Intent intent = new Intent(signinActivity.this, forgotPassword.class);
            startActivity(intent);
        });

        // Sign-in button logic
        signInButton.setOnClickListener(v -> {
            String email = emailField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check for admin credentials first
            if (email.equals(ADMIN_EMAIL) && password.equals(ADMIN_PASSWORD)) {
                // Admin sign-in success
                Toast.makeText(this, "Admin Sign-in Successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(signinActivity.this, AdminHome.class);  // Replace with your Admin Home Activity
                startActivity(intent);
                finish();
            } else {
                // Firebase authentication for regular users
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(this, "Sign-in successful!", Toast.LENGTH_SHORT).show();
                                // Navigate to client home page (replace with your client activity)
                                startActivity(new Intent(signinActivity.this, clientHome.class));
                                finish();
                            } else {
                                Toast.makeText(this, "Sign-in failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        // Register link to navigate to registration page
        registerLink.setOnClickListener(v -> {
            startActivity(new Intent(this, registerActivity.class));  // Make sure this activity exists
        });
    }
}