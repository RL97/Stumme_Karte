package com.example.stumme_karte;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentResultListener;
import android.graphics.Point;

import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.TextureView;
import android.view.View;
import android.widget.Toast;
import android.os.Build;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.MotionEvent;
import android.view.WindowInsets;
import android.view.WindowInsetsController;

import com.example.stumme_karte.databinding.ActivityFullscreenBinding;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import room.GameDatabase;
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

    GameDatabase database;

    // this executor will run all runnables/callables
    // that access the database
    // off the main thread
    private ExecutorService executor;

    // all tasks available
    private List<Task> availableTasks;
    // random subset of tasks for current game
    private List<Task> gameTasks;
    Task currentTask;
    // gamestate will contain all tasks which were answered (id of tasks)
    // and whether the user guessed correctly
    private Hashtable<Integer, Boolean> gameState = new Hashtable<>();

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;


    // TODO
    //  add class-global static attribute
    //  defining (maybe 2-3%?) tolerance when checking whether
    //  guessed location matches coordinates of tasks location

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding = ActivityFullscreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mContentView = binding.map;
        mContentView.setOnTouchListener(handleTouch);

        executor = Executors.newSingleThreadScheduledExecutor();
        database = GameDatabase.getDatabase(getApplicationContext());

        getAvailableTasks();

        setupNavigationDrawer();

        getSupportFragmentManager().setFragmentResultListener("startRequest", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                if (result.getBoolean("startGame")) {
                    // TODO
                    //  should later be replaced by method
                    //  which selects random subset of tasks for the current game
                    gameTasks = availableTasks;
                    playGame();
                }
            }
        });

        showStartGameDialog();
    }

    private View.OnTouchListener handleTouch = new View.OnTouchListener() {

        @Override
        // onTouch tells you were on the Display you clicked
        // Added X, Y to get coordinates to compare with the defined Points
        public boolean onTouch(View v, MotionEvent event) {


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
                    double diff = compare(x,y);
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
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        double maxX = size.x;
        double maxY = size.y;
        //Log.i("TAG", "x,y "+maxX+ maxY);
        double xInPercent = (100/maxX * x );
        double yInPercent = (100/maxY * y );
        //Log.i("TAG", "Rechnung  "+100+" / "+maxX+ " * " +x+ " = " +xInPercent);
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
        Log.i("TAG", "Auswahl  "+ "Erwartet X = " +expectedX+ " auswahl X = "+ xInPercent);
        Log.i("TAG", "Auswahl  "+ "Erwartet Y = " +expectedY+ " auswahl Y = "+ yInPercent+ " gesammte Diff = "+ diff);

        return diff;
         // TODO
         //  Stätte anpassen Hamburg liegt im Meer laut definition
         //  Welche abweichungen sind Legitim 10% also 5%x und 5% Y zusmamen?


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
    }

    // override the onOptionsItemSelected()
    // function to implement
    // the item click listener callback
    // to open and close the navigation
    // drawer when the icon is clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // TODO
        //  add (?ListView-)Activity to display scores
        //  implement joker
        //  implement app-quit
        //  implement "manual" start of game in case initial dialog to start game is canceled
        //  hookup to this method with switch-case

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showStartGameDialog() {
        DialogFragment startGameDialog = new StartGameDialogFragment();
        startGameDialog.show(getSupportFragmentManager(), "startGameDialog");
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

    private void playGame() {
        // get first task which was not yet answered
        currentTask = gameTasks.get(gameState.size());
        // display taskmasterdialog with info about name of location to be guessed
        showTaskMasterDialog(currentTask.getLocation());

        // TODO
        //  set onTouchListener or onClickListener on View in onCreate()
        //  check whether coordinates of event match currentTask.getX()/currentTask.getY()
        //  add result and currentTask.getId() to gameState
        //  call playGame()
        //  repeat until gameState.size() == gameTasks.size()
        //  calculate and save Score
    }

    private void showTaskMasterDialog(String locationName) {
        DialogFragment taskMasterDialog = new TaskMasterDialogFragment(locationName);
        taskMasterDialog.show(getSupportFragmentManager(), "taskMasterDialog");
    }
}