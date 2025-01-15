package com.example.crime;

import android.Manifest;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        TextView welcomeTextView = findViewById(R.id.welcomeTextView);
        ImageView profileIcon = findViewById(R.id.userProfileIcon);
        Button emergencySOSButton = findViewById(R.id.emergencySOSButton);
        Button reportCrimeButton = findViewById(R.id.reportButton);
        Button myCrimeReportsButton = findViewById(R.id.myCrimeReportsButton);
        myCrimeReportsButton.setOnClickListener(v -> fetchUserCrimeReports());

        // Set up Report Crime Button functionality
        reportCrimeButton.setOnClickListener(v -> openReportCrimePage());

        // Set up SOS Button functionality
        emergencySOSButton.setOnClickListener(v -> checkAndSendSOS());

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
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Please log in to view your reports.", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("crime_reports");

        databaseReference.orderByChild("username").equalTo(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                if (task.getResult().getChildrenCount() == 0) {
                    Toast.makeText(this, "No active crime reports found.", Toast.LENGTH_SHORT).show();
                    return;
                }

                ArrayList<CrimeReport> crimeReports = new ArrayList<>();
                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                    CrimeReport report = snapshot.getValue(CrimeReport.class);
                    if (report != null) {
                        crimeReports.add(report);
                    }
                }

                // Open a new activity or dialog to display the reports
                Intent intent = new Intent(this, UserReportsActivity.class);
                intent.putParcelableArrayListExtra("crimeReports", crimeReports);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Failed to fetch reports. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void checkAndSendSOS() {
        // Check permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

            // Request permissions
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.SEND_SMS,
                    Manifest.permission.CALL_PHONE
            }, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        // Request high-accuracy location
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);  // Update interval (10 seconds)
        locationRequest.setFastestInterval(5000);  // Fastest interval (5 seconds)

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null && locationResult.getLocations().size() > 0) {
                    Location location = locationResult.getLastLocation();
                    if (location != null) {
                        fetchEmergencyContactAndSendSOS(location);
                        // Stop location updates after getting the location
                        fusedLocationClient.removeLocationUpdates(locationCallback);
                    }
                }
            }
        };

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private void fetchEmergencyContactAndSendSOS(Location location) {
        // Fetch emergency contact details from the database
        userDatabaseReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                String emergencyContact = task.getResult().child("emergencyContact").getValue(String.class);
                String emergencyEmail = task.getResult().child("emergencyEmail").getValue(String.class);

                if (emergencyContact != null && emergencyEmail != null) {
                    String emergencyMessage = "Emergency! I need help. My location is: \n" +
                            "https://www.google.com/maps?q=" + location.getLatitude() + "," + location.getLongitude();
                    sendSOSAlert(location, emergencyMessage, emergencyContact, emergencyEmail);
                } else {
                    Toast.makeText(clientHome.this, "Emergency contact details not set.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(clientHome.this, "Error fetching emergency contact details.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendSOSAlert(Location location, String emergencyMessage, String emergencyContact, String emergencyEmail) {
        // Send SOS to Firebase
        sendSOSToFirebase(location, emergencyMessage, emergencyContact);

        // Send SMS to emergency contact
        sendSMS(emergencyContact, emergencyMessage);

        // Send Email alert
        sendEmailAlert(emergencyEmail, "Emergency SOS Alert", emergencyMessage);

        // Make phone call to emergency contact
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
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "SOS alert sent successfully!", Toast.LENGTH_SHORT).show();
                    })
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
            if (grantResults.length > 0) {
                boolean locationPermissionGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean smsPermissionGranted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                boolean callPermissionGranted = grantResults[2] == PackageManager.PERMISSION_GRANTED;

                if (locationPermissionGranted && smsPermissionGranted && callPermissionGranted) {
                    checkAndSendSOS();
                } else {
                    Toast.makeText(this, "Location, SMS, and Phone permissions are required for SOS functionality.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
