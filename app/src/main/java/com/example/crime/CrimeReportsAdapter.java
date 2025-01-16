package com.example.crime;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CrimeReportsAdapter extends RecyclerView.Adapter<CrimeReportsAdapter.CrimeReportViewHolder> {
    private List<CrimeReport> crimeReports;
    private OnCrimeReportDeleteListener deleteListener;

    public CrimeReportsAdapter(List<CrimeReport> crimeReports, OnCrimeReportDeleteListener deleteListener) {
        this.crimeReports = crimeReports;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public CrimeReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_crime_report, parent, false);
        return new CrimeReportViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull CrimeReportViewHolder holder, int position) {
        CrimeReport report = crimeReports.get(position);

        if (report != null) {
            Log.d("CrimeReportsAdapter", "Binding report: " + report.getCrimeType()); // Log report
            holder.crimeTypeTextView.setText(report.getCrimeType());
            holder.descriptionTextView.setText(report.getDescription());
            holder.remarksTextView.setText(report.getRemarks());

            // Add click listener to pass the data
            holder.itemView.setOnClickListener(v -> {
                if (report != null) {
                    Log.d("CrimeReportsAdapter", "Passing CrimeReport: " + report);
                    Intent intent = new Intent(v.getContext(), ViewReportActivity.class);
                    intent.putExtra("crimeReport", report); // Pass the object
                    v.getContext().startActivity(intent);

                } else {
                    Log.e("CrimeReportsAdapter", "CrimeReport is null, cannot pass to ViewReportActivity.");
                }
            });
            holder.deleteButton.setOnClickListener(v -> {
                if (deleteListener != null) {
                    // Trigger delete action
                    deleteListener.onDelete(report, position);
                }
            });

        } else {
            Log.e("CrimeReportsAdapter", "CrimeReport is null at position: " + position);
        }

    }


    @Override
    public int getItemCount() {
        return crimeReports != null ? crimeReports.size() : 0;
    }

    public static class CrimeReportViewHolder extends RecyclerView.ViewHolder {
        TextView crimeTypeTextView, descriptionTextView, remarksTextView;
        ImageButton deleteButton;

        public CrimeReportViewHolder(View itemView) {
            super(itemView);
            crimeTypeTextView = itemView.findViewById(R.id.crimeTypeTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            remarksTextView = itemView.findViewById(R.id.remarksTextView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }

    public interface OnCrimeReportDeleteListener {
        void onDelete(CrimeReport crimeReport, int position);
    }
}
