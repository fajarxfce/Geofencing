package com.example.geofencing.ui.child;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.geofencing.Contstants;
import com.example.geofencing.R;
import com.example.geofencing.databinding.ActivityChildBinding;
import com.example.geofencing.model.SendNotification;
import com.example.geofencing.services.LocationService;
import com.example.geofencing.util.KmlUtil;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.List;

public class ChildActivity extends AppCompatActivity {


    ActivityChildBinding binding;
    private GoogleMap mMap;

    private int FINE_LOCATION_ACCESS_REQUEST_CODE = 10001;
    private int BACKGROUND_LOCATION_ACCESS_REQUEST_CODE = 10002;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
//            LatLng sydney = new LatLng(-34, 151);
//            googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//            googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));


            mMap = googleMap;
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

            KmlUtil kmlUtil = new KmlUtil();

            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            addPolygon(kmlUtil.parseKMLFile(R.raw.contoh, ChildActivity.this));



            enableUserLocation();


        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChildBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        SendNotification sendNotification = new SendNotification("fK4ryQCpS6O2AFit8GmVII:APA91bE4CFhHyCR_fC7LrTqXXsPiKDcfaFBhaWXHR8lzEZtFblTjexkpM2fV2D4FIOgv2Pxb_lhcQsHoKmXNqLeL7BgeL6h79XClICAIKj7D0zU31-iVcEE0Sb-rfF---nXUFAY_iCYx",
                "Location Service", "You are outside the polygon", this);
        sendNotification.sendNotification();

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }

        if (ContextCompat.checkSelfPermission(ChildActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    ChildActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    Contstants.REQUEST_CODE_LOCATION_PERMISSION
            );
        } else {
            startLocationService();
        }

    }

    private void addPolygon(List<LatLng> latLngList) {

        PolygonOptions polygonOptions = new PolygonOptions();
        polygonOptions.addAll(latLngList);
        polygonOptions.strokeColor(Color.argb(255, 255, 0, 0));
        polygonOptions.fillColor(Color.argb(64, 255, 0, 0));
        polygonOptions.strokeWidth(4);
        mMap.addPolygon(polygonOptions);
    }

    private boolean isLocationServiceRunning() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
                if (LocationService.class.getName().equals(service.service.getClassName())) {
                    if (service.foreground) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void startLocationService() {
        if (!isLocationServiceRunning()) {
            Intent intent = new Intent(ChildActivity.this, LocationService.class);
            intent.setAction(Contstants.ACTION_START_LOCATION_SERVICE);
            startService(intent);
            Toast.makeText(ChildActivity.this, "Location service started", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(ChildActivity.this, "Location service is already running", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopLocationService() {
        if (isLocationServiceRunning()) {
            Intent intent = new Intent(ChildActivity.this, LocationService.class);
            intent.setAction(Contstants.ACTION_STOP_LOCATION_SERVICE);
            startService(intent);
            Toast.makeText(ChildActivity.this, "Location service stopped", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FINE_LOCATION_ACCESS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //We have the permission
                mMap.setMyLocationEnabled(true);
            } else {
                //We do not have the permission..

            }
        }

        if (requestCode == BACKGROUND_LOCATION_ACCESS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //We have the permission
                Toast.makeText(ChildActivity.this, "You can add geofences...", Toast.LENGTH_SHORT).show();
            } else {
                //We do not have the permission..
                Toast.makeText(ChildActivity.this, "Background location access is neccessary for geofences to trigger...", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void enableUserLocation() {
        if (ContextCompat.checkSelfPermission(ChildActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            //Ask for permission
            if (ActivityCompat.shouldShowRequestPermissionRationale(ChildActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                //We need to show user a dialog for displaying why the permission is needed and then ask for the permission...
                ActivityCompat.requestPermissions(ChildActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(ChildActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
            }
        }
    }


}