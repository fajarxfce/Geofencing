package com.example.geofencing.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.geofencing.adapter.ChildAdapter;
import com.example.geofencing.databinding.FragmentHomeBinding;
import com.example.geofencing.dialog.DeleteChildDialog;
import com.example.geofencing.model.Child;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
    }

    private void setupRecyclerView() {

        List<Child> childList = createList();
        ChildAdapter adapter = new ChildAdapter(childList);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.HORIZONTAL));
        binding.recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new ChildAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int i) {
                Toast.makeText(getContext(), "Item "+childList.get(i).getName()+" clicked", Toast.LENGTH_SHORT).show();
            }
        });

        adapter.setOnItemLongClickListener(new ChildAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int i) {
                DeleteChildDialog deleteChildDialog = new DeleteChildDialog(childList.get(i).getId(), childList.get(i).getName());
                deleteChildDialog.show(getParentFragmentManager(), "delete_child");
            }
        });

    }

    private List<Child> createList() {
        List<Child> childList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            childList.add(new Child(i, "Name " + i, "email"+i+"@gmail.com", "avatar"+i+".png"));
        }

        return childList;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}