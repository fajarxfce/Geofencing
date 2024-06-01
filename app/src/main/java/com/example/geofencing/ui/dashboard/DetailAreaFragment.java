package com.example.geofencing.ui.dashboard;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.geofencing.Config;
import com.example.geofencing.Contstants;
import com.example.geofencing.R;
import com.example.geofencing.databinding.FragmentDetailAreaBinding;
import com.example.geofencing.model.Child;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.List;

public class DetailAreaFragment extends Fragment {

    private static final String TAG = "DetailAreaFragment";
    private GoogleMap mMap;
    FragmentDetailAreaBinding binding;
    private int FINE_LOCATION_ACCESS_REQUEST_CODE = 10001;
    private int BACKGROUND_LOCATION_ACCESS_REQUEST_CODE = 10002;
    private List<LatLng> points = new ArrayList<>();

    private String id;

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
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            // Get data from db
            DB = FirebaseDatabase.getInstance(Config.getDB_URL()).getReference("users/" + uid + "/areas/" + id);

            DB.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot areaSnapshot: dataSnapshot.getChildren()) {
                        LatLng point = new LatLng(
                                areaSnapshot.child("latitude").getValue(Double.class),
                                areaSnapshot.child("longitude").getValue(Double.class)
                        );

                        mMap.moveCamera(CameraUpdateFactory.newLatLng(point));
                        points.add(point);

                        mMap.moveCamera(CameraUpdateFactory.zoomTo(15.0f));
                        drawPolygon();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("Error", databaseError.getMessage());
                }
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
        binding = FragmentDetailAreaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        id = getArguments().getString("id");

        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

}