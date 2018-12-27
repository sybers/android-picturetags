package fr.stanyslasbres.picturetags.readers;

import android.Manifest;
import android.annotation.SuppressLint;
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
    @SuppressLint("MissingPermission")
    public Cursor readEventsForDay(Calendar cal) {
        if (!hasCalendarPermission()) {
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

    /**
     * Read the events with the given Ids
     * @param eventsIds ids of the events to query
     * @return Cursor with the matching events
     */
    @SuppressLint("MissingPermission")
    public Cursor readEventWithIds(long... eventsIds) {
        if (!hasCalendarPermission()) {
            return null;
        }

        String queryString = String.format("%s IN (" + makeInPlaceholders(eventsIds.length) + ")", CalendarContract.Events._ID);

        // convert query args to string
        String[] queryArgs = new String[eventsIds.length];
        for (int i = 0; i < eventsIds.length; i++) {
            queryArgs[i] = String.valueOf(eventsIds[i]);
        }

        return context.getContentResolver().query(CalendarContract.Events.CONTENT_URI, projection, queryString, queryArgs, null);
    }

    /**
     * Check if the calendar permission is granted
     * @return boolean whether the calendar permission is granted
     */
    private boolean hasCalendarPermission() {
        return ContextCompat.checkSelfPermission(context,  Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Make the placeholders for the `IN` query
     * @param size number of placeholders to generate
     * @return placeholders string
     */
    private String makeInPlaceholders(int size) {
        StringBuilder sb = new StringBuilder(size * 2 - 1);
        sb.append("?");
        for (int i = 1; i < size; i++) sb.append(",?");
        return sb.toString();
    }
}
