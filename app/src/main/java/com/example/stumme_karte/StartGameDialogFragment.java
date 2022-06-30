package com.example.stumme_karte;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
// Dialog fragment that shows the dialog if the game starts
// request an results from onclick methods that set fragment results to ture or false
// depending if you click start game or not
public class StartGameDialogFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Bundle result = new Bundle();
        return new AlertDialog.Builder(getActivity())
                .setTitle("Stumme Karte")
                .setMessage("Los gehts \\[T]/")
                .setPositiveButton("START", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        result.putBoolean("startGame", true);
                        getParentFragmentManager().setFragmentResult("startRequest", result);
                    }
                })
                .setNegativeButton("NICHT JETZT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        result.putBoolean("startGame", false);
                        getParentFragmentManager().setFragmentResult("startRequest", result);
                    }
                }).create();
    }
}
