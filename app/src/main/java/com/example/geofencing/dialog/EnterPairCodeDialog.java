package com.example.geofencing.dialog;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.geofencing.R;
import com.example.geofencing.auth.LoginActivity;
import com.example.geofencing.databinding.DialogEnterPairCodeBinding;
import com.example.geofencing.ui.child.ChildActivity;
import com.example.geofencing.util.SharedPreferencesUtil;

public class EnterPairCodeDialog extends DialogFragment {

    DialogEnterPairCodeBinding binding;

    SharedPreferencesUtil sf;

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

        sf.setPref("pair_code", pairCode, requireContext());

        dismiss();
        Intent intent = new Intent(getActivity(), ChildActivity.class);
        startActivity(intent);

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