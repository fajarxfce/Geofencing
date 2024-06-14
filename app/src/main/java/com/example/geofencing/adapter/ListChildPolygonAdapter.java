package com.example.geofencing.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.geofencing.databinding.UserAdapterBinding;
import com.example.geofencing.model.ListChildPolygon;

import java.util.ArrayList;
import java.util.List;

public class ListChildPolygonAdapter extends RecyclerView.Adapter<ListChildPolygonAdapter.ViewHolder>{

    List<ListChildPolygon> listChildPolygons = new ArrayList<>();
    OnItemClickListener listener;
    OnItemLongClickListener longClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, int i);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(View view, int i);
    }


    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener longClickListener) {
        this.longClickListener = longClickListener;
    }

    public ListChildPolygonAdapter(List<ListChildPolygon> listChildPolygons) {
        this.listChildPolygons = listChildPolygons;
    }

    @NonNull
    @Override
    public ListChildPolygonAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        UserAdapterBinding binding = UserAdapterBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ListChildPolygonAdapter.ViewHolder holder, int position) {
        holder.binding.tvName.setText(listChildPolygons.get(position).getName());
        holder.binding.getRoot().setOnClickListener(v -> listener.onItemClick(v, position));

    }

    @Override
    public int getItemCount() {
        return listChildPolygons.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        UserAdapterBinding binding;
        public ViewHolder(UserAdapterBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
