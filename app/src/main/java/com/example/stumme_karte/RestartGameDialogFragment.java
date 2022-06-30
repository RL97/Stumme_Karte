package com.example.stumme_karte;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class RestartGameDialogFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Bundle result = new Bundle();
        return new AlertDialog.Builder(getActivity())
                .setTitle("Neues Spiel")
                .setMessage("Das aktuelle Spiel wird abgebrochen und ein neues gestartet!")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        result.putBoolean("restart", true);
                        getParentFragmentManager().setFragmentResult("restartConfirmRequest", result);
                    }
                })
                .setNegativeButton("ZURUECK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        result.putBoolean("restart", false);
                        getParentFragmentManager().setFragmentResult("restartConfirmRequest", result);
                    }
                }).create();
    }
}
