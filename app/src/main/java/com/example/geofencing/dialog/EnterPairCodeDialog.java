package com.example.geofencing.dialog;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.geofencing.Config;
import com.example.geofencing.R;
import com.example.geofencing.auth.LoginActivity;
import com.example.geofencing.databinding.DialogEnterPairCodeBinding;
import com.example.geofencing.model.Child;
import com.example.geofencing.model.ChildData;
import com.example.geofencing.ui.child.ChildActivity;
import com.example.geofencing.util.SharedPreferencesUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EnterPairCodeDialog extends DialogFragment {

    private static final String TAG = "EnterPairCodeDialog";
    DialogEnterPairCodeBinding binding;

    SharedPreferencesUtil sf;
    private DatabaseReference DB;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DialogEnterPairCodeBinding.inflate(inflater, container, false);

        binding.btnSubmit.setOnClickListener(v -> { validatePairCode(); });
        sf = new SharedPreferencesUtil(requireContext());

        return binding.getRoot();
    }

    private void validatePairCode() {
        String pairCode = binding.txtPairCode.getText().toString().trim();
        if (pairCode.isEmpty()) {
            binding.txtPairCode.setError("Pair code is required");
            return;
        }

        DB = FirebaseDatabase.getInstance(Config.getDB_URL()).getReference("childs/" + pairCode);

        DB.addListenerForSingleValueEvent(new ValueEventListener()  {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    ChildData childData = snapshot.getValue(ChildData.class);
                    // Execute
                    Toast.makeText(requireContext(), "Pair code is valid", Toast.LENGTH_SHORT).show();

                    if (childData != null) {
                        Log.d(TAG, "onDataChange: "+childData.getName().toString());
                        Log.d(TAG, "onDataChange: "+childData.getPairKey().toString());
                        Log.d(TAG, "onDataChange: "+childData.getParentId().toString());
                        sf.setPref("pair_code", pairCode, requireContext());
                        sf.setPref("name", childData.getName().toString(), requireContext());
                        sf.setPref("pair_key", childData.getPairKey().toString(), requireContext());
                        sf.setPref("parent_id", childData.getParentId().toString(), requireContext());
                        dismiss();
                        Intent intent = new Intent(getActivity(), ChildActivity.class);
                        startActivity(intent);
                    }

                }else {
                    Toast.makeText(requireContext(), "Pair code is invalid", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



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