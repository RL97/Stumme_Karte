package com.example.stumme_karte;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import room.GameDatabase;
import room.Score;
import room.Task;

public class ScoresActivity extends AppCompatActivity {

    private GameDatabase database;

    private ExecutorService executor;

    private List<Score> scores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);

        database = GameDatabase.getDatabase(getApplicationContext());
        executor = Executors.newSingleThreadScheduledExecutor();

        getScores();

        ArrayAdapter<Score> adapter = new ArrayAdapter<Score>(this, android.R.layout.simple_list_item_1, scores);

        ListView listView = (ListView) findViewById(R.id.scoreList);
        listView.setAdapter(adapter);
    }

    private void getScores() {
        Future f = executor.submit(new Callable<List<Score>>() {
            @Override
            public List<Score> call() throws Exception {
                return database.scoreDAO().getAllScores();
            }
        });

        try {
            scores = (List<Score>) f.get();
            Collections.reverse(scores);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}