package com.example.geofencing.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.geofencing.databinding.UserAdapterBinding;
import com.example.geofencing.model.Child;

import java.util.ArrayList;
import java.util.List;

public class ChildAdapter extends RecyclerView.Adapter<ChildAdapter.ViewHolder>{

    List<Child> childList = new ArrayList<>();
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

    public ChildAdapter(List<Child> childList) {
        this.childList = childList;
    }

    @NonNull
    @Override
    public ChildAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        UserAdapterBinding binding = UserAdapterBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ChildAdapter.ViewHolder holder, int position) {
        holder.binding.tvName.setText(childList.get(position).getName());
        holder.binding.tvPairkey.setText(childList.get(position).getPairkey());
        holder.binding.getRoot().setOnClickListener(v -> listener.onItemClick(v, position));
        holder.binding.getRoot().setOnLongClickListener(v -> {
            longClickListener.onItemLongClick(v, position);
            return true;
        });


    }

    @Override
    public int getItemCount() {
        return childList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        UserAdapterBinding binding;
        public ViewHolder(UserAdapterBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
