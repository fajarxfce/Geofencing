package com.example.geofencing.ui.dashboard;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.geofencing.Config;
import com.example.geofencing.R;
import com.example.geofencing.adapter.AreaAdapter;
import com.example.geofencing.databinding.FragmentListAreaBinding;
import com.example.geofencing.dialog.DeleteAreaDialog;
import com.example.geofencing.model.Area;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListAreaFragment extends Fragment {

    private FragmentListAreaBinding binding;
    private DatabaseReference DB;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentListAreaBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        setupRecyclerView();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupEventListener();
    }

    private void setupEventListener() {
        binding.fabAddArea.setOnClickListener(view -> Navigation.findNavController(view).navigate(R.id.action_navigation_dashboard_to_addAreaFragment));
    }

    private void setupRecyclerView() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Get data from db
        DB = FirebaseDatabase.getInstance(Config.getDB_URL()).getReference("users/" + uid + "/areas");
        DB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Area> areaList = new ArrayList<>();

                int i = 0;
                for (DataSnapshot clidSnapshot: dataSnapshot.getChildren()) {
                    i++;

                    areaList.add(new Area(clidSnapshot.getKey(), clidSnapshot.child("name").getValue(String.class), clidSnapshot.getKey()));
                }

                AreaAdapter adapter = new AreaAdapter(areaList);
                binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                binding.recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.HORIZONTAL));
                binding.recyclerView.setAdapter(adapter);
                adapter.setOnItemClickListener((view, i1) -> {
                    // GGWP
                });

                adapter.setOnItemLongClickListener((view, i12) -> {
                    DeleteAreaDialog deleteAreaDialog = new DeleteAreaDialog(areaList.get(i12).getId(), areaList.get(i12).getName());
                    deleteAreaDialog.show(getParentFragmentManager(), "delete_area");
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Error", databaseError.getMessage());
            }
        });
    }
}