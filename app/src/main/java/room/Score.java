package room;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Calendar;

@Entity(tableName = "scores")
public class Score {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    private int id;

    private int maxScore = 10;

    @NonNull
    private int score;

    @NonNull
    private String player;

    @NonNull
    private Calendar date;

    public Score(int score, @NonNull String player, @NonNull Calendar date) {
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
}
