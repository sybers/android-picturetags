package fr.stanyslasbres.picturetags.reader;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.CalendarContract;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import fr.stanyslasbres.picturetags.viewmodel.EventViewModel;

public class CalendarEventsReader {
    private static final String[] EVENT_PROJECTION = new String[]{
            CalendarContract.Events._ID,
            CalendarContract.Events.TITLE,
            CalendarContract.Events.DTSTART,
            CalendarContract.Events.DTEND,
            CalendarContract.Events.CALENDAR_COLOR,
    };

    private Context context;

    /**
     * CalendarEventsReader Constructor.
     * @param context {@link Context} to use
     */
    public CalendarEventsReader(Context context) {
        this.context = context;
    }

    /**
     * Retrieve a cursor with all the events for a day.
     * @param cal Calendar with the day to use
     * @return Cursor with the events or null if the permission is not granted
     */
    @SuppressLint("MissingPermission")
    public List<EventViewModel> readEventsForDay(Calendar cal) {
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

        Cursor data = context.getContentResolver().query(CalendarContract.Events.CONTENT_URI, EVENT_PROJECTION, queryString, queryArgs, null);
        return inflateFromCursor(data);
    }

    /**
     * Read the events with the given Ids
     * @param eventsIds ids of the events to query
     * @return Cursor with the matching events
     */
    @SuppressLint("MissingPermission")
    public List<EventViewModel> readEventsWithIds(List<Long> eventsIds) {
        if (!hasCalendarPermission() || eventsIds == null || eventsIds.size() == 0) {
            return null;
        }

        String queryString = String.format("%s IN (" + makeInPlaceholders(eventsIds.size()) + ")", CalendarContract.Events._ID);

        // convert query args to string
        String[] queryArgs = new String[eventsIds.size()];
        for (int i = 0; i < eventsIds.size(); i++) {
            queryArgs[i] = String.valueOf(eventsIds.get(i));
        }

        Cursor data = context.getContentResolver().query(CalendarContract.Events.CONTENT_URI, EVENT_PROJECTION, queryString, queryArgs, null);
        return inflateFromCursor(data);
    }

    /**
     * Generate a list of inflated view models from the cursor
     * @param data Cursor with events data
     * @return inflated list
     */
    private List<EventViewModel> inflateFromCursor(Cursor data) {
        List<EventViewModel> events = new ArrayList<>();

        // return an empty list of the cursor is null
        if(data == null) return null;

        for(data.moveToFirst(); !data.isAfterLast(); data.moveToNext()) {
            long id = data.getLong(data.getColumnIndex(CalendarContract.Events._ID));
            EventViewModel vm = new EventViewModel(id);

            String title = data.getString(data.getColumnIndex(CalendarContract.Events.TITLE));
            vm.setTitle(title);

            long eventStart = data.getLong(data.getColumnIndex(CalendarContract.Events.DTSTART));
            vm.setStartDate(eventStart);

            long eventEnd = data.getLong(data.getColumnIndex(CalendarContract.Events.DTEND));
            vm.setEndDate(eventEnd);

            int calendarColor = data.getInt(data.getColumnIndex(CalendarContract.Events.CALENDAR_COLOR));
            vm.setCalendarColor(calendarColor);

            events.add(vm);
        }

        return events;
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
