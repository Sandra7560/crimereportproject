package com.example.crime;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewReportActivity extends AppCompatActivity implements  OnMapReadyCallback{

    private RecyclerView recyclerView;
    private ReportsAdapter reportAdapter;
    private EditText etRemarks;
    private Button btnUpdateRemarks;
    private CrimeReport selectedReport;

    private GoogleMap googleMap;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_reports);

        recyclerView = findViewById(R.id.recyclerViewReports);
        etRemarks = findViewById(R.id.etRemarks);
        btnUpdateRemarks = findViewById(R.id.btnUpdateRemarks);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync((OnMapReadyCallback) this);
        }
        // Fetch data passed from the previous activity
        ArrayList<CrimeReport> reports = getIntent().getParcelableArrayListExtra("reports");

        if (reports != null && !reports.isEmpty()) {
            Log.d("ViewReportActivity", "Received " + reports.size() + " reports.");
            reportAdapter = new ReportsAdapter(this, reports, this::onReportSelected);
            recyclerView.setAdapter(reportAdapter);
        } else {
            Log.e("ViewReportActivity", "No reports received or list is empty.");
            Toast.makeText(this, "No crime reports available.", Toast.LENGTH_SHORT).show();
            reportAdapter = new ReportsAdapter(this, new ArrayList<>(), this::onReportSelected); // Set empty list
            recyclerView.setAdapter(reportAdapter);
        }

        btnUpdateRemarks.setOnClickListener(v -> {
            if (selectedReport != null) {
                updateRemarks(selectedReport, etRemarks.getText().toString());
            } else {
                Toast.makeText(this, "Please select a report first.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void onReportSelected(CrimeReport report) {
        selectedReport = report;
        etRemarks.setText(report.getRemarks());
        if (googleMap != null && report.getLatitude() != null && report.getLongitude() != null) {
            LatLng location = new LatLng(report.getLatitude(), report.getLongitude());

            googleMap.clear(); // Clear previous markers
            googleMap.addMarker(new MarkerOptions().position(location).title(report.getCrimeType()));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 14));
        }
// Pre-fill existing remarks
        Toast.makeText(this, "Selected: " + report.getCrimeType(), Toast.LENGTH_SHORT).show();
    }


    private void updateRemarks(CrimeReport report, String remarks) {
        DatabaseReference reportsRef = FirebaseDatabase.getInstance()
                .getReference("crime_reports")
                .child(report.getUsername());

        reportsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean reportFound = false;

                for (DataSnapshot reportSnapshot : dataSnapshot.getChildren()) {
                    CrimeReport fetchedReport = reportSnapshot.getValue(CrimeReport.class);

                    if (fetchedReport != null && fetchedReport.getCrimeType().equals(report.getCrimeType())
                            && fetchedReport.getDescription().equals(report.getDescription())) {

                        // Update the remarks for the matched report
                        reportSnapshot.getRef().child("remarks").setValue(remarks).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(ViewReportActivity.this, "Remarks updated successfully.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ViewReportActivity.this, "Failed to update remarks.", Toast.LENGTH_SHORT).show();
                            }
                        });
                        reportFound = true;
                        break;
                    }
                }

                if (!reportFound) {
                    Toast.makeText(ViewReportActivity.this, "Report not found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FIREBASE_DEBUG", "Database error: " + databaseError.getMessage());
                Toast.makeText(ViewReportActivity.this, "Error accessing database.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void onMapReady(GoogleMap googleMap) {
        LatLng location = new LatLng(45.37229823816096, -73.66356108337641);
        googleMap.addMarker(new MarkerOptions().position(location).title("Crime Location"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
    }

}

