package com.example.crime;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;

public class ReportsAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<CrimeReport> crimeReports;

    public ReportsAdapter(Context context, ArrayList<CrimeReport> crimeReports) {
        this.context = context;
        this.crimeReports = crimeReports;
    }

    @Override
    public int getCount() {
        return crimeReports.size();
    }

    @Override
    public Object getItem(int position) {
        return crimeReports.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.report_item, null);
        }

        CrimeReport report = crimeReports.get(position);

        // Get references to the views in report_item.xml
        TextView crimeTypeTextView = convertView.findViewById(R.id.crimeTypeTextView);
        TextView descriptionTextView = convertView.findViewById(R.id.descriptionTextView);
        TextView usernameTextView = convertView.findViewById(R.id.usernameTextView);
        TextView statusTextView = convertView.findViewById(R.id.crimeStatusTextView);  // Add status TextView
  // Add status TextView

        // Populate the views with data
        crimeTypeTextView.setText(report.getCrimeType());
        descriptionTextView.setText("Description: " + report.getDescription());
        usernameTextView.setText("Reported by: " + report.getUsername());
        statusTextView.setText("Status: " + report.getStatus());  // Display status

        return convertView;
    }
}
