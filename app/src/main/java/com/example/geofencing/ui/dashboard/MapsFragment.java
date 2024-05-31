package com.example.geofencing.ui.dashboard;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.geofencing.Contstants;
import com.example.geofencing.R;
import com.example.geofencing.databinding.FragmentMapsBinding;
import com.example.geofencing.services.LocationService;
import com.example.geofencing.util.KmlUtil;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.List;

public class MapsFragment extends Fragment {

    private static final String TAG = "MapsFragment";
    private GoogleMap mMap;
    FragmentMapsBinding binding;
    private int FINE_LOCATION_ACCESS_REQUEST_CODE = 10001;
    private int BACKGROUND_LOCATION_ACCESS_REQUEST_CODE = 10002;
    private List<LatLng> points = new ArrayList<>();
    FirebaseDatabase database = FirebaseDatabase.getInstance();

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

            DatabaseReference pointsRef = database.getReference("points");
            mMap = googleMap;
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

            KmlUtil kmlUtil = new KmlUtil();

            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            addPolygon(kmlUtil.parseKMLFile(R.raw.contoh, getContext()));
            mMap.setOnMapClickListener(latLng -> {
                binding.fabDelete.setVisibility(View.VISIBLE);
                binding.ibSave.setVisibility(View.VISIBLE);
                points.add(latLng);
                drawPolygon();
            });

            binding.fabDelete.setOnClickListener(v -> {
                if (points.size() <= 1) {
                    points.clear();
                    mMap.clear();
                    binding.fabDelete.setVisibility(View.GONE);
                    binding.ibSave.setVisibility(View.GONE);
                } else {
                    points.remove(points.size() - 1);
                    drawPolygon();
                }
            });

            binding.ibSave.setOnClickListener(v -> {


            });

            enableUserLocation();

        }
    };

    private void drawPolygon(){
        mMap.clear();
        PolygonOptions polygon = new PolygonOptions();
        for (LatLng point : points) {
//            mMap.addMarker(new MarkerOptions().position(point));
            polygon.add(point);
        }
        binding.squareFeet.setText("Area: " + SphericalUtil.computeArea(points));
        polygon.fillColor(R.color.purple_700);
        mMap.addPolygon(polygon);
        for (int i = 0; i < points.size(); i++) {
            Log.d(TAG, "drawPolygon: "+points.get(i).latitude + ", " + points.get(i).longitude);
        }
    }



    private boolean isLocationServiceRunning() {
        ActivityManager activityManager = (ActivityManager) requireActivity().getSystemService(Context.ACTIVITY_SERVICE);
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
            Intent intent = new Intent(getActivity(), LocationService.class);
            intent.setAction(Contstants.ACTION_START_LOCATION_SERVICE);
            requireActivity().startService(intent);
            Toast.makeText(getContext(), "Location service started", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopLocationService() {
        if (isLocationServiceRunning()) {
            Intent intent = new Intent(getContext(), LocationService.class);
            intent.setAction(Contstants.ACTION_STOP_LOCATION_SERVICE);
            requireActivity().startService(intent);
            Toast.makeText(getContext(), "Location service stopped", Toast.LENGTH_SHORT).show();
        }
    }

    private void enableUserLocation() {
        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            //Ask for permission
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                //We need to show user a dialog for displaying why the permission is needed and then ask for the permission...
                ActivityCompat.requestPermissions(getActivity(), new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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
                Toast.makeText(getContext(), "You can add geofences...", Toast.LENGTH_SHORT).show();
            } else {
                //We do not have the permission..
                Toast.makeText(getContext(), "Background location access is neccessary for geofences to trigger...", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMapsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    private void addPolygon(List<LatLng> latLngList) {

        PolygonOptions polygonOptions = new PolygonOptions();
        polygonOptions.addAll(latLngList);
        polygonOptions.strokeColor(Color.argb(255, 255, 0,0));
        polygonOptions.fillColor(Color.argb(64, 255, 0,0));
        polygonOptions.strokeWidth(4);
        mMap.addPolygon(polygonOptions);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }

        binding.btnStartLocationUpdate.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        getActivity(),
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        Contstants.REQUEST_CODE_LOCATION_PERMISSION
                );
            }else {
                startLocationService();
            }
        });

        binding.btnStropLocationUpdate.setOnClickListener(v -> {
            stopLocationService();
        });
    }

//    @Override
//    public void onMapLongClick(LatLng latLng) {
//
//    }
}