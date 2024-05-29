package com.example.geofencing.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.geofencing.MainActivity;
import com.example.geofencing.auth.LoginActivity;
import com.example.geofencing.databinding.FragmentUserBinding;
import com.google.firebase.auth.FirebaseAuth;

public class UserFragment extends Fragment {

    private FragmentUserBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentUserBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnLogout.setOnClickListener(v -> logout());
        getProfile();


    }

    private void getProfile() {
        // Get user profile
        // Name, email address, and profile photo Url
        String name = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        // Check if user's email is verified
        boolean emailVerified = FirebaseAuth.getInstance().getCurrentUser().isEmailVerified();

        // The user's ID, unique to the Firebase project. Do NOT use this value to
        // authenticate with your backend server, if you have one. Use
        // FirebaseUser.getIdToken() instead.
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        binding.txtUser.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
    }

    private void logout() {
        // Logout
        FirebaseAuth.getInstance().signOut();

        // Back to login page
        Toast.makeText(requireContext(), "Already logged out", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}