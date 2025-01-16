package com.example.crime;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ReportsAdapter extends RecyclerView.Adapter<ReportsAdapter.ReportViewHolder> {

    private Context context;
    private List<CrimeReport> reports;
    private OnReportClickListener listener;

    public ReportsAdapter(Context context, List<CrimeReport> reports, OnReportClickListener listener) {
        this.context = context;
        this.reports = reports;
        this.listener = listener;
    }


    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.report_item, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        CrimeReport report = reports.get(position);

        holder.tvCrimeType.setText(report.getCrimeType());
        holder.tvDescription.setText(report.getDescription());
        holder.tvRemarks.setText(report.getRemarks());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onReportClick(report);
            }
        });
    }

    @Override
    public int getItemCount() {
        return reports != null ? reports.size() : 0;
    }

    public static class ReportViewHolder extends RecyclerView.ViewHolder {
        TextView tvCrimeType, tvDescription, tvRemarks;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCrimeType = itemView.findViewById(R.id.crimeTypeTextView);
            tvDescription = itemView.findViewById(R.id.descriptionTextView);
            tvRemarks = itemView.findViewById(R.id.remarksTextView);
        }
    }

    public interface OnReportClickListener {
        void onReportClick(CrimeReport report);
    }
}
