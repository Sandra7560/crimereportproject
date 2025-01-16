package com.example.crime;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.MenuInflater;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class clientHome extends AppCompatActivity {

    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 101;
    private DatabaseReference userDatabaseReference;
    private LocationCallback locationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_home);
        Button btnReadNews = findViewById(R.id.btnReadNews);
        btnReadNews.setOnClickListener(v -> {
            Intent intent = new Intent(clientHome.this, NewsActivity.class);
            startActivity(intent);
        });
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        TextView welcomeTextView = findViewById(R.id.welcomeTextView);
        ImageView profileIcon = findViewById(R.id.userProfileIcon);
        Button emergencySOSButton = findViewById(R.id.emergencySOSButton);
        Button reportCrimeButton = findViewById(R.id.reportButton);
        Button viewHistoryButton = findViewById(R.id.viewHistoryButton);
        // Set up Report Crime Button functionality
        reportCrimeButton.setOnClickListener(v -> openReportCrimePage());

        // Set up SOS Button functionality
        emergencySOSButton.setOnClickListener(v -> checkAndSendSOS());
        viewHistoryButton.setOnClickListener(v -> fetchUserCrimeReports());
        // Retrieve user information and update UI
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String username = user.getDisplayName();
            welcomeTextView.setText("Welcome, " + (username != null ? username : "User") + "!");
            userDatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
        } else {
            welcomeTextView.setText("Welcome, Guest!");
        }

        profileIcon.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(clientHome.this, profileIcon);
            MenuInflater inflater = popupMenu.getMenuInflater();
            inflater.inflate(R.menu.profile_menu, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.menu_profile) {
                    Intent profileIntent = new Intent(clientHome.this, Profile.class);
                    startActivity(profileIntent);
                    return true;
                } else if (item.getItemId() == R.id.menu_logout) {
                    FirebaseAuth.getInstance().signOut();
                    Intent loginIntent = new Intent(clientHome.this, signinActivity.class);
                    startActivity(loginIntent);
                    finish();
                    return true;
                }
                return false;
            });

            popupMenu.show();
        });
    }



    private void openReportCrimePage() {
        Intent intent = new Intent(clientHome.this, reportCrimeActivity.class);
        startActivity(intent);
    }

    private void fetchUserCrimeReports() {
        // Get the logged-in user's username
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String username = currentUser != null ? currentUser.getDisplayName() : null;

        if (username == null) {
            Toast.makeText(this, "Unable to fetch username. Please log in again.", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d("FIREBASE_DEBUG", "Querying database with username: " + username);

        // Firebase database reference
        DatabaseReference reportsRef = FirebaseDatabase.getInstance().getReference("crime_reports")
                .child(username); //

        reportsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Data exists, proceed with handling
                    ArrayList<CrimeReport> crimeReports = new ArrayList<>();
                    for (DataSnapshot reportSnapshot : dataSnapshot.getChildren()) {
                        CrimeReport report = reportSnapshot.getValue(CrimeReport.class);
                        if (report != null) {
                            crimeReports.add(report);
                        }
                    }
                    // Pass data to next activity
                    Intent intent = new Intent(clientHome.this, UserReportsActivity.class);
                    intent.putParcelableArrayListExtra("crimeReports", crimeReports);
                    startActivity(intent);
                } else {
                    Log.d("FIREBASE_DEBUG", "No crime reports found for " + username);
                    Toast.makeText(clientHome.this, "No crime reports found for this user.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Log the error details
                Log.e("FIREBASE_ERROR", "Error: " + databaseError.getMessage());
                Toast.makeText(clientHome.this, "Failed to fetch reports. Please try again later.", Toast.LENGTH_SHORT).show();
            }
        });

    }



    private void checkAndSendSOS() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.SEND_SMS,
                    Manifest.permission.CALL_PHONE
            }, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        // Request location using FusedLocationProvider
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        fetchEmergencyContactAndSendSOS(location);
                    } else {
                        Toast.makeText(this, "Unable to fetch location. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("SOS_DEBUG", "Error fetching location: " + e.getMessage());
                    Toast.makeText(this, "Failed to get location.", Toast.LENGTH_SHORT).show();
                });
    }

    private void fetchEmergencyContactAndSendSOS(Location location) {
        userDatabaseReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                String emergencyContact = task.getResult().child("emergencyContact").getValue(String.class);
                String emergencyEmail = task.getResult().child("emergencyEmail").getValue(String.class);

                if (emergencyContact != null && emergencyEmail != null) {
                    String emergencyMessage = "Emergency! I need help. My location is: \n" +
                            "https://www.google.com/maps?q=" + location.getLatitude() + "," + location.getLongitude();
                    sendSOSAlert(location, emergencyMessage, emergencyContact, emergencyEmail);
                } else {
                    Toast.makeText(this, "Emergency contact details not set.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Error fetching emergency contact details.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendSOSAlert(Location location, String emergencyMessage, String emergencyContact, String emergencyEmail) {
        sendSOSToFirebase(location, emergencyMessage, emergencyContact);
        sendSMS(emergencyContact, emergencyMessage);
        sendEmailAlert(emergencyEmail, "Emergency SOS Alert", emergencyMessage);
        makePhoneCall(emergencyContact);
    }

    private void sendSMS(String emergencyContact, String emergencyMessage) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(emergencyContact, null, emergencyMessage, null, null);
            Toast.makeText(this, "Emergency SOS sent via SMS!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("SOS_DEBUG", "Error sending SMS: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, "Failed to send SMS. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendSOSToFirebase(Location location, String emergencyMessage, String emergencyContact) {
        String emergencyLocation = "https://www.google.com/maps?q=" + location.getLatitude() + "," + location.getLongitude();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentUser != null ? currentUser.getUid() : "anonymous";
        String username = currentUser != null ? currentUser.getDisplayName() : "Unknown";
        long timestamp = System.currentTimeMillis();
        String deviceInfo = Build.MODEL;

        SOSAlert sosAlert = new SOSAlert(
                userId,
                username,
                emergencyMessage,
                emergencyContact,
                emergencyLocation,
                timestamp,
                "pending",
                deviceInfo,
                "sandra7560@gmail.com"
        );

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        String alertId = database.child("sos_alerts").push().getKey();

        if (alertId != null) {
            database.child("sos_alerts").child(alertId).setValue(sosAlert)
                    .addOnSuccessListener(aVoid -> Toast.makeText(this, "SOS alert sent successfully!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> {
                        Log.e("SOS_DEBUG", "Error saving SOS alert: " + e.getMessage());
                        Toast.makeText(this, "Failed to send SOS alert.", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void makePhoneCall(String phoneNumber) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(callIntent);
    }

    private void sendEmailAlert(String emailAddress, String subject, String message) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailAddress});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, message);

        try {
            startActivity(Intent.createChooser(emailIntent, "Send email..."));
            Toast.makeText(this, "Email client opened!", Toast.LENGTH_SHORT).show();
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(this, "No email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            boolean locationPermissionGranted = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
            boolean smsPermissionGranted = grantResults.length > 1 && grantResults[1] == PackageManager.PERMISSION_GRANTED;
            boolean callPermissionGranted = grantResults.length > 2 && grantResults[2] == PackageManager.PERMISSION_GRANTED;

            if (locationPermissionGranted && smsPermissionGranted && callPermissionGranted) {
                checkAndSendSOS();
            } else {
                Toast.makeText(this, "Permissions are required for SOS functionality.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onCancelled(DatabaseError databaseError) {
        // Log the full error details
        Log.e("UserReportsActivity", "Error Code: " + databaseError.getCode());
        Log.e("UserReportsActivity", "Error Message: " + databaseError.getMessage());
        Log.e("UserReportsActivity", "Error Details: " + databaseError.getDetails());

        // Display a Toast with the error message
        Toast.makeText(clientHome.this, "Error fetching reports: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
    }
}
