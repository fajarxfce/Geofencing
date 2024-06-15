package com.example.geofencing;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.geofencing.databinding.ActivityChildLoginBinding;
import com.example.geofencing.model.ChildData;
import com.example.geofencing.ui.child.ChildActivity;
import com.example.geofencing.util.SharedPreferencesUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChildLoginActivity extends AppCompatActivity {

    private static final String TAG = "ChildLoginActivity";
    SharedPreferencesUtil sf;
    private DatabaseReference DB;
    ActivityChildLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChildLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sf = new SharedPreferencesUtil(ChildLoginActivity.this);

        binding.login.setOnClickListener(v -> {
            validatePairCode();
        });

    }

    private void validatePairCode() {
        String pairCode = binding.pairCode.getText().toString().trim();
        if (pairCode.isEmpty()) {
            binding.pairCode.setError("Pair code is required");
            return;
        }

        DB = FirebaseDatabase.getInstance(Config.getDB_URL()).getReference("childs/" + pairCode);

        DB.addListenerForSingleValueEvent(new ValueEventListener()  {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    ChildData childData = snapshot.getValue(ChildData.class);
                    // Execute
                    Toast.makeText(ChildLoginActivity.this, "Pair code is valid", Toast.LENGTH_SHORT).show();

                    if (childData != null) {
                        Log.d(TAG, "onDataChange: "+childData.getName().toString());
                        Log.d(TAG, "onDataChange: "+childData.getPairKey().toString());
                        Log.d(TAG, "onDataChange: "+childData.getParentId().toString());
                        sf.setPref("pair_code", pairCode, ChildLoginActivity.this);
                        sf.setPref("name", childData.getName().toString(), ChildLoginActivity.this);
                        sf.setPref("pair_key", childData.getPairKey().toString(), ChildLoginActivity.this);
                        sf.setPref("parent_id", childData.getParentId().toString(), ChildLoginActivity.this);

                        Intent intent = new Intent(ChildLoginActivity.this, ChildActivity.class);
                        startActivity(intent);
                    }

                }else {
                    Toast.makeText(ChildLoginActivity.this, "Pair code is invalid", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}