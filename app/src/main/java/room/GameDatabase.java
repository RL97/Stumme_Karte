package room;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.Calendar;
import java.util.concurrent.Executors;

// database class which enables room to manage associated classes
// the database schema is not exported
@TypeConverters({CalendarTypeConverters.class})
@Database(entities = {Score.class, Task.class}, version = 2, exportSchema = false)
public abstract class GameDatabase extends RoomDatabase {

    private static GameDatabase INSTANCE;

    public abstract ScoreDAO scoreDAO();
    public abstract TaskDAO taskDAO();

    // returns database instance
    // generates a new instance if none exists
    public static GameDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = buildDatabase(context);
        }
        return INSTANCE;
    }

    // generates database instance via Room.databaseBuilder
    // specifies that the database may use destructive migration
    // (i.e. resets database to initial state with each migration)
    // (scores will be lost)
    private static GameDatabase buildDatabase(final Context context) {
        return Room.databaseBuilder(context, GameDatabase.class, "GameDatabase")
                // adds a callback to the database which executes a runnable immediately after database creation
                .addCallback(new Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        // runnable pre-populates database with sample-scores and tasks which can be given to the user
                        Executors.newSingleThreadScheduledExecutor().execute(new Runnable() {
                            @Override
                            public void run() {
                                GameDatabase database = getDatabase(context);

                                Calendar c = Calendar.getInstance();

                                database.scoreDAO().addScore(new Score(10, 2, "Spieler 1", c));
                                database.scoreDAO().addScore(new Score(10, 4, "Spieler 2", c));
                                database.scoreDAO().addScore(new Score(10, 6, "Spieler 1", c));
                                database.scoreDAO().addScore(new Score(10, 8, "Spieler 2", c));
                                database.scoreDAO().addScore(new Score(10, 10, "Spieler 3", c));

                                database.taskDAO().addTask(new Task("Hamburg", 45, 30, 1));
                                database.taskDAO().addTask(new Task("Dresden", 85, 50, 1));
                                database.taskDAO().addTask(new Task("Augsburg", 60, 73, 1));
                                database.taskDAO().addTask(new Task("Saarbruecken", 11, 65, 1));
                                database.taskDAO().addTask(new Task("K??ln", 11, 50, 1));
                                database.taskDAO().addTask(new Task("Stralsund", 75, 25, 1));
                                database.taskDAO().addTask(new Task("Flensburg", 40, 20, 1));
                                database.taskDAO().addTask(new Task("Bremen", 33, 34, 1));
                                database.taskDAO().addTask(new Task("Berlin", 80, 38, 1));
                                database.taskDAO().addTask(new Task("Winterberg", 27, 47, 1));
                                database.taskDAO().addTask(new Task("Karlsruhe", 27, 67, 1));
                                database.taskDAO().addTask(new Task("M??nchen", 65, 73, 1));
                                database.taskDAO().addTask(new Task("Kiel", 64, 24, 1));
                                database.taskDAO().addTask(new Task("Basel", 18, 78, 1));
                            }
                        });
                    }
                })
                .fallbackToDestructiveMigration()
                .build();
    }
}
