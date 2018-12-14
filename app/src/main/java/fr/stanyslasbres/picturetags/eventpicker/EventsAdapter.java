package fr.stanyslasbres.picturetags.eventpicker;

import android.content.Context;
import android.database.Cursor;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Calendar;

import javax.annotation.Nonnull;

import fr.stanyslasbres.picturetags.R;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventViewHolder> {
    public static final String[] CALENDAR_PROJECTION = new String[]{
            CalendarContract.Events._ID,
            CalendarContract.Events.TITLE,
            CalendarContract.Events.DTSTART,
            CalendarContract.Events.DTEND,
    };

    class EventViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final TextView start;
        private final TextView end;

        EventViewHolder(@Nonnull View view) {
            super(view);
            title = view.findViewById(R.id.event_title);
            start = view.findViewById(R.id.event_start);
            end = view.findViewById(R.id.event_end);
        }
    }

    private Cursor data;
    private Context context;

    public EventsAdapter(Context context) {
        this.context = context;
    }

    /**
     * Set the current data for the Adapter
     * @param data {@link Cursor} data cursor object
     */
    public void setData(Cursor data) {
        this.data = data;
        this.notifyDataSetChanged();
    }

    /**
     * Get the {@link Cursor} data object
     * @return Cursor data
     */
    public Cursor getData() {
        return this.data;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new EventViewHolder(
                LayoutInflater.from(context).inflate(R.layout.event_item, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        if (data == null || data.getCount() == 0) {
            holder.title.setText("No Event");
            holder.start.setText("");
            holder.end.setText("");
        } else {
            data.moveToPosition(position);

            // set the event title
            holder.title.setText(data.getString(data.getColumnIndex(CalendarContract.Events.TITLE)));

            // set the start date
            long eventStart = data.getLong(data.getColumnIndex(CalendarContract.Events.DTSTART));
            holder.start.setText(timestampToFormattedString(eventStart));

            // set the end date
            long eventEnd = data.getLong(data.getColumnIndex(CalendarContract.Events.DTEND));
            holder.end.setText(timestampToFormattedString(eventEnd));
        }
    }

    @Override
    public int getItemCount() {
        if(data == null || data.getCount() == 0) {
            return 1;
        } else {
            return data.getCount();
        }
    }

    /**
     * Get a human readable date representation for the given timestamp
     * @param timestamp timestamp
     * @return string representation of the given timestamp
     */
    private String timestampToFormattedString(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        DateFormat dateFormat = DateFormat.getInstance();

        calendar.setTimeInMillis(timestamp);
        return dateFormat.format(calendar.getTime());
    }
}