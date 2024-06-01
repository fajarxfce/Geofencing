package com.example.geofencing.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.geofencing.databinding.AreaAdapterBinding;
import com.example.geofencing.model.Area;

import java.util.ArrayList;
import java.util.List;

public class AreaAdapter extends RecyclerView.Adapter<AreaAdapter.ViewHolder>{

    List<Area> areaList = new ArrayList<>();
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

    public AreaAdapter(List<Area> childList) {
        this.areaList = childList;
    }

    @NonNull
    @Override
    public AreaAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AreaAdapterBinding binding = AreaAdapterBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AreaAdapter.ViewHolder holder, int position) {
        holder.binding.tvName.setText(areaList.get(position).getName());
        holder.binding.tvId.setText(areaList.get(position).getId());
        holder.binding.getRoot().setOnClickListener(v -> listener.onItemClick(v, position));
        holder.binding.getRoot().setOnLongClickListener(v -> {
            longClickListener.onItemLongClick(v, position);
            return true;
        });


    }

    @Override
    public int getItemCount() {
        return areaList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        AreaAdapterBinding binding;
        public ViewHolder(AreaAdapterBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
