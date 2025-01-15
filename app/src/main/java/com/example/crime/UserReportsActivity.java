package com.example.crime;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserReportsActivity extends AppCompatActivity {

    private ListView crimeReportsListView;
    private ArrayList<CrimeReport> crimeReports;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_reports);

        crimeReportsListView = findViewById(R.id.crimeReportsListView);
        crimeReports = new ArrayList<>();

        // Fetch reports from Firebase
        fetchCrimeReportsFromFirebase();
    }

    private void fetchCrimeReportsFromFirebase() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("crime_reports");

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                crimeReports.clear(); // Clear the previous list
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String crimeType = dataSnapshot.child("crimeType").getValue(String.class);
                    String description = dataSnapshot.child("description").getValue(String.class);
                    String status = dataSnapshot.child("status").getValue(String.class);
                    double latitude = dataSnapshot.child("latitude").getValue(Double.class);
                    double longitude = dataSnapshot.child("longitude").getValue(Double.class);
                    String username = dataSnapshot.child("username").getValue(String.class);

                    if (crimeType != null && description != null && status != null) {
                        // Add the crime report to the list
                        CrimeReport report = new CrimeReport(crimeType, description, status, latitude, longitude, username);
                        crimeReports.add(report);
                    }
                }

                // Set the adapter to the ListView
                CrimeReportsAdapter adapter = new CrimeReportsAdapter(UserReportsActivity.this, crimeReports);
                crimeReportsListView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserReportsActivity.this, "Failed to fetch data.", Toast.LENGTH_SHORT).show();
                Log.e("UserReportsActivity", "Error fetching data: " + error.getMessage());
            }
        });
    }

    // Adapter to display the crime reports
    public class CrimeReportsAdapter extends ArrayAdapter<CrimeReport> {
        public CrimeReportsAdapter(UserReportsActivity context, ArrayList<CrimeReport> reports) {
            super(context, 0, reports);
        }


        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.report_item, parent, false);
            }

            CrimeReport report = getItem(position);
            TextView crimeTypeTextView = convertView.findViewById(R.id.crimeTypeTextView);
            TextView descriptionTextView = convertView.findViewById(R.id.descriptionTextView);
            TextView statusTextView = convertView.findViewById(R.id.crimeStatusTextView);
            TextView usernameTextView = convertView.findViewById(R.id.usernameTextView); // Assuming you want to display username

            crimeTypeTextView.setText(report.getCrimeType());
            descriptionTextView.setText(report.getDescription());
            statusTextView.setText(report.getStatus());
            usernameTextView.setText(report.getUsername()); // Display username

            return convertView;
        }
    }
}
