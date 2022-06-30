package room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

// dao of task entity
// allows for
// the retrieval of all tasks
// the insertion of a new task (conflict strategy = replace ensures scores with the same primary keys cause override)
@Dao
public interface TaskDAO {
    @Query("SELECT * FROM tasks")
    List<Task> getAllTasks();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long addTask(Task t);
}
