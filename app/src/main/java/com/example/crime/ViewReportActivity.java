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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ViewReportActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ReportsAdapter reportAdapter;
    private EditText etRemarks;
    private Button btnUpdateRemarks;
    private CrimeReport selectedReport;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_reports);

        recyclerView = findViewById(R.id.recyclerViewReports);
        etRemarks = findViewById(R.id.etRemarks);
        btnUpdateRemarks = findViewById(R.id.btnUpdateRemarks);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Fetch data passed from the previous activity
        ArrayList<CrimeReport> reports = getIntent().getParcelableArrayListExtra("reports");

        if (reports != null && !reports.isEmpty()) {
            Log.d("ViewReportActivity", "Received " + reports.size() + " reports.");

            reportAdapter = new ReportsAdapter(this, reports, this::onReportSelected); // Pass listener
            recyclerView.setAdapter(reportAdapter);
        } else {
            Log.e("ViewReportActivity", "No reports received or list is empty.");
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
        etRemarks.setText(report.getRemarks()); // Pre-fill existing remarks
        Toast.makeText(this, "Selected: " + report.getCrimeType(), Toast.LENGTH_SHORT).show();
    }


    private void updateRemarks(CrimeReport report, String remarks) {
        // Assuming 'reportId' is a unique ID for each report in your database
        DatabaseReference reportRef = FirebaseDatabase.getInstance()
                .getReference("crime_reports")
                .child(report.getUsername()).child(report.getCrimeType());  // Replace with unique user reference if applicable
                 // Adjust this key for the unique report

        // Update remarks in the database
        reportRef.child("remarks").setValue(remarks).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Remarks updated successfully.", Toast.LENGTH_SHORT).show();
                report.setRemarks(remarks);  // Update remarks locally
                reportAdapter.notifyDataSetChanged(); // Refresh the report list to show updated remarks
            } else {
                Toast.makeText(this, "Failed to update remarks.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
