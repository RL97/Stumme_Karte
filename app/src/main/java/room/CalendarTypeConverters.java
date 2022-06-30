package room;

import androidx.room.TypeConverter;

import java.util.Calendar;

// necessary typeconvertes which enable room to convert between
// the date in milliseconds (database)
// and the date as calendar (java)
public class CalendarTypeConverters {
    // converts from timestamp/milliseconds to calendar
    @TypeConverter
    public static Calendar fromTimestamp(Long value) {
        Calendar c = Calendar.getInstance();
        if (value != null) {
            c.setTimeInMillis(value);
            return c;
        }
        return null;
    }

    // converts from calendar to timestamp/milliseconds
    @TypeConverter
    public static Long calendarToTimestamp(Calendar date) {
        return date == null ? null : date.getTimeInMillis();
    }
}
