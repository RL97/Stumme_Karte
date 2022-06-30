package com.example.stumme_karte;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class QuitDialogFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Bundle result = new Bundle();
        return new AlertDialog.Builder(getActivity())
                .setTitle("Spiel beenden")
                .setMessage("Willst du das Spiel wirklich beenden?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        result.putBoolean("quit", true);
                        getParentFragmentManager().setFragmentResult("quitConfirmRequest", result);
                    }
                })
                .setNegativeButton("ZURUECK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        result.putBoolean("quit", false);
                        getParentFragmentManager().setFragmentResult("quitConfirmRequest", result);
                    }
                }).create();
    }
}
