package com.example.crime;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class UserReportsActivity extends AppCompatActivity implements CrimeReportsAdapter.OnCrimeReportDeleteListener {

    private RecyclerView crimeReportsRecyclerView;
    private CrimeReportsAdapter adapter;
    private ArrayList<CrimeReport> crimeReports;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_reports);

        crimeReportsRecyclerView = findViewById(R.id.crimeReportsRecyclerView);
        crimeReportsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get the list of crime reports passed from the previous activity
        crimeReports = getIntent().getParcelableArrayListExtra("crimeReports");

        if (crimeReports != null) {
            // Pass the delete listener to the adapter
            adapter = new CrimeReportsAdapter(crimeReports, this);
            crimeReportsRecyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void onDelete(CrimeReport crimeReport, int position) {
        // Show a toast to confirm deletion
        Toast.makeText(this, "Deleting report: " + crimeReport.getCrimeType(), Toast.LENGTH_SHORT).show();

        // Remove the report from the list
        crimeReports.remove(position);

        // Notify the adapter that an item was removed
        adapter.notifyItemRemoved(position);
    }
    public void onBackPressed(View view) {
        super.onBackPressed(); // This goes back to the previous activity
    }
}
