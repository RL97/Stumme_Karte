package com.example.stumme_karte;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentResultListener;

import android.content.Intent;
import android.graphics.Point;

import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.os.Build;
import android.os.Handler;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stumme_karte.databinding.ActivityFullscreenBinding;
import com.google.android.material.navigation.NavigationView;

import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import room.GameDatabase;
import room.Score;
import room.Task;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 1000;

    private final Handler mHideHandler = new Handler();

    private View mContentView;

    private ActivityFullscreenBinding binding;

    private GameDatabase database;

    // this executor will run all runnables/callables
    // that access the database
    // off the main thread
    private ExecutorService executor;

    // all tasks available
    private List<Task> availableTasks;
    // random subset of tasks for current game
    private Hashtable<Integer, Task> gameTasks;
    Task currentTask = null;
    // gamestate will contain all tasks which were answered (id of tasks)
    // and whether the user guessed correctly
    private Hashtable<Integer, Boolean> gameState = new Hashtable<>();

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;


    // TODO
    //  adjustments needed
    private double tolerance;
    
    private double maxX;
    private double maxY;

    private int score = 0;
    private int maxScore = 0;

    private TextView jokerStatusTextView;
    private int jokerAvailable = 3;
    private int jokerUsed = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding = ActivityFullscreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mContentView = binding.map;
        mContentView.setOnTouchListener(handleTouch);

        setupConfiguration();

        executor = Executors.newSingleThreadScheduledExecutor();
        database = GameDatabase.getDatabase(getApplicationContext());

        getAvailableTasks();

        setupNavigationDrawer();

        getSupportActionBar().setTitle("Joker verbleibend: " + " " + (jokerAvailable - jokerUsed));

        setFragmentResultListeners();

        showStartGameDialog();
    }

    private void setupConfiguration() {
        // setup device screen configuration
        // and percentage based tolerance
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        maxX = size.x;
        maxY = size.y;
        tolerance = 10;

    }

    private View.OnTouchListener handleTouch = new View.OnTouchListener() {

        @Override
        // onTouch will execute once the user interacts with the screen
        // Added X, Y to get coordinates to compare with the defined Points
        public boolean onTouch(View v, MotionEvent event) {

            if (currentTask == null) {
                return true;
            }
            // gets X and Y coordinates from the clicked Point
            int x = (int) event.getX();
            int y = (int) event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // Log for testing
                    Log.i("TAG", "touched down " + x + ", " + y);
                    break;
                case MotionEvent.ACTION_MOVE:
                    // Log for testing
                    Log.i("TAG", "moving: (" + x + ", " + y + ")");
                    break;
                case MotionEvent.ACTION_UP:
                    // compares selected point with Database and returns the difference
                    double diff = compare(x,y);
                    // check if difference is low enough to be tolerated
                    if (diff <= tolerance) {
                        gameState.put(currentTask.getId(), true);
                    } else {
                        gameState.put(currentTask.getId(), false);
                    }
                    playGame();
                    break;
            }

            return true;
        }
    };


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    public double compare(double x, double y) {
        // compare clicked coordinates with saved

        // get persantage value of the with and length of the screen
        double xInPercent = (100/maxX * x );
        double yInPercent = (100/maxY * y );

        double expectedX = currentTask.getX();
        double expectedY = currentTask.getY();

        double diff = 0;
        /// Diff from X
        if(xInPercent>expectedX){
            diff += xInPercent-expectedX;
        }
        else{
            diff += expectedX-xInPercent;
        }
        /// Diff from X
        if(yInPercent>expectedY){
            diff += yInPercent-expectedY;
        }
        else{
            diff += expectedY-yInPercent;
        }
        // Testing purpose
        Log.i("TAG", "Auswahl  "+ "Erwartet X = " +expectedX+ " auswahl X = "+ xInPercent);
        Log.i("TAG", "Auswahl  "+ "Erwartet Y = " +expectedY+ " auswahl Y = "+ yInPercent+ " gesammte Diff = "+ diff);

        return diff;
    }

    private void hide() {
        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar
            if (Build.VERSION.SDK_INT >= 30) {
                mContentView.getWindowInsetsController().hide(WindowInsets.Type.systemBars());
                mContentView.getWindowInsetsController().setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            } else {
                // Note that some of these constants are new as of API 16 (Jelly Bean)
                // and API 19 (KitKat). It is safe to use them, as they are inlined
                // at compile-time and do nothing on earlier devices.
                mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            }
        }
    };

    private void setupNavigationDrawer() {
        // drawer layout instance to toggle the menu icon to open
        // drawer and back button to close drawer
        drawerLayout = findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);

        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        // to make the Navigation drawer icon always appear on the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navView = (NavigationView) findViewById(R.id.navigation);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawerLayout.closeDrawers();
                switch (item.getItemId()) {
                    case R.id.nav_newGame:
                        if (currentTask != null) {
                            showRestartDialog();
                        } else {
                            initGame();
                        }
                        return true;
                    case R.id.nav_scores:
                        Intent scoreIntent = new Intent(getApplicationContext(), ScoresActivity.class);
                        startActivity(scoreIntent);
                        return true;
                    case R.id.nav_joker:
                        if (jokerUsed < jokerAvailable && currentTask != null) {
                            jokerUsed++;
                            gameState.put(currentTask.getId(), true);
                            getSupportActionBar().setTitle("Joker verbleibend: " + " " + (jokerAvailable - jokerUsed));
                            playGame();
                        }
                        return true;
                    case R.id.nav_quit:
                        showQuitDialog();
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    // override the onOptionsItemSelected()
    // function to implement
    // the item click listener callback
    // to open and close the navigation
    // drawer when the icon is clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getAvailableTasks() {
        // submitting a callable to the executorService returns a future
        // representing the pending results of the task
        Future f = executor.submit(new Callable<List<Task>>() {
            @Override
            public List<Task> call() throws Exception {
                // database-access
                return database.taskDAO().getAllTasks();
            }
        });

        try {
            // the futures get method will return the tasks result upon successful completion
            availableTasks = (List<Task>) f.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void setFragmentResultListeners() {
        getSupportFragmentManager().setFragmentResultListener("startRequest", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                if (result.getBoolean("startGame")) {
                    initGame();
                }
            }
        });

        getSupportFragmentManager().setFragmentResultListener("playerNameRequest", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        Calendar c = Calendar.getInstance();
                        database.scoreDAO().addScore(new Score(maxScore, score, result.getString("playerName"), c));
                    }
                });
            }
        });

        getSupportFragmentManager().setFragmentResultListener("quitConfirmRequest", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                if (result.getBoolean("quit")) {
                    finish();
                }
            }
        });

        getSupportFragmentManager().setFragmentResultListener("restartConfirmRequest", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                if (result.getBoolean("restart")) {
                    initGame();
                }
            }
        });
    }

    private void initGame() {
        gameTasks = new Hashtable<>();

        // TODO
        //  should later be replaced by method
        //  which selects random subset of tasks for the current game
        while (gameTasks.size()<10){

            Random rand = new Random();
            int n = rand.nextInt(availableTasks.size());
            if (gameTasks.get(availableTasks.get(n).getId()) == null){
                gameTasks.put(availableTasks.get(n).getId(),availableTasks.get(n));
                availableTasks.get(n);
            }
        }


        score = 0;
        maxScore = 0;
        playGame();
    }

    private void playGame() {
        if (gameState.size() == gameTasks.size()) {
            for (Hashtable.Entry<Integer, Boolean> entry : gameState.entrySet()) {
                Integer taskId = entry.getKey();
                Boolean result = entry.getValue();
                maxScore += gameTasks.get(taskId).getPoints();
                if (result) {
                    score += gameTasks.get(taskId).getPoints();
                }
            }
            showScoreDialog(score, maxScore);
            return;
        }

        // get first task which was not yet answered
        Integer[] keys = gameTasks.keySet().toArray(new Integer[gameTasks.keySet().size()]);
        currentTask = gameTasks.get(keys[gameState.size()]);
        // display taskmasterdialog with info about name of location to be guessed
        showTaskMasterDialog(currentTask.getLocation());
    }

    private void showStartGameDialog() {
        DialogFragment startGameDialog = new StartGameDialogFragment();
        startGameDialog.show(getSupportFragmentManager(), "startGameDialog");
    }

    private void showTaskMasterDialog(String locationName) {
        DialogFragment taskMasterDialog = new TaskMasterDialogFragment(locationName);
        taskMasterDialog.show(getSupportFragmentManager(), "taskMasterDialog");
    }

    private void showScoreDialog(int score, int maxScore) {
        currentTask = null;
        DialogFragment scoreDialog = new ScoreDialogFragment(score, maxScore);
        scoreDialog.show(getSupportFragmentManager(), "scoreDialog");
    }

    private void showQuitDialog() {
        DialogFragment quitDialog = new QuitDialogFragment();
        quitDialog.show(getSupportFragmentManager(), "quitDialog");
    }

    private void showRestartDialog() {
        DialogFragment restartDialog = new RestartGameDialogFragment();
        restartDialog.show(getSupportFragmentManager(), "restartDialog");
    }
}