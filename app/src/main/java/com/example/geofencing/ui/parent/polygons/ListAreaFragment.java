package com.example.geofencing.ui.parent.polygons;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.example.geofencing.model.ChildPolygon;
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

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
        setupEventListener();
    }

    private void setupEventListener() {
        binding.fabAddArea.setOnClickListener(view -> Navigation.findNavController(view).navigate(R.id.action_navigation_polygons_to_addPolygonMapsFragment));
    }

    private void setupRecyclerView() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Get data from db
        DB = FirebaseDatabase.getInstance(Config.getDB_URL()).getReference("users/" + uid + "/polygons");
        DB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!isAdded()){
                    return;
                }
                List<ChildPolygon> polygonList = new ArrayList<>();

                int i = 0;
                for (DataSnapshot clidSnapshot: dataSnapshot.getChildren()) {
                    i++;

                    polygonList.add(new ChildPolygon(clidSnapshot.getKey(), clidSnapshot.child("name").getValue(String.class), clidSnapshot.getKey()));
                }

                AreaAdapter adapter = new AreaAdapter(polygonList);
                binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                binding.recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.HORIZONTAL));
                binding.recyclerView.setAdapter(adapter);
                adapter.setOnItemClickListener((view, i1) -> {
                    final Bundle bundle = new Bundle();
                    bundle.putString("id", polygonList.get(i1).getId());
                    bundle.putString("area_name", polygonList.get(i1).getName());
                    Navigation.findNavController(view).navigate(R.id.action_navigation_dashboard_to_detailMapFragment, bundle);
                });

                adapter.setOnItemLongClickListener((view, i12) -> {
                    DeleteAreaDialog deleteAreaDialog = new DeleteAreaDialog(polygonList.get(i12).getId(), polygonList.get(i12).getName());
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