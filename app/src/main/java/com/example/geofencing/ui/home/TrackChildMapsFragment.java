package com.example.geofencing.ui.home;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.geofencing.Config;
import com.example.geofencing.R;
import com.example.geofencing.databinding.FragmentTrackChildMapsBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TrackChildMapsFragment extends Fragment {

    private static final String TAG = "TrackChildMapsFragment";
    FragmentTrackChildMapsBinding binding;
    private GoogleMap mMap;
    private DatabaseReference DB;
    Marker marker;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
//            LatLng sydney = new LatLng(-34, 151);
//            googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//            googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

            if (getArguments() != null) {
                String childId = getArguments().getString("id");
                String name = getArguments().getString("name");
                getChildLocation(childId, name);
                getAreas(childId);

            }

        }
    };

    private void getAreas(String childId) {
        // Get reference to the areas
        DatabaseReference areasRef = FirebaseDatabase.getInstance(Config.getDB_URL()).getReference("childs").child(childId).child("areas");

        // Attach a ValueEventListener to read the data
        areasRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Create a new List to hold the areas
                List<String> areas = new ArrayList<>();

                // Iterate over the children of the dataSnapshot
                for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                    // Get the area as a String and add it to the list
                    String area = areaSnapshot.getValue(String.class);
                    areas.add(area);
                }

                // Now you have a list of areas, you can use it as needed
                // For example, you could pass it to a method that updates your UI
                getPolygonData(areas);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
                Log.d(TAG, "Database error: " + databaseError.getMessage());
            }
        });
    }

    private void getPolygonData(List<String> areas) {
        // Update your UI with the areas here
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        for (int i = 0; i < areas.size(); i++) {
            String areaName = areas.get(i);

            getLatLng(userId, areaName);

        }

    }

    private void getLatLng(String userId, String areaName) {
        // Get reference to the latitude and longitude
        DatabaseReference latLngRef = FirebaseDatabase.getInstance(Config.getDB_URL()).getReference("users").child(userId).child("areas").child(areaName);


        Log.d(TAG, "getLatLng: "+latLngRef.toString());
        // Attach a ValueEventListener to read the data
        latLngRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                List<LatLng> latLngList = new ArrayList<>();

                for (DataSnapshot latLngSnapshot : dataSnapshot.getChildren()) {
                    Double latitude = latLngSnapshot.child("latitude").getValue(Double.class);
                    Double longitude = latLngSnapshot.child("longitude").getValue(Double.class);

                    LatLng latLng = new LatLng(latitude, longitude);
                    latLngList.add(latLng);
                }

                drawPolygon(latLngList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
                Log.d(TAG, "Database error: " + databaseError.getMessage());
            }
        });
    }

    private void drawPolygon(List<LatLng> points ){
//        mMap.clear();
        PolygonOptions polygon = new PolygonOptions();
        for (LatLng point : points) {
//            mMap.addMarker(new MarkerOptions().position(point));
            polygon.add(point);
        }
        polygon.fillColor(R.color.purple_700);
        mMap.addPolygon(polygon);
        for (int i = 0; i < points.size(); i++) {
            Log.d(TAG, "drawPolygon: "+points.get(i).latitude + ", " + points.get(i).longitude);
        }
    }

    private void updateUIWithLatLng(List<LatLng> latLngList) {
        // Update your UI with the LatLng list here
    }




    private void drawPolygon(LatLng point) {
        mMap.clear();
        PolygonOptions polygon = new PolygonOptions();
        mMap.addMarker(new MarkerOptions().position(point));
        polygon.add(point);

        polygon.fillColor(R.color.purple_700);
        mMap.addPolygon(polygon);
    }

    private void getChildLocation(String childId, String name) {

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Get data from db
        DB = FirebaseDatabase.getInstance(Config.getDB_URL()).getReference("users/" + uid + "/childs/" + childId);
        DB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (marker != null) {
                        marker.remove();
                    }
                    Double lat = dataSnapshot.child("latitude").getValue(Double.class);
                    Double lng = dataSnapshot.child("longitude").getValue(Double.class);


                    if (lat != null && lng != null) {
                        marker = mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(lat, lng))
                                .title(name)
                                .icon(bitmapDescriptorFromVector(getContext(), R.drawable.baseline_circle_24)));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lat, lng)));
                        mMap.moveCamera(CameraUpdateFactory.zoomTo(15.0f));

                        Log.d(TAG, "Latitude: " + lat + ", Longitude: " + lng);
                    } else {
                        Log.d(TAG, "Latitude or Longitude is null");
                    }
                } else {
                    Log.d(TAG, "Child does not exist");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "Database error: " + databaseError.getMessage());
            }
        });

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentTrackChildMapsBinding.inflate(inflater, container, false);
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

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}