package com.example.geofencing.ui.dashboard;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.geofencing.Config;
import com.example.geofencing.Contstants;
import com.example.geofencing.R;
import com.example.geofencing.databinding.FragmentMapsBinding;
import com.example.geofencing.helper.DBHelper;
import com.example.geofencing.services.LocationService;
import com.example.geofencing.util.KmlUtil;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MapsFragment extends Fragment {

    private static final String TAG = "MapsFragment";
    private GoogleMap mMap;
    FragmentMapsBinding binding;
    private int FINE_LOCATION_ACCESS_REQUEST_CODE = 10001;
    private int BACKGROUND_LOCATION_ACCESS_REQUEST_CODE = 10002;
    private List<LatLng> points = new ArrayList<>();

    private FusedLocationProviderClient fusedLocationProviderClient;

    private Location currentLocation;
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    private DatabaseReference DB;

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
            DatabaseReference pointsRef = database.getReference("points");
            mMap = googleMap;
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
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
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DB = FirebaseDatabase.getInstance(Config.getDB_URL()).getReference();

                DBHelper.saveArea(DB, uid, "area" + new Random().nextInt(9999), points);

                Toast.makeText(getActivity(), "Area saved", Toast.LENGTH_SHORT).show();

                // Move to List Area fragment
                Navigation.findNavController(v).navigate(R.id.action_addAreaFragment_to_navigation_dashboard);
            });

            enableUserLocation();
        }
    };

    private void drawPolygon(){
        mMap.clear();
        PolygonOptions polygon = new PolygonOptions();
        for (LatLng point : points) {
            mMap.addMarker(new MarkerOptions().position(point));
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        getLastLocation();

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

    private void getLastLocation() {
        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            Task<Location> task = fusedLocationProviderClient.getLastLocation();
            task.addOnSuccessListener(location -> {
                if (location != null) {
                    currentLocation = location;
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
                    mMap.moveCamera(CameraUpdateFactory.zoomTo(15.0f));
                    Toast.makeText(getContext(), "Lat: " + location.getLatitude() + " Lng: " + location.getLongitude(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

//    @Override
//    public void onMapLongClick(LatLng latLng) {
//
//    }
}