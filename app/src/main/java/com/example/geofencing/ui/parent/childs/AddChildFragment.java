package com.example.geofencing.ui.parent.childs;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.geofencing.Config;
import com.example.geofencing.R;
import com.example.geofencing.databinding.FragmentAddChildBinding;
import com.example.geofencing.helper.DBHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddChildFragment extends Fragment {

    FragmentAddChildBinding binding;

    private DatabaseReference DB;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAddChildBinding.inflate(inflater, container, false);

        // Create instance firebase
        this.DB = FirebaseDatabase.getInstance(Config.getDB_URL()).getReference();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupEventListener();

    }

    private void setupEventListener() {
        binding.btnAddChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addChild(v);
            }
        });
    }

    private void addChild(View v) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String name = binding.txtName.getText().toString();

        if(name.isEmpty()) {
            Toast.makeText(getActivity(), "Enter name",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Create childs
        DBHelper.saveChild(DB, user.getUid(), name);

        // Make alert
        Toast.makeText(getActivity(), "Child added",
                Toast.LENGTH_SHORT).show();

        // Set txtName to null
        binding.txtName.setText("");

        // Move to Home fragment
        Navigation.findNavController(v).navigate(R.id.action_addChildFragment_to_navigation_home);
    }
}