package fr.stanyslasbres.picturetags.eventpicker;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.CalendarContract;
import android.support.v4.content.ContextCompat;

import java.util.Calendar;

public class CalendarEventsReader {
    public static final String[] DEFAULT_PROJECTION = new String[]{
            CalendarContract.Events._ID,
            CalendarContract.Events.TITLE,
            CalendarContract.Events.DTSTART,
            CalendarContract.Events.DTEND,
    };

    private String[] projection = DEFAULT_PROJECTION;

    private Context context;

    /**
     * CalendarEventsReader Constructor.
     * @param context {@link Context} to use
     */
    public CalendarEventsReader(Context context) {
        this.context = context;
    }

    /**
     * Set the projection to use to retrieve data from the content resolver.
     * @param projection Projection
     */
    public void setProjection(String[] projection) {
        this.projection = projection;
    }

    /**
     * Get the current projection used by the reader
     * @return Current projection
     */
    public String[] getProjection() {
        return this.projection;
    }

    /**
     * Retrieve a cursor with all the events for a day.
     * @param year Year
     * @param month Month
     * @param day Day of month
     * @return Cursor with the events or null if the permission is not granted
     */
    public Cursor readEventsForDay(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        return readEventsForDay(cal.getTimeInMillis());
    }

    /**
     * Retrieve a cursor with all the events for a day.
     * @param timestamp Timestamp
     * @return Cursor with the events or null if the permission is not granted
     */
    public Cursor readEventsForDay(long timestamp) {
        if (!(ContextCompat.checkSelfPermission(context,  Manifest.permission.READ_CALENDAR)  == PackageManager.PERMISSION_GRANTED)) {
            return null;
        }

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timestamp);

        String dayStart = String.valueOf(c.getTimeInMillis());
        c.add(Calendar.DATE,1);
        String dayEnd = String.valueOf(c.getTimeInMillis());

        // query all the events for the day
        String queryString = String.format("%s < ? AND %s > ?", CalendarContract.Events.DTSTART, CalendarContract.Events.DTEND);
        String[] queryArgs = new String[]{dayEnd, dayStart};

        return context.getContentResolver().query(CalendarContract.Events.CONTENT_URI, projection, queryString, queryArgs, null);
    }
}
