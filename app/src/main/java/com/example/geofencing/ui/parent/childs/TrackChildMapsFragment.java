package com.example.geofencing.ui.parent.childs;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
    List<LatLng> latLngList;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;

            if (getArguments() != null) {
                String childId = getArguments().getString("id");
                String name = getArguments().getString("name");
                getChildLocation(childId, name);
                getPolygons(childId);

            }

        }
    };

    private void getPolygons(String childId) {
        DatabaseReference polygonRef = FirebaseDatabase.getInstance(Config.getDB_URL()).getReference("childs").child(childId).child("polygons");

        polygonRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> poligonList = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String polygonName = snapshot.getValue(String.class);
                    poligonList.add(polygonName);
                }

                getPolygonData(poligonList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "Database error: " + databaseError.getMessage());
            }
        });
    }

    private void getPolygonData(List<String> polygonList) {

        for (int i = 0; i < polygonList.size(); i++) {
            String polygonName = polygonList.get(i);

            getLatLng(polygonName);

        }

    }

    private void getLatLng(String polygonName) {
        // Get reference to the latitude and longitude
        DatabaseReference latLngRef = FirebaseDatabase.getInstance(Config.getDB_URL()).getReference("polygons").child(polygonName);

        latLngRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                latLngList = new ArrayList<>();

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

    private void drawPolygon(List<LatLng> points) {
        PolygonOptions polygon = new PolygonOptions();
        for (LatLng point : points) {
            polygon.add(point);
        }
        polygon.fillColor(R.color.red_transparent);
        polygon.strokeColor(Color.RED);
        mMap.addPolygon(polygon);
    }

    private void getChildLocation(String childId, String name) {

        DB = FirebaseDatabase.getInstance(Config.getDB_URL()).getReference("childs/" + childId);
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

                    } else {
                        Toast.makeText(getContext(), "Lokasi tidak ada!", Toast.LENGTH_SHORT).show();
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