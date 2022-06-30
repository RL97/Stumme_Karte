package room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

// dao of score entity
// allows for
// the retrieval of all scores
// the retrieval of scores of a certain player
// the insertion a score (conflict strategy = replace ensures scores with the same primary keys cause override)
// the deletion of all scores
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
