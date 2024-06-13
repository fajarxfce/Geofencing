package com.example.geofencing.ui.home;

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
import com.example.geofencing.adapter.ChildAdapter;
import com.example.geofencing.databinding.FragmentHomeBinding;
import com.example.geofencing.dialog.ChildCodeDialog;
import com.example.geofencing.dialog.ChildOptionDialog;
import com.example.geofencing.dialog.DeleteChildDialog;
import com.example.geofencing.model.Child;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private FragmentHomeBinding binding;
    private DatabaseReference DB;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
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
        binding.fabAddChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_navigation_home_to_addChildFragment);
            }
        });
    }

    private void setupRecyclerView() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Get data from db
        DB = FirebaseDatabase.getInstance(Config.getDB_URL()).getReference("users/" + uid + "/childs");
        DB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!isAdded()){
                    return;
                }
                List<Child> childList = new ArrayList<>();

                int i = 0;
                for (DataSnapshot clidSnapshot: dataSnapshot.getChildren()) {
                    i++;

                    Double lat = clidSnapshot.child("latitude").getValue(Double.class);
                    Double lng = clidSnapshot.child("longitude").getValue(Double.class);

                    if (lat != null || lng != null) {
                        Log.d(TAG, "onDataChange: have lat lng" + i + " " + clidSnapshot.getKey() + " " + clidSnapshot.child("name").getValue(String.class) + " " + lat + " " + lng);
                    }

                    childList.add(new Child(clidSnapshot.getKey(), clidSnapshot.child("name").getValue(String.class), clidSnapshot.getKey()));
                }

                ChildAdapter adapter = new ChildAdapter(childList);
                binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                binding.recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.HORIZONTAL));
                binding.recyclerView.setAdapter(adapter);
                adapter.setOnItemClickListener((view, i1) -> {
                    final Bundle bundle = new Bundle();
                    bundle.putString("id", childList.get(i1).getId());
                    bundle.putString("name", childList.get(i1).getName());
//                    ChildCodeDialog childCodeDialog = new ChildCodeDialog(childList.get(i1).getPairkey());
//                    childCodeDialog.show(getParentFragmentManager(), "child_code");
//                    Navigation.findNavController(view).navigate(R.id.action_navigation_home_to_trackChildMapsFragment, bundle);
                    ChildOptionDialog childOptionDialog = new ChildOptionDialog(view, childList.get(i1).getId(), childList.get(i1).getName());
                    childOptionDialog.show(getParentFragmentManager(), "child_option");
                });

//                adapter.setOnItemLongClickListener((view, i12) -> {
//                    String id = childList.get(i12).getId();
//                    String name = childList.get(i12).getName();
//
//                    DeleteChildDialog deleteChildDialog = new DeleteChildDialog(id, name);
//                    deleteChildDialog.show(getParentFragmentManager(), "delete_child");
//                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Error", databaseError.getMessage());
            }
        });
    }

    private void showChildOptionDialog() {
        Toast.makeText(requireContext(), "Option", Toast.LENGTH_SHORT).show();
    }

}