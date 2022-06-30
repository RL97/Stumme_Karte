package com.example.stumme_karte;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

// Dialog fragment that shows the dialog which city you have to click while playing the game

public class TaskMasterDialogFragment extends DialogFragment {

    private String locationName;
    // generates the dialog if it gets a city
    // ensures that a location can be displayed
    public TaskMasterDialogFragment(String locationName) {
        this.locationName = locationName;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle("Finde die Stadt:")
                .setMessage(locationName)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // might not need any specific behavior
                        // since it mainly serves as a conveyor of information for the user
                    }
                }).create();
    }
}
