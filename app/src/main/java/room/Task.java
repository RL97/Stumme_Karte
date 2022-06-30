package room;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

// entity of task relation
// @PrimaryKey annotation with argument autogenerate = true ensures its automatic generation
// consists of:
// id       ; primary key
// location ; name of the location
// x        ; percentage based position of location on the x-axis
// y        ; percentage based position of location on the y-axis
// points   ; points awarded to the user on correctly guessing the locations position
@Entity(tableName = "tasks")
public class Task {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private String location;

    @NonNull
    private int x;

    @NonNull
    private int y;

    private int points;

    public Task(@NonNull String location, int x, int y, int points) {
        this.location = location;
        this.x = x;
        this.y = y;
        this.points = points;
    }

    public int getId() {
        return id;
    }

    @NonNull
    public String getLocation() {
        return location;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getPoints() {
        return points;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLocation(@NonNull String location) {
        this.location = location;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
