package com.example.geofencing.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.navigation.Navigation;

import com.example.geofencing.Config;
import com.example.geofencing.R;
import com.example.geofencing.databinding.DialogEnterAreaNameBinding;
import com.example.geofencing.databinding.DialogEnterPairCodeBinding;
import com.example.geofencing.helper.DBHelper;
import com.example.geofencing.util.SharedPreferencesUtil;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Random;

public class EnterAreaNameDialog extends DialogFragment {

    private static final String TAG = "EnterAreaNameDialog";
    DialogEnterAreaNameBinding binding;

    SharedPreferencesUtil sf;
    private DatabaseReference DB;
    View view;

    public EnterAreaNameDialog(View view) {
        // Required empty public constructor
        this.view = view;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DialogEnterAreaNameBinding.inflate(inflater, container, false);
        if (getArguments() != null) {
            List<LatLng> points = getArguments().getParcelableArrayList("points");
            binding.btnSubmit.setOnClickListener(v -> { saveArea(points); });
        }


        sf = new SharedPreferencesUtil(requireContext());

        return binding.getRoot();
    }

    private void saveArea(List<LatLng> points) {
        String areaName = binding.txtAreaName.getText().toString().trim();
        if (areaName.isEmpty()) {
            binding.txtAreaName.setError("Area name is required");
            return;
        }

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DB = FirebaseDatabase.getInstance(Config.getDB_URL()).getReference();

        DBHelper.saveArea(DB, uid, areaName, points);

        Navigation.findNavController(view).navigate(R.id.action_addAreaFragment_to_navigation_dashboard);

        Toast.makeText(getActivity(), "Area saved", Toast.LENGTH_SHORT).show();
        dismiss();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        return dialog;
    }

}