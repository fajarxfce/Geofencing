package com.example.geofencing.ui.dashboard;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.geofencing.Contstants;
import com.example.geofencing.R;
import com.example.geofencing.databinding.FragmentPolygonMapsBinding;
import com.example.geofencing.dialog.EnterAreaNameDialog;
import com.example.geofencing.services.LocationService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.List;

public class PolygonMapsFragment extends Fragment {

    private static final String TAG = "PolygonMapsFragment";
    private GoogleMap mMap;
    FragmentPolygonMapsBinding binding;
    private int FINE_LOCATION_ACCESS_REQUEST_CODE = 10001;
    private int BACKGROUND_LOCATION_ACCESS_REQUEST_CODE = 10002;
    private List<LatLng> points = new ArrayList<>();

    private FusedLocationProviderClient fusedLocationProviderClient;

    private Location currentLocation;


    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
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
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("points", (ArrayList<? extends Parcelable>) points);
                EnterAreaNameDialog dialog = new EnterAreaNameDialog(v);
                dialog.setArguments(bundle);
                dialog.show(getParentFragmentManager(), "EnterAreaNameDialog");
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
        polygon.fillColor(R.color.purple_700);
        mMap.addPolygon(polygon);

    }

    private void enableUserLocation() {
        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            getLastLocation();
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
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FINE_LOCATION_ACCESS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableUserLocation();
                Toast.makeText(requireContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentPolygonMapsBinding.inflate(inflater, container, false);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    private void getLastLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            Task<Location> task = fusedLocationProviderClient.getLastLocation();
            task.addOnSuccessListener(location -> {
                if (location != null) {
                    currentLocation = location;
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
                    mMap.moveCamera(CameraUpdateFactory.zoomTo(15.0f));
                }
            });
        }
    }
}