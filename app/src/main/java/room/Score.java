package room;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Calendar;

// entity of score relation
// @PrimaryKey annotation with argument autogenerate = true ensures its automatic generation
// overridden toString method allows for automatic display of entity in listview
// consists of:
// id       ; primary key
// maxScore ; score which could have been achieved in the played game
// score    ; score which was achieved by the player
// player   ; player name
// date     ; date at which highscore was achieved
@Entity(tableName = "scores")
public class Score {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private int maxScore;

    @NonNull
    private int score;

    @NonNull
    private String player;

    @NonNull
    private Calendar date;

    public Score(int maxScore, int score, @NonNull String player, @NonNull Calendar date) {
        this.maxScore = maxScore;
        this.score = score;
        this.player = player;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public int getMaxScore() {
        return maxScore;
    }

    public int getScore() {
        return score;
    }

    @NonNull
    public String getPlayer() {
        return player;
    }

    @NonNull
    public Calendar getDate() {
        return date;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setMaxScore(int maxScore) {
        this.maxScore = maxScore;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setPlayer(@NonNull String player) {
        this.player = player;
    }

    public void setDate(@NonNull Calendar date) {
        this.date = date;
    }

    @NonNull
    @Override
    public String toString() {
        return player + " - " + score + "/" + maxScore + "\n"
                + date.get(Calendar.DAY_OF_MONTH) + "." + date.get(Calendar.MONTH) + "." + date.get(Calendar.YEAR);
    }
}
