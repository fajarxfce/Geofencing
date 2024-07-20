package com.example.geofencing.ui.parent.childs;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.geofencing.Config;
import com.example.geofencing.adapter.ListChildPolygonAdapter;
import com.example.geofencing.bottomsheet.AddPolygonBottomsheet;
import com.example.geofencing.databinding.FragmentListChildPolygonBinding;
import com.example.geofencing.model.ListChildPolygon;
import com.example.geofencing.util.SharedPreferencesUtil;
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
        Log.d(TAG, "onViewCreated: "+getArguments().getString("id"));
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
        String pairCode = getArguments().getString("id");

        DB = FirebaseDatabase.getInstance(Config.getDB_URL()).getReference("childs/" + pairCode + "/polygons");
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

                    String value = clidSnapshot.getValue(String.class);
                    String key = clidSnapshot.getKey();
                    listChildPolygons.add(new ListChildPolygon(key, value));

                }

                ListChildPolygonAdapter adapter = new ListChildPolygonAdapter(listChildPolygons);
                binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                binding.recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.HORIZONTAL));
                binding.recyclerView.setAdapter(adapter);
                adapter.setOnItemClickListener((view, i1) -> {
                    showDeleteConfirmationDialog(listChildPolygons.get(i1).getKey(), getArguments().getString("id"));
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Error", databaseError.getMessage());
            }
        });
    }

    private void showDeleteConfirmationDialog(String key, String childId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Hapus Polygon");
        builder.setMessage("Apakah Anda yakin ingin menghapus polygon ini?");

        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                removePolygonFromChild(key, childId);
            }
        });
        builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog, do nothing
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private  void removePolygonFromChild(String key, String childId){
        DB = FirebaseDatabase.getInstance(Config.getDB_URL()).getReference("childs/" + childId + "/polygons");

        Log.d(TAG, "removeFromChild: "+key);

        DB.child(key).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                Toast.makeText(requireContext(), "Berhasil menghapus polygon", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(requireContext(), "Gagal menghapus polygon", Toast.LENGTH_SHORT).show();
            }
        });
    }
}