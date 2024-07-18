package com.example.geofencing.ui.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.geofencing.Config;
import com.example.geofencing.R;
import com.example.geofencing.adapter.ChildAdapter;
import com.example.geofencing.adapter.ChildLocationHistoryAdapter;
import com.example.geofencing.databinding.FragmentChildLocationHistoryBinding;
import com.example.geofencing.dialog.ChildOptionDialog;
import com.example.geofencing.model.Child;
import com.example.geofencing.model.ChildLocationHistory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChildLocationHistoryFragment extends Fragment {

    private static final String TAG = "ChildLocationHistoryFragment";
    FragmentChildLocationHistoryBinding binding;
    private DatabaseReference DB;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentChildLocationHistoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();

    }


    private void setupRecyclerView() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String childId = getArguments().getString("id");
        // Get data from db
        DB = FirebaseDatabase.getInstance(Config.getDB_URL()).getReference("location_history/"+childId);
        DB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!isAdded()){
                    return;
                }
                List<ChildLocationHistory> historyList = new ArrayList<>();

                int i = 0;
                for (DataSnapshot clidSnapshot: dataSnapshot.getChildren()) {
                    i++;
                    String message = clidSnapshot.getValue(String.class);

                    historyList.add(new ChildLocationHistory(message));

                    Log.d(TAG, "onDataChange: "+message);
                }

                Collections.reverse(historyList);

                ChildLocationHistoryAdapter adapter = new ChildLocationHistoryAdapter(historyList);
                binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                binding.recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.HORIZONTAL));
                binding.recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Error", databaseError.getMessage());
            }
        });
    }
}