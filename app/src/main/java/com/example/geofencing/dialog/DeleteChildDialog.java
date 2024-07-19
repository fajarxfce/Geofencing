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

public class DeleteChildDialog extends DialogFragment {

    private static final String TAG = "DeleteChildDialog";
    private String id;
    private String name;
    private String pairCode;

    private DatabaseReference DB;
    private FirebaseAuth Auth;

    public DeleteChildDialog(String id, String name, String pairCode) {
        this.id = id;
        this.name = name;
        this.pairCode = pairCode;
        this.DB = FirebaseDatabase.getInstance(Config.getDB_URL()).getReference();
        this.Auth = FirebaseAuth.getInstance();
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction.
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Hapus "+name+"?")
                .setPositiveButton("Delete", (dialog, id) -> {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    Log.d(TAG, "onCreateDialog: name: "+this.name);
                    Log.d(TAG, "onCreateDialog: id :"+this.id);
                    Log.d(TAG, "onCreateDialog: id :"+this.pairCode);

                    // Delete child
                    DBHelper.deleteChildFromParent(this.DB, Auth.getUid(), this.pairCode);
                    DBHelper.deleteParentFromChild(this.DB, this.id, Auth.getUid());
                    DBHelper.deleteParentFcmFromChild(this.DB, this.id, Auth.getUid());

                    // Make alert
                    Toast.makeText(getActivity(), "Child deleted",
                            Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", (dialog, id) -> {
                    // User cancels the dialog.
                });
        // Create the AlertDialog object and return it.
        return builder.create();
    }
}