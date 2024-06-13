package com.example.geofencing.dialog;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.geofencing.Config;
import com.example.geofencing.auth.LoginActivity;
import com.example.geofencing.databinding.DialogEnterPairCodeBinding;
import com.example.geofencing.databinding.DialogForgotPasswordBinding;
import com.example.geofencing.model.ChildData;
import com.example.geofencing.ui.child.ChildActivity;
import com.example.geofencing.util.SharedPreferencesUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ForgotPasswordDialog extends DialogFragment {

    private static final String TAG = "EnterPairCodeDialog";
    DialogForgotPasswordBinding binding;

    SharedPreferencesUtil sf;
    private DatabaseReference DB;
    private FirebaseAuth Auth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DialogForgotPasswordBinding.inflate(inflater, container, false);

        binding.btnSubmit.setOnClickListener(v -> { validatePairCode(); });
        sf = new SharedPreferencesUtil(requireContext());
        Auth = FirebaseAuth.getInstance();

        return binding.getRoot();
    }

    private void validatePairCode() {
        String email = binding.txtEmail.getText().toString().trim();
        if (email.isEmpty()) {
            binding.txtEmail.setError("Masukkan email!");
            return;
        }

        Log.d(TAG, "validatePairCode: "+email);

        Auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(requireContext(), "Email terkirim, silahkan cek email anda",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(), "Gagal mengirim email, silahkan coba lagi!",
                                Toast.LENGTH_SHORT).show();
                    }
                });


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        return dialog;
    }

}