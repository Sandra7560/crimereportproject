package com.example.crime;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng selectedLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Initialize map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            Toast.makeText(this, "Map failed to load", Toast.LENGTH_SHORT).show();
        }

        // Confirm location selection
        findViewById(R.id.confirmLocationButton).setOnClickListener(v -> {
            if (selectedLocation != null) {
                // Pass selected location back to reportCrimeActivity
                Intent resultIntent = new Intent();
                String locationString = selectedLocation.latitude + "," + selectedLocation.longitude;
                resultIntent.putExtra("location", locationString);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            } else {
                Toast.makeText(this, "Please select a location on the map", Toast.LENGTH_SHORT).show();
            }
        });
    }



    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Set default map location to Montreal
        LatLng defaultLocation = new LatLng(45.5017, -73.5673); // Montreal, Canada
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 10));

        // Log when the map is ready
        Toast.makeText(this, "Map is ready and centered on Montreal", Toast.LENGTH_SHORT).show();

        // Handle map click to select a location
        mMap.setOnMapClickListener(latLng -> {
            selectedLocation = latLng;
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(latLng).title("Selected Location"));

            // Fetch the location name
            fetchLocationName(latLng);
        });
    }

    /**
     * Fetches and displays the location name using Geocoder.
     */
    private void fetchLocationName(LatLng latLng) {
        Geocoder geocoder = new Geocoder(this);
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                String locationName = addresses.get(0).getAddressLine(0);
                Toast.makeText(this, "Selected Location: " + locationName, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Unable to get location name", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error fetching location name: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }



}
