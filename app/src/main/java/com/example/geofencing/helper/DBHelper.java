package com.example.geofencing.helper;

import androidx.annotation.NonNull;

import com.example.geofencing.Config;
import com.example.geofencing.model.ChildFirebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.example.geofencing.model.User;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class DBHelper {

    private static String childName, childParentId, childPairKey;

    public static void saveUser(DatabaseReference DB, String userId, String name, String email) {
        User user = new User(name, email);

        DB.child("users")
                .child(userId)
                .setValue(user);
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
}