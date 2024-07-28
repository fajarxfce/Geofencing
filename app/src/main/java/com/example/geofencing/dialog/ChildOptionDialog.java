package com.example.geofencing.dialog;


import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.navigation.Navigation;

import com.example.geofencing.Config;
import com.example.geofencing.R;
import com.example.geofencing.helper.DBHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChildOptionDialog extends DialogFragment {

    private static final String TAG = "ChildOptionDialog";
    private String id;
    private String name, pairCode;
    private View view;

    private DatabaseReference DB;

    public ChildOptionDialog(View view, String id, String name, String paircode) {
        this.id = id;
        this.name = name;
        this.pairCode = paircode;
        this.view = view;
        this.DB = FirebaseDatabase.getInstance(Config.getDB_URL()).getReference();
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction.
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String[] options = {"Lihat Lokasi Anak", "Lihat Riwayat Lokasi", "Area", "Hapus Anak"};

        Bundle bundle = new Bundle();
        bundle.putString("id", this.id);
        bundle.putString("name", this.name);

        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0:
                    // Lihat Lokasi Anak


                    Navigation.findNavController(view).navigate(R.id.action_navigation_home_to_trackChildMapsFragment, bundle);
                    break;
                case 1:
                    // Lihat Riwayat Lokasi
                    Navigation.findNavController(view).navigate(R.id.action_navigation_home_to_childLocationHistoryFragment, bundle);
                    break;
                case 2:
                    // List Polygon
                    Navigation.findNavController(view).navigate(R.id.action_navigation_home_to_listChildPolygonFragment, bundle);

                    break;
                case 3:
                    // Hapus Anak
                    new DeleteChildDialog(this.id, this.name, this.pairCode).show(getParentFragmentManager(), "delete_child");
                    Log.d(TAG, "onCreateDialog: "+this.pairCode);
                    break;
            }
        });

        // Create the AlertDialog object and return it.
        return builder.create();
    }
}