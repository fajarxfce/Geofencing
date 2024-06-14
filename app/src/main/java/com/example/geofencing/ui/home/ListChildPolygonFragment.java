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
import android.widget.Toast;

import com.example.geofencing.Config;
import com.example.geofencing.R;
import com.example.geofencing.adapter.ChildAdapter;
import com.example.geofencing.adapter.ListChildPolygonAdapter;
import com.example.geofencing.bottomsheet.AddPolygonBottomsheet;
import com.example.geofencing.databinding.FragmentListChildPolygonBinding;
import com.example.geofencing.dialog.ChildOptionDialog;
import com.example.geofencing.model.Child;
import com.example.geofencing.model.ListChildPolygon;
import com.example.geofencing.util.SharedPreferencesUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListChildPolygonFragment extends Fragment {

    FragmentListChildPolygonBinding binding;
    private DatabaseReference DB;
    private static final String TAG = "ListChildPolygonFragment";
    SharedPreferencesUtil sp;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentListChildPolygonBinding.inflate(inflater, container, false);
        sp = new SharedPreferencesUtil(requireContext());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
        binding.fabAddPolygon.setOnClickListener(v -> {
            addPolygon();
        });
    }

    private void addPolygon() {
        String pairCode = getArguments().getString("id");

        Bundle bundle = new Bundle();
        bundle.putString("id", pairCode);
        AddPolygonBottomsheet addPolygonBottomsheet = new AddPolygonBottomsheet();
        addPolygonBottomsheet.setArguments(bundle);
        addPolygonBottomsheet.show(getParentFragmentManager(), addPolygonBottomsheet.getTag());
    }

    private void setupRecyclerView() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String pairCode = getArguments().getString("id");

        // Get data from db
        DB = FirebaseDatabase.getInstance(Config.getDB_URL()).getReference("childs/" + pairCode + "/areas");
        DB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!isAdded()){
                    return;
                }
                List<ListChildPolygon> listChildPolygons = new ArrayList<>();

                int i = 0;
                for (DataSnapshot clidSnapshot: dataSnapshot.getChildren()) {
                    i++;

                    String area = clidSnapshot.getValue(String.class);

                    listChildPolygons.add(new ListChildPolygon(area));

                }

                ListChildPolygonAdapter adapter = new ListChildPolygonAdapter(listChildPolygons);
                binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                binding.recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.HORIZONTAL));
                binding.recyclerView.setAdapter(adapter);
                adapter.setOnItemClickListener((view, i1) -> {

                    Toast.makeText(requireContext(), listChildPolygons.get(i1).getName(), Toast.LENGTH_SHORT).show();
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Error", databaseError.getMessage());
            }
        });
    }
}