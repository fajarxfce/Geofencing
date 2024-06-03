package com.example.geofencing.helper;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.geofencing.Config;
import com.example.geofencing.model.ChildCoordinat;
import com.example.geofencing.model.ChildFirebase;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.example.geofencing.model.User;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class DBHelper {

    private static final String TAG = "DBHelper";
    private static String childName, childParentId, childPairKey;

    public static void saveUser(DatabaseReference DB, String userId, String name, String email) {
        User user = new User(name, email);

        DB.child("users")
                .child(userId)
                .setValue(user);
    }

    public static void saveCurrentLocation(DatabaseReference DB, String pairCode, ChildCoordinat coordinat, String parentId) {

        Map<String, Object> updates = new HashMap<>();
        updates.put("latitude", coordinat.getLatitude());
        updates.put("longitude", coordinat.getLongitude());

        DB.child("users")
                .child(parentId)
                .child("childs")
                .child(pairCode)
                .updateChildren(updates);
        Log.d(TAG, "saveCurrentLocation: "+DB.child("childs").child(pairCode).toString());
        Log.d(TAG, "saveCurrentLocation: "+coordinat.getLatitude()+" "+coordinat.getLongitude());
    }

    public static void saveParentToken(DatabaseReference DB, String parentId, String fcmToken) {

        Map<String, Object> updates = new HashMap<>();
        updates.put("fcm_token", fcmToken);

        DB.child("users")
                .child(parentId)
                .updateChildren(updates);
    }

    public static void saveChild(DatabaseReference DB, String parentId, String name) {
        childName = name;
        childParentId = parentId;

        Random rnd = new Random();
        int number = rnd.nextInt(999999);

        String pairkey = String.format("%06d", number);

        childPairKey = pairkey;

        // Check if pair key exist
        DatabaseReference DB2 = DB;
        DB2 = FirebaseDatabase.getInstance(Config.getDB_URL()).getReference("childs/" + pairkey);

        DB2.addListenerForSingleValueEvent(new ValueEventListener()  {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // If exist generate new key
                    Random rnd = new Random();
                    int number = rnd.nextInt(999999);
                    String pairkey = String.format("%06d", number);

                    childPairKey = pairkey;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //
            }
        });

        ChildFirebase child = new ChildFirebase(childParentId, childName, childName);

        DB.child("users")
                .child(childParentId)
                .child("childs")
                .child(childPairKey)
                .setValue(child);

        DB.child("childs")
                .child(childPairKey)
                .setValue(child);
    }

    public static void deleteChild(DatabaseReference DB, String parentId, String id) {
        DB.child("users")
                .child(parentId)
                .child("childs")
                .child(id)
                .removeValue();

        DB.child("childs")
                .child(id)
                .removeValue();
    }

    public static void saveArea(DatabaseReference DB, String parentId, String name, List<LatLng> points) {
        for (int i = 0; i < points.size(); i++) {
            DB.child("users")
                    .child(parentId)
                    .child("areas")
                    .child(name)
                    .child(String.valueOf(i))
                    .setValue(points.get(i));
        }
    }

    public static void deleteArea(DatabaseReference DB, String parentId, String id) {
        DB.child("users")
                .child(parentId)
                .child("areas")
                .child(id)
                .removeValue();
    }
}