package com.example.crime;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CrimeReportAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<CrimeReport> crimeReportsList;

    public CrimeReportAdapter(Context context, ArrayList<CrimeReport> crimeReportsList) {
        this.context = context;
        this.crimeReportsList = crimeReportsList;
    }

    @Override
    public int getCount() {
        return crimeReportsList.size();
    }

    @Override
    public Object getItem(int position) {
        return crimeReportsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_crime_report, parent, false);
        }

        CrimeReport crimeReport = crimeReportsList.get(position);

        TextView crimeTitleTextView = convertView.findViewById(R.id.crimeTitleTextView);
        TextView crimeDescriptionTextView = convertView.findViewById(R.id.crimeDescriptionTextView);
        TextView crimeStatusTextView = convertView.findViewById(R.id.crimeStatusTextView);

        crimeTitleTextView.setText(crimeReport.getCrimeType());
        crimeDescriptionTextView.setText(crimeReport.getDescription());
        crimeStatusTextView.setText("Status: " + crimeReport.getStatus());

        return convertView;
    }
}
