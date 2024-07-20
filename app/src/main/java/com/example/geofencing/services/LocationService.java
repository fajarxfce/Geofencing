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

import com.example.geofencing.Config;
import com.example.geofencing.Contstants;
import com.example.geofencing.R;
import com.example.geofencing.helper.DBHelper;
import com.example.geofencing.helper.StringHelper;
import com.example.geofencing.model.ChildCoordinat;
import com.example.geofencing.model.FcmToken;
import com.example.geofencing.model.SendNotification;
import com.example.geofencing.util.AccessToken;
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
    List<List<LatLng>> polygonList = new ArrayList<>();
    List<String> polygonNameList = new ArrayList<>();

    public interface LocationListener {
        void onLocationChanged(boolean inside, String name);
    }

    private void setLocationListener(LocationListener locationListener) {
        this.locationListener = locationListener;
    }

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);

            if (locationResult != null && locationResult.getLastLocation() != null) {
                double latitude = locationResult.getLastLocation().getLatitude();
                double longitude = locationResult.getLastLocation().getLongitude();
                LatLng currentLocation = new LatLng(latitude, longitude);

                saveLocationToFirebase(latitude, longitude);

                boolean insideAnyPolygon = false;
                String polygonName = "";
                for (int i = 0; i < polygonList.size(); i++) {
                    List<LatLng> polygon = polygonList.get(i);
                    polygonName = polygonNameList.get(i);
                    if (PolyUtil.containsLocation(currentLocation.latitude, currentLocation.longitude, polygon, true)) {
                        insideAnyPolygon = true;
                        break;
                    }
                }

                if (lastStatus == null || insideAnyPolygon != lastStatus) {
                    if (locationListener != null) {
                        locationListener.onLocationChanged(insideAnyPolygon, polygonName);
                    }
                    lastStatus = insideAnyPolygon;
                }
            }

        }
    };

    private void getPolygons(String childId) {
        DatabaseReference polygonRef = FirebaseDatabase.getInstance(Config.getDB_URL()).getReference("childs").child(childId).child("polygons");

        polygonRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                List<String> polygonList = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String value = snapshot.getValue(String.class);
                    Log.d(TAG, "onDataChange: "+value);
                    polygonList.add(value);
                }

                getPolygonData(polygonList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getPolygonData(List<String> polygonList) {
        for (int i = 0; i < polygonList.size(); i++) {
            String polygon = polygonList.get(i);
            getLatLng(polygon);

        }

    }

    private void getLatLng(String polygonName) {
        DatabaseReference latLngRef = FirebaseDatabase.getInstance(Config.getDB_URL()).getReference("polygons")
                .child(polygonName);

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

                polygonList.add(latLngList);
                LocationService.this.polygonNameList.add(polygonName);

                for (int j = 0; j < latLngList.size(); j++) {
                    Log.d(TAG, "onDataChange: getLatLng " + polygonName + " " + latLngList.get(j).latitude + ", " + latLngList.get(j).longitude);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void getFcmToken() {

        DatabaseReference DB2 = FirebaseDatabase.getInstance(Config.getDB_URL()).getReference("childs/" + Auth.getUid() + "/parent_fcm_token");
        DB2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                fcmTokenList.clear();
                dataSnapshot.getChildren().forEach(dataSnapshot1 -> {
                    fcmTokenList.add(new FcmToken(dataSnapshot1.getValue(String.class)));
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    @Override
    public void onCreate() {
        super.onCreate();
        sp = new SharedPreferencesUtil(this);
        DB = FirebaseDatabase.getInstance(Config.getDB_URL()).getReference();
        Auth = FirebaseAuth.getInstance();

        getPolygons(Auth.getUid());
        getFcmToken();

        setLocationListener(new LocationListener() {
            @Override
            public void onLocationChanged(boolean inside, String polygonName) {

                //format date
                Date now = new Date();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String timestamp = formatter.format(now);

                String accessToken = AccessToken.getAccessToken();
                String name = StringHelper.usernameFromEmail(Auth.getCurrentUser().getEmail());
                String body = "";
                String title = "Location Service";

                if (inside) {
                    body = "[ " + timestamp + " ]" + " : Your child " + name + " is inside the polygon " + polygonName;
                } else {
                    body = "[ " + timestamp + " ]" + " : Your child " + name + " is outside the polygon";
                }

                for (int i = 0; i < fcmTokenList.size(); i++) {
                    SendNotification sendNotification = new SendNotification(accessToken, fcmTokenList.get(i).getFcmToken(), title, body);

                    if (inside) {
                        Log.d(TAG, "onLocationChanged test: " + name + " Inside the polygon " + polygonName);
                        sendNotification.sendNotification();
                        saveLocationHistoryToFirebase("[ " + timestamp + " ]" + " Your child " + name + " is inside the " + polygonName);
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
        String pairCode = sp.getPref("pair_code", this);

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
                } else if (action.equals(Contstants.ACTION_STOP_LOCATION_SERVICE)) {
                    stopLocationService();
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }
}
