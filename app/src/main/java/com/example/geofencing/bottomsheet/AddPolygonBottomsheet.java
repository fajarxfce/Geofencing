package com.example.geofencing.bottomsheet;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.geofencing.Config;
import com.example.geofencing.adapter.AreaAdapter;
import com.example.geofencing.databinding.AddChildPolygonBottomsheetBinding;
import com.example.geofencing.model.ChildPolygon;
import com.example.geofencing.model.Child;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AddPolygonBottomsheet extends BottomSheetDialogFragment {

    private static final String TAG = "AddPolygonBottomsheet";
    private DatabaseReference DB;
    AddChildPolygonBottomsheetBinding binding;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = AddChildPolygonBottomsheetBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));


        getAllPolygon();

    }

    private void getAllPolygon() {

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String pairCode = getArguments().getString("id");

        // Get data from db
        DB = FirebaseDatabase.getInstance(Config.getDB_URL()).getReference("users/" + uid + "/polygons");
        DB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!isAdded()){
                    return;
                }
                List<ChildPolygon> polygonList = new ArrayList<>();

                for (DataSnapshot clidSnapshot : dataSnapshot.getChildren()) {
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
                    assignToChild(pairCode, polygonList.get(i1).getName());
                });


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Error", databaseError.getMessage());
            }
        });
    }

    private void assignToChild(String pairCode, String areaName) {
        DB = FirebaseDatabase.getInstance(Config.getDB_URL()).getReference("childs").child(pairCode).child("polygons");
        DB.push().setValue(areaName);
        Toast.makeText(requireContext(), "Polygon " + areaName + " telah ditambahkan ", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStart() {
        super.onStart();

    }
}
