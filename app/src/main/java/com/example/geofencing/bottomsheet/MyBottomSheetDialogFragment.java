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
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.geofencing.Config;
import com.example.geofencing.R;
import com.example.geofencing.adapter.ChildAdapter;
import com.example.geofencing.databinding.FragmentBottomsheetDialogBinding;
import com.example.geofencing.dialog.DeleteChildDialog;
import com.example.geofencing.model.Child;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MyBottomSheetDialogFragment extends com.google.android.material.bottomsheet.BottomSheetDialogFragment {

    private static final String TAG = "MyBottomSheetDialogFragment";
    private DatabaseReference DB;
    FragmentBottomsheetDialogBinding binding;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentBottomsheetDialogBinding.inflate(inflater, container, false);

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


        if (getArguments() != null) {
            String id = getArguments().getString("pair_code");
            getAllChild();
        }

    }

    private void getAllChild(){

            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            // Get data from db
            DB = FirebaseDatabase.getInstance(Config.getDB_URL()).getReference("users/" + uid + "/childs");
            DB.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    List<Child> childList = new ArrayList<>();

                    int i = 0;
                    for (DataSnapshot clidSnapshot: dataSnapshot.getChildren()) {
                        i++;

                        Log.d(TAG, "onDataChange: "+clidSnapshot.getKey());

                        childList.add(new Child(clidSnapshot.getKey(), clidSnapshot.child("name").getValue(String.class), clidSnapshot.getKey()));
                    }

                    ChildAdapter adapter = new ChildAdapter(childList);

                    binding.recyclerView.setAdapter(adapter);
                    adapter.setOnItemClickListener((view, i1) -> {
                        assignToChild(childList.get(i1).getId(), childList.get(i1).getName());
                    });

                    adapter.setOnItemLongClickListener((view, i12) -> {

                    });

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("Error", databaseError.getMessage());
                }
            });
    }

    private void assignToChild(String pairCode, String childName){
        String areaName = getArguments().getString("area_name");
        DB = FirebaseDatabase.getInstance(Config.getDB_URL()).getReference("childs").child(pairCode).child("areas");
        DB.push().setValue(areaName);
        Toast.makeText(requireContext(), "Polygon " + areaName +" telah ditambahkan ke  : " + childName, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStart() {
        super.onStart();

    }
}
