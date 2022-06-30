package com.example.stumme_karte;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class ScoreDialogFragment extends DialogFragment {

    private int score = 0;
    private int maxScore = 0;

    public ScoreDialogFragment(int score, int maxScore) {
        this.score = score;
        this.maxScore = maxScore;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Bundle result = new Bundle();
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View scoreDialog = inflater.inflate(R.layout.dialog_score, null);
        return new AlertDialog.Builder(getActivity())
                .setTitle("Spiel Beendet - Punktzahl: " + score + "/" + maxScore)
                .setView(scoreDialog)
                .setPositiveButton("Speichern", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        TextView input = (TextView) scoreDialog.findViewById(R.id.playerName);
                        result.putString("playerName", input.getText().toString());
                        getParentFragmentManager().setFragmentResult("playerNameRequest", result);
                    }
                }).create();
    }
}
