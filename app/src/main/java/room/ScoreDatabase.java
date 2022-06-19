package room;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.Calendar;
import java.util.concurrent.Executors;

@TypeConverters({CalendarTypeConverters.class})
@Database(entities = {Score.class}, version = 1, exportSchema = false)
public abstract class ScoreDatabase extends RoomDatabase {

    private static ScoreDatabase INSTANCE;

    public abstract ScoreDAO scoreDAO();

    public static ScoreDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = buildDatabase(context);
        }
        return INSTANCE;
    }

    private static ScoreDatabase buildDatabase(final Context context) {
        return Room.databaseBuilder(context, ScoreDatabase.class, "ScoreDatabase")
                .addCallback(new Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        Executors.newSingleThreadScheduledExecutor().execute(new Runnable() {
                            @Override
                            public void run() {
                                ScoreDatabase database = getDatabase(context);

                                Calendar c = Calendar.getInstance();

                                database.scoreDAO().addScore(new Score(2, "Spieler 1", c));
                                database.scoreDAO().addScore(new Score(4, "Spieler 2", c));
                                database.scoreDAO().addScore(new Score(6, "Spieler 1", c));
                                database.scoreDAO().addScore(new Score(8, "Spieler 2", c));
                                database.scoreDAO().addScore(new Score(10, "Spieler 3", c));
                            }
                        });
                    }
                })
                .fallbackToDestructiveMigration()
                .build();
    }
}
