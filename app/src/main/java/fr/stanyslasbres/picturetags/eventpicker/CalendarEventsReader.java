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
     * @param cal Calendar with the day to use
     * @return Cursor with the events or null if the permission is not granted
     */
    public Cursor readEventsForDay(Calendar cal) {
        if (!(ContextCompat.checkSelfPermission(context,  Manifest.permission.READ_CALENDAR)  == PackageManager.PERMISSION_GRANTED)) {
            return null;
        }

        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);

        String dayStart = String.valueOf(cal.getTimeInMillis());
        cal.add(Calendar.DATE,1);
        String dayEnd = String.valueOf(cal.getTimeInMillis());

        // query all the events for the day
        String queryString = String.format("%s < ? AND %s > ?", CalendarContract.Events.DTSTART, CalendarContract.Events.DTEND);
        String[] queryArgs = new String[]{dayEnd, dayStart};

        return context.getContentResolver().query(CalendarContract.Events.CONTENT_URI, projection, queryString, queryArgs, null);
    }
}
