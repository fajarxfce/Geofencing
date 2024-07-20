package com.example.geofencing.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.geofencing.Config;
import com.example.geofencing.Contstants;
import com.example.geofencing.R;
import com.example.geofencing.adapter.ChildLocationHistoryAdapter;
import com.example.geofencing.helper.DBHelper;
import com.example.geofencing.helper.StringHelper;
import com.example.geofencing.model.ChildCoordinat;
import com.example.geofencing.model.ChildLocationHistory;
import com.example.geofencing.model.FcmToken;
import com.example.geofencing.model.LocationHistory;
import com.example.geofencing.model.SendNotification;
import com.example.geofencing.util.AccessToken;
import com.example.geofencing.util.KmlUtil;
import com.example.geofencing.util.SharedPreferencesUtil;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.PolyUtil;

import org.apache.commons.logging.LogFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class LocationService extends Service {

    private static final String TAG = "LocationService";
    private static final org.apache.commons.logging.Log log = LogFactory.getLog(LocationService.class);
    SharedPreferencesUtil sp;
    private DatabaseReference DB, DB2;
    List<LatLng> latLngList;
    List<FcmToken> fcmTokenList = new ArrayList<>();
    private FirebaseAuth Auth;

    private LocationListener locationListener;
    private Boolean lastStatus = null;
    List<List<LatLng>> polygons = new ArrayList<>();
    List<String> areasName = new ArrayList<>();

    public interface LocationListener {
        void onLocationChanged(boolean inside, String area);
    }

    private void setLocationListener(LocationListener locationListener) {
        this.locationListener = locationListener;
    }

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);

            if (locationResult != null && locationResult.getLastLocation() != null) {
                // Get the location
                double latitude = locationResult.getLastLocation().getLatitude();
                double longitude = locationResult.getLastLocation().getLongitude();
                LatLng currentLocation = new LatLng(latitude, longitude);

                saveLocationToFirebase(latitude, longitude);

                boolean insideAnyPolygon = false;
                String area = "";
                for (int i = 0; i < polygons.size(); i++) {
                    List<LatLng> polygon = polygons.get(i);
                    area = areasName.get(i);
                    if (PolyUtil.containsLocation(currentLocation.latitude, currentLocation.longitude, polygon, true)) {
                        insideAnyPolygon = true;
                        break;
                    }
                }

                // Only call onLocationChanged if the status has changed
                if (lastStatus == null || insideAnyPolygon != lastStatus) {
                    if (locationListener != null) {
                        locationListener.onLocationChanged(insideAnyPolygon, area);
                    }
                    lastStatus = insideAnyPolygon;
                }


                // Do something with the location
                // For example, send the location to a server
            }

        }
    };

    private void getAreas(String childId) {
        Log.d(TAG, "getAreas: "+childId);
        // Get reference to the areas
        DatabaseReference areasRef = FirebaseDatabase.getInstance(Config.getDB_URL()).getReference("childs").child(childId).child("areas");
        Log.d(TAG, "getAreas: called");

        areasRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                List<String> areas = new ArrayList<>();

                int i = 0;
                for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                    i++;
                    String area = areaSnapshot.getValue(String.class);
                    Log.d(TAG, "onDataChange: "+area);
                    areas.add(area);
//                    break;
                }

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
//        String userId = sp.getPref("parent_id", this);

        for (int i = 0; i < areas.size(); i++) {
            String areaName = areas.get(i);

            Log.d(TAG, "getPolygonData: " + areaName);

            getLatLng(areaName);

        }

    }

    private void getLatLng(String areaName) {
        // Get reference to the latitude and longitude
        DatabaseReference latLngRef = FirebaseDatabase.getInstance(Config.getDB_URL()).getReference("areas")
                .child(areaName);


        Log.d(TAG, "getLatLng: " + latLngRef.toString());
        // Attach a ValueEventListener to read the data
        latLngRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                latLngList = new ArrayList<>();

                int i = 0;
                for (DataSnapshot latLngSnapshot : dataSnapshot.getChildren()) {
                    i++;
                    Double latitude = latLngSnapshot.child("latitude").getValue(Double.class);
                    Double longitude = latLngSnapshot.child("longitude").getValue(Double.class);

                    LatLng latLng = new LatLng(latitude, longitude);
                    latLngList.add(latLng);

                }

                polygons.add(latLngList);
                areasName.add(areaName);

                for (int j = 0; j < latLngList.size(); j++) {
                    Log.d(TAG, "onDataChange: getLatLng " + areaName + " " + latLngList.get(j).latitude + ", " + latLngList.get(j).longitude);
                }

//                drawPolygon(latLngList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
                Log.d(TAG, "Database error: " + databaseError.getMessage());
            }
        });
    }

    private void getFcmToken() {

        DatabaseReference DB2 = FirebaseDatabase.getInstance(Config.getDB_URL()).getReference("childs/" + Auth.getUid() + "/parent_fcm_token");
        Log.d(TAG, "getFcmToken: "+DB2);
        DB2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                fcmTokenList.clear();
                dataSnapshot.getChildren().forEach(dataSnapshot1 -> {
                    Log.d(TAG, "fetchFcm: "+dataSnapshot1.getValue(String.class));
                    fcmTokenList.add(new FcmToken(dataSnapshot1.getValue(String.class)));
                });

                for (int i = 0; i < fcmTokenList.size(); i++) {
                    Log.d(TAG, "fcmTokenList: "+fcmTokenList.get(i).getFcmToken());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Error", databaseError.getMessage());
            }
        });
    }


    @Override
    public void onCreate() {
        super.onCreate();
        sp = new SharedPreferencesUtil(this);
        DB = FirebaseDatabase.getInstance(Config.getDB_URL()).getReference();
        Auth = FirebaseAuth.getInstance();


        getAreas(Auth.getUid());
        getFcmToken();

        setLocationListener(new LocationListener() {
            @Override
            public void onLocationChanged(boolean inside, String area) {

                // Membuat objek Date untuk mendapatkan waktu saat ini
                Date now = new Date();

                // Membuat objek SimpleDateFormat untuk mengubah format Date menjadi string
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                // Mengubah Date menjadi string dengan format yang telah ditentukan
                String timestamp = formatter.format(now);

                String accessToken = AccessToken.getAccessToken();
                String name = StringHelper.usernameFromEmail(Auth.getCurrentUser().getEmail());
                String parentFcmToken = sp.getPref("parent_fcm_token", getApplicationContext());
                String body = "";
                String title = "Location Service";

                if (inside) {
                    body = "[ " + timestamp + " ]" + " : Your child " + name + " is inside the polygon " + area;
                } else {
                    body = "[ " + timestamp + " ]" + " : Your child " + name + " is outside the polygon";
                }

                for (int i = 0; i < fcmTokenList.size(); i++) {
                    SendNotification sendNotification = new SendNotification(accessToken, fcmTokenList.get(i).getFcmToken(), title, body);

                    if (inside) {
                        Log.d(TAG, "onLocationChanged test: " + name + " Inside the polygon " + area);
                        sendNotification.sendNotification();
                        saveLocationHistoryToFirebase("[ " + timestamp + " ]" + " Your child " + name + " is inside the " + area);
                    } else {
                        Log.d(TAG, "onLocationChanged test: " + name + " Outside the polygon ");
                        saveLocationHistoryToFirebase("[ " + timestamp + " ]" + " Your child " + name + " is outside the polygon");
                        sendNotification.sendNotification();
                    }
                }


            }
        });
    }

    private void saveLocationToFirebase(double latitude, double longitude) {
        DBHelper.saveCurrentLocation(
                DB,
                Auth.getUid(),
                new ChildCoordinat(latitude, longitude)
        );

    }

    private void saveLocationHistoryToFirebase(String message) {
        Log.d(TAG, "saveLocationToFirebase: " + message);
        String pairCode = sp.getPref("pair_code", this);
        String parentId = sp.getPref("parent_id", this);

        DBHelper.saveLocationHistory2(
                DB,
                pairCode,
                message);

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void startLocationService() {
        String channelId = "location_notification_channel";
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent resultIntent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelId);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("Location Service");
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        builder.setContentText("Running");
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(false);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager != null && notificationManager.getNotificationChannel(channelId) == null) {
                NotificationChannel notificationChannel = new NotificationChannel(channelId, "Location Service", NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.setDescription("This channel is used by location service");
                notificationManager.createNotificationChannel(notificationChannel);

            }

            LocationRequest locationRequest = new LocationRequest();
//            locationRequest.setInterval(10000);
//            locationRequest.setFastestInterval(20000);
            locationRequest.setInterval(4000);
            locationRequest.setFastestInterval(2000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest, locationCallback, null);
            startForeground(Contstants.LOCATION_SERVICE_ID, builder.build());
        }
    }

    private void stopLocationService() {
        LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(locationCallback);
        stopForeground(true);
        stopSelf();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if (action != null) {
                if (action.equals(Contstants.ACTION_START_LOCATION_SERVICE)) {
                    startLocationService();
//                    startLocationHistoryService();
                } else if (action.equals(Contstants.ACTION_STOP_LOCATION_SERVICE)) {
                    stopLocationService();
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }
}
