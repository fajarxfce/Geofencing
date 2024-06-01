package com.example.geofencing.dialog;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.geofencing.Config;
import com.example.geofencing.R;
import com.example.geofencing.helper.DBHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DeleteAreaDialog extends DialogFragment {

    private String id;
    private String name;

    private DatabaseReference DB;

    public DeleteAreaDialog(String id, String name) {
        this.id = id;
        this.name = name;
        this.DB = FirebaseDatabase.getInstance(Config.getDB_URL()).getReference();
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction.
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Delete "+name+"?")
                .setPositiveButton("Delete", (dialog, id) -> {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    // Delete area
                     DBHelper.deleteArea(this.DB, user.getUid(), this.id);

                    // Make alert
                    Toast.makeText(getActivity(), "Area deleted",
                            Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", (dialog, id) -> {
                    // User cancels the dialog.
                });
        // Create the AlertDialog object and return it.
        return builder.create();
    }
}