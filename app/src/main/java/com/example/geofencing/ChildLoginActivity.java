package com.example.geofencing;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.geofencing.auth.LoginActivity;
import com.example.geofencing.auth.RegisterChildActivity;
import com.example.geofencing.databinding.ActivityChildLoginBinding;
import com.example.geofencing.helper.StringHelper;
import com.example.geofencing.model.ChildData;
import com.example.geofencing.ui.child.ChildActivity;
import com.example.geofencing.util.SharedPreferencesUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    private FirebaseAuth Auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChildLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        DB = FirebaseDatabase.getInstance(Config.getDB_URL()).getReference();
        Auth = FirebaseAuth.getInstance();

        sf = new SharedPreferencesUtil(ChildLoginActivity.this);

        binding.login.setOnClickListener(v -> {
            if (!validateForm()) return;
            login();
        });

        binding.register.setOnClickListener(v -> {
            Intent intent = new Intent(ChildLoginActivity.this, RegisterChildActivity.class);
            startActivity(intent);
        });

    }

    private void login() {
        if (!validateForm()) return;

        String email = binding.txtEmail.getText().toString();
        String password = binding.txtPassword.getText().toString();

        Auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        onAuthSuccess(task.getResult().getUser());
                        sf.setPref("pair_code", Auth.getUid(), ChildLoginActivity.this);
                    } else {
                        Toast.makeText(this, "Sign In Failed",
                                Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void onAuthSuccess(FirebaseUser user) {
        // Create User If Not Exist
//        DBHelper.saveUser(DB, user.getUid(), name, user.getEmail());
        sf.setPref("account_type", "child", ChildLoginActivity.this);

        // Make alert
        Toast.makeText(this, "Sign In Success !",
                Toast.LENGTH_SHORT).show();

        // Move to Main Activity
        startActivity(new Intent(ChildLoginActivity.this, ChildActivity.class));
        finish();
    }

    private boolean validateForm() {
        boolean result = true;
        if (TextUtils.isEmpty(binding.txtEmail.getText().toString())) {
            binding.txtEmail.setError("Required");
            result = false;
        } else {
            binding.txtEmail.setError(null);
        }

        if (TextUtils.isEmpty(binding.txtPassword.getText().toString())) {
            binding.txtPassword.setError("Required");
            result = false;
        } else {
            binding.txtPassword.setError(null);
        }

        // Min 6
        if (binding.txtPassword.getText().toString().length() < 6) {
            Toast.makeText(this, "Password min 6 character",
                    Toast.LENGTH_SHORT).show();
        }

        // Must contain @
        if (!binding.txtEmail.getText().toString().contains("@")) {
            Toast.makeText(this, "Email does not comply with the conditions",
                    Toast.LENGTH_SHORT).show();
        }

        return result;
    }

}