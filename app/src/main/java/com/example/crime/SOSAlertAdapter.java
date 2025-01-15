package com.example.crime;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SOSAlertAdapter extends RecyclerView.Adapter<SOSAlertAdapter.SOSAlertViewHolder> {
    private List<SOSAlert> sosAlertList;

    public SOSAlertAdapter(List<SOSAlert> sosAlertList) {
        this.sosAlertList = sosAlertList;
    }

    @NonNull
    @Override
    public SOSAlertViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sos_alert, parent, false);
        return new SOSAlertViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SOSAlertViewHolder holder, int position) {
        SOSAlert sosAlert = sosAlertList.get(position);
        Log.d("SOS_ALERT_ADAPTER", "Binding SOS Alert: " + sosAlert.toString());
        holder.messageTextView.setText(sosAlert.getEmergencyMessage());
        holder.locationTextView.setText(sosAlert.getLocation());
        holder.timeTextView.setText(formatTimestamp(sosAlert.getTimestamp()));
    }

    @Override
    public int getItemCount() {
        return sosAlertList.size();
    }

    // Method to update the list of SOS Alerts
    public void setList(List<SOSAlert> newSosAlertList) {
        this.sosAlertList = new ArrayList<>(newSosAlertList);
        notifyDataSetChanged();  // Refresh the entire list
    }

    // Method to add a single SOS Alert
    public void addSOSAlert(SOSAlert sosAlert) {
        this.sosAlertList.add(sosAlert);
        notifyItemInserted(sosAlertList.size() - 1);  // Notify that a new item is added
    }

    // Method to update a specific SOS Alert at a particular position
    public void updateSOSAlert(int position, SOSAlert sosAlert) {
        this.sosAlertList.set(position, sosAlert);
        notifyItemChanged(position);  // Notify that a specific item has changed
    }

    // Method to remove a SOS Alert at a specific position
    public void removeSOSAlert(int position) {
        this.sosAlertList.remove(position);
        notifyItemRemoved(position);  // Notify that a specific item has been removed
    }

    private String formatTimestamp(long timestamp) {
        // Format timestamp as "dd-MM-yyyy HH:mm:ss"
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        Date date = new Date(timestamp);
        return sdf.format(date);
    }

    public static class SOSAlertViewHolder extends RecyclerView.ViewHolder {
        TextView locationTextView, messageTextView, timeTextView;

        public SOSAlertViewHolder(@NonNull View itemView) {
            super(itemView);
            locationTextView = itemView.findViewById(R.id.locationTextView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
        }
    }
}
