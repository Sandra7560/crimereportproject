package com.example.crime;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ViewReportsActivity extends AppCompatActivity {

    private ListView reportsListView;
    private ArrayAdapter<String> reportsAdapter;
    private List<String> crimeReports;
    private List<String> reportIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_reports);

        // Initialize ListView and lists
        reportsListView = findViewById(R.id.reportsListView);  // Uncommented this line to initialize ListView
        crimeReports = new ArrayList<>();
        reportIds = new ArrayList<>();

        // Initialize ArrayAdapter with the list of reports
        reportsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, crimeReports);
        reportsListView.setAdapter(reportsAdapter);

        // Fetch reports from Firebase
        fetchReportsFromFirebase();

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());  // Navigate back or finish the activity
    }

    private void fetchReportsFromFirebase() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("crime_reports");

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                crimeReports.clear(); // Clear the previous list
                reportIds.clear();

                // Loop through all the child nodes of "crime_reports"
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String reportId = dataSnapshot.getKey(); // Get the report ID
                    String crimeType = dataSnapshot.child("crimeType").getValue(String.class);
                    String description = dataSnapshot.child("description").getValue(String.class);
                    String status = dataSnapshot.child("status").getValue(String.class);

                    if (crimeType != null && description != null && status != null) {
                        // Add the formatted report to the list for display
                        crimeReports.add("Crime: " + crimeType + "\nDescription: " + description + "\nStatus: " + status);
                        reportIds.add(reportId); // Add the report ID for future updates
                    }
                }

                // Notify the adapter to update the ListView
                reportsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Log errors during data fetching
                Log.e("ViewReportsActivity", "Error fetching data: " + error.getMessage());
            }
        });

        // Add a listener to handle item clicks for status updates
        reportsListView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedReportId = reportIds.get(position); // Get the report ID of the selected item
            updateReportStatus(selectedReportId);
        });
    }

    private void updateReportStatus(String reportId) {
        // Example update, you would want to integrate a Spinner for selecting new status
        String newStatus = "Resolved"; // Example of status update

        DatabaseReference reportRef = FirebaseDatabase.getInstance().getReference("crime_reports").child(reportId);
        reportRef.child("status").setValue(newStatus)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ViewReportsActivity.this, "Status updated successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ViewReportsActivity.this, "Failed to update status.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
