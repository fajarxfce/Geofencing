package com.example.geofencing.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.geofencing.databinding.LocationHistoryAdapterBinding;
import com.example.geofencing.databinding.UserAdapterBinding;
import com.example.geofencing.model.ChildLocationHistory;

import java.util.ArrayList;
import java.util.List;

public class ChildLocationHistoryAdapter extends RecyclerView.Adapter<ChildLocationHistoryAdapter.ViewHolder>{

    List<ChildLocationHistory> historyList = new ArrayList<>();
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

    public ChildLocationHistoryAdapter(List<ChildLocationHistory> historyList) {
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public ChildLocationHistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LocationHistoryAdapterBinding binding = LocationHistoryAdapterBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ChildLocationHistoryAdapter.ViewHolder holder, int position) {
        holder.binding.tvName.setText(historyList.get(position).getMessage());
        holder.binding.getRoot().setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(v, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        LocationHistoryAdapterBinding binding;
        public ViewHolder(LocationHistoryAdapterBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
