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

@TypeConverters({CalendarTypeConverters.class})
@Database(entities = {Score.class, Task.class}, version = 2, exportSchema = false)
public abstract class GameDatabase extends RoomDatabase {

    private static GameDatabase INSTANCE;

    public abstract ScoreDAO scoreDAO();
    public abstract TaskDAO taskDAO();

    public static GameDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = buildDatabase(context);
        }
        return INSTANCE;
    }

    private static GameDatabase buildDatabase(final Context context) {
        return Room.databaseBuilder(context, GameDatabase.class, "GameDatabase")
                .addCallback(new Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        Executors.newSingleThreadScheduledExecutor().execute(new Runnable() {
                            @Override
                            public void run() {
                                GameDatabase database = getDatabase(context);

                                Calendar c = Calendar.getInstance();

                                database.scoreDAO().addScore(new Score(2, "Spieler 1", c));
                                database.scoreDAO().addScore(new Score(4, "Spieler 2", c));
                                database.scoreDAO().addScore(new Score(6, "Spieler 1", c));
                                database.scoreDAO().addScore(new Score(8, "Spieler 2", c));
                                database.scoreDAO().addScore(new Score(10, "Spieler 3", c));

                                database.taskDAO().addTask(new Task("Hamburg", 50, 10, 1));
                                database.taskDAO().addTask(new Task("Dresden", 90, 50, 1));
                                database.taskDAO().addTask(new Task("Augsburg", 50, 90, 1));
                                database.taskDAO().addTask(new Task("Saarbruecken", 10, 50, 1));
                            }
                        });
                    }
                })
                .fallbackToDestructiveMigration()
                .build();
    }
}
