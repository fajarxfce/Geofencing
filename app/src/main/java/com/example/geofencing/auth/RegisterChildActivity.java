package com.example.geofencing.auth;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.geofencing.Config;
import com.example.geofencing.R;
import com.example.geofencing.databinding.ActivityRegisterChildBinding;
import com.example.geofencing.helper.DBHelper;
import com.example.geofencing.helper.StringHelper;
import com.example.geofencing.model.ChildPairCode;
import com.example.geofencing.model.UserChild;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

public class RegisterChildActivity extends AppCompatActivity {

    ActivityRegisterChildBinding binding;
    private DatabaseReference DB;
    private FirebaseAuth Auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterChildBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        DB = FirebaseDatabase.getInstance(Config.getDB_URL()).getReference();
        Auth = FirebaseAuth.getInstance();

        binding.login.setOnClickListener(v -> {
            if (!validateForm()) return;
            String email = binding.txtEmail.getText().toString();
            String password = binding.txtPassword.getText().toString();

            signUp(email, password);
        });

    }

    private void signUp(String email, String password){
        Auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if(task.isSuccessful()) {
                        onAuthSuccess(task.getResult().getUser());

                    } else {
                        Toast.makeText(RegisterChildActivity.this, "Sign Up Failed",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void onAuthSuccess(FirebaseUser user) {
        String name = StringHelper.usernameFromEmail(user.getEmail());

        int pairCode = generatePairCode();

        String pairkey = String.format("%06d", pairCode);
        ChildPairCode userChild = new ChildPairCode(name, user.getEmail(), user.getUid());

        // Create User If Not Exist
        DBHelper.saveUserChild(DB, user.getUid(), name, user.getEmail(), pairkey);
        DBHelper.saveChildCode(DB, pairkey, userChild);

        // Make alert
        Toast.makeText(this, "Daftar berhasil ! Silakan login",
                Toast.LENGTH_SHORT).show();
        Auth.signOut();
        finish();
    }

    private int generatePairCode(){
        Random rnd = new Random();
        int number = rnd.nextInt(999999);
        return number;
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
        if(binding.txtPassword.getText().toString().length() < 6) {
            Toast.makeText(RegisterChildActivity.this, "Password min 6 character",
                    Toast.LENGTH_SHORT).show();
        }

        // Must contain @
        if(!binding.txtEmail.getText().toString().contains("@")) {
            Toast.makeText(RegisterChildActivity.this, "Email does not comply with the conditions",
                    Toast.LENGTH_SHORT).show();
        }

        return result;
    }
}