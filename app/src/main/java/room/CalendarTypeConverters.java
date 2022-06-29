package room;

import androidx.room.TypeConverter;

import java.util.Calendar;

public class CalendarTypeConverters {
    @TypeConverter
    public static Calendar fromTimestamp(Long value) {
        Calendar c = Calendar.getInstance();
        if (value != null) {
            c.setTimeInMillis(value);
            return c;
        }
        return null;
    }

    @TypeConverter
    public static Long calendarToTimestamp(Calendar date) {
        return date == null ? null : date.getTimeInMillis();
    }
}
