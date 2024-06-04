package com.example.geofencing.ui.map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

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
import com.example.geofencing.adapter.ChildAdapter;
import com.example.geofencing.dialog.ChildCodeDialog;
import com.example.geofencing.dialog.DeleteChildDialog;
import com.example.geofencing.model.Child;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.List;

public class MapsFragment extends Fragment {
    private static final String TAG = "MapsFragment";
    private DatabaseReference DB;
    private GoogleMap mMap;

    private List<LatLng> points = new ArrayList<>();

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
            mMap = googleMap;
            getAllChild();
            getAllPolygonData();
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
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

    private void drawPolygon(){
        mMap.clear();
        PolygonOptions polygon = new PolygonOptions();
        for (LatLng point : points) {
            mMap.addMarker(new MarkerOptions().position(point));
            polygon.add(point);
        }
        polygon.fillColor(R.color.purple_700);
        mMap.addPolygon(polygon);
        for (int i = 0; i < points.size(); i++) {
            Log.d(TAG, "drawPolygon: "+points.get(i).latitude + ", " + points.get(i).longitude);
        }
    }

    private void getAllPolygonData() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Get data from db
        DB = FirebaseDatabase.getInstance(Config.getDB_URL()).getReference("users/" + uid + "/areas");

        DB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DatabaseReference DB2;
                int i = 0;
                for (DataSnapshot areaSnapshot: dataSnapshot.getChildren()) {
                    i++;

                    areaSnapshot.getKey();
                    DB2 = FirebaseDatabase.getInstance(Config.getDB_URL()).getReference("users/" + uid + "/areas/" + areaSnapshot.getKey());

                    DB2.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            
                            for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
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

                        public void onCancelled(DatabaseError databaseError) {
                            Log.d("Error", databaseError.getMessage());
                        }
                    });

                    if(i == 1) {
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Error", databaseError.getMessage());
            }
        });
    }

    private void getAllChild() {

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Get data from db
        DB = FirebaseDatabase.getInstance(Config.getDB_URL()).getReference("users/" + uid + "/childs");
        DB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Child> childList = new ArrayList<>();
                mMap.clear();

                int i = 0;
                for (DataSnapshot clidSnapshot: dataSnapshot.getChildren()) {
                    i++;

                    Double lat = clidSnapshot.child("latitude").getValue(Double.class);
                    Double lng = clidSnapshot.child("longitude").getValue(Double.class);

                    if (lat != null || lng != null) {
                        Log.d(TAG, "onDataChange: have lat lng" + i + " " + clidSnapshot.getKey() + " " + clidSnapshot.child("name").getValue(String.class) + " " + lat + " " + lng);
                        mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(lat, lng))
                                .title(clidSnapshot.child("name").getValue(String.class))
                                .icon(bitmapDescriptorFromVector(getContext(), R.drawable.baseline_circle_24)));
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Error", databaseError.getMessage());
            }
        });
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