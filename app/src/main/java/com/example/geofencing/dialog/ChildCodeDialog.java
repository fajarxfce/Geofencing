package com.example.geofencing.dialog;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class ChildCodeDialog extends DialogFragment {

    private String code;

    public ChildCodeDialog(String code) {
        this.code = code;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction.
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Child Code :");
        builder.setMessage(code)
                .setNegativeButton("Done", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancels the dialog.
                        dismiss();
                    }
                });
        // Create the AlertDialog object and return it.
        return builder.create();
    }
}