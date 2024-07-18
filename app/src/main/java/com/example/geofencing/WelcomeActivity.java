package com.example.geofencing;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.geofencing.auth.LoginActivity;
import com.example.geofencing.databinding.ActivityWelcomeBinding;
import com.example.geofencing.ui.child.ChildActivity;
import com.example.geofencing.util.SharedPreferencesUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class WelcomeActivity extends AppCompatActivity {

    private static final Log log = LogFactory.getLog(WelcomeActivity.class);
    private static final String TAG = "WelcomeActivity";
    ActivityWelcomeBinding binding;
    SharedPreferencesUtil sf;
    private FirebaseAuth Auth;
    private DatabaseReference DB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWelcomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        DB = FirebaseDatabase.getInstance(Config.getDB_URL()).getReference();
        Auth = FirebaseAuth.getInstance();
        sf = new SharedPreferencesUtil(WelcomeActivity.this);
        android.util.Log.d(TAG, "account_type: "+sf.getPref("account_type", WelcomeActivity.this));

        // Check if user is logged in
        if (Auth.getCurrentUser() != null) {

            if (sf.getPref("account_type", WelcomeActivity.this) != null){
                if (sf.getPref("account_type", WelcomeActivity.this).equals("parent")){
                    Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else if (sf.getPref("account_type", WelcomeActivity.this).equals("child")){
                    Intent intent = new Intent(WelcomeActivity.this, ChildActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }

//            Toast.makeText(WelcomeActivity.this, "Already logged in",
//                    Toast.LENGTH_SHORT).show();

//            Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            startActivity(intent);
//            finish();
        }

        binding.btnLoginAsParent.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        binding.btnLoginAsChild.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, ChildLoginActivity.class);
            startActivity(intent);
        });

    }
}