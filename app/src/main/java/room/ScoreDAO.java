package room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ScoreDAO {
    @Query("SELECT * FROM scores")
    List<Score> getAllScores();

    @Query("SELECT * FROM scores WHERE player = :playerName")
    List<Score> getScores(String playerName);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long addScore(Score s);

    @Query("DELETE FROM scores")
    void deleteAllScores();
}
