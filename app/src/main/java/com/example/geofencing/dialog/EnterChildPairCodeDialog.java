package com.example.geofencing.dialog;

import android.app.Dialog;
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
import androidx.navigation.Navigation;

import com.example.geofencing.Config;
import com.example.geofencing.R;
import com.example.geofencing.databinding.DialogEnterChildPaircodeBinding;
import com.example.geofencing.helper.DBHelper;
import com.example.geofencing.model.ChildPairCode;
import com.example.geofencing.util.SharedPreferencesUtil;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class EnterChildPairCodeDialog extends DialogFragment {

    private static final String TAG = "EnterAreaNameDialog";
    DialogEnterChildPaircodeBinding binding;

    SharedPreferencesUtil sf;
    private DatabaseReference DB;
    FirebaseAuth Auth;
    View view;

    public EnterChildPairCodeDialog(View view) {
        // Required empty public constructor
        this.view = view;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DialogEnterChildPaircodeBinding.inflate(inflater, container, false);
        DB = FirebaseDatabase.getInstance(Config.getDB_URL()).getReference();
        Auth = FirebaseAuth.getInstance();
        if (getArguments() != null) {
            List<LatLng> points = getArguments().getParcelableArrayList("points");

        }

        binding.btnSubmit.setOnClickListener(v -> { validatePairCode(); });


        sf = new SharedPreferencesUtil(requireContext());

        return binding.getRoot();
    }

    private void validatePairCode() {

        String pairCode = binding.txtAreaName.getText().toString().trim();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (pairCode.isEmpty()) {
            binding.txtAreaName.setError("Pair code is required");
            return;
        }

        DB.child("child_pair_code").child(pairCode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String childUid = snapshot.child("childId").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    String username = snapshot.child("username").getValue(String.class);

                    ChildPairCode childPairCode = new ChildPairCode(username, email, childUid);

                    DBHelper.saveChildToParent(DB, userId,pairCode, childPairCode);
                    DBHelper.saveParentToChild(DB, childUid, userId);
                    DBHelper.saveFcmTokenToChild(DB, childUid, Auth.getUid(), sf.getPref("parent_fcm_token", getContext()));

                } else {
                    binding.txtAreaName.setError("Pair code not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



//        Navigation.findNavController(view).navigate(R.id.action_addAreaFragment_to_navigation_dashboard);

        Toast.makeText(getActivity(), "Child saved", Toast.LENGTH_SHORT).show();
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