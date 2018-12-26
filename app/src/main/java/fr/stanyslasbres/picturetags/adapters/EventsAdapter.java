package fr.stanyslasbres.picturetags.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.provider.CalendarContract;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Calendar;

import fr.stanyslasbres.picturetags.R;
import fr.stanyslasbres.picturetags.activity.EventPickerActivity;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventViewHolder> {
    public static final String[] CALENDAR_PROJECTION = new String[]{
            CalendarContract.Events._ID,
            CalendarContract.Events.TITLE,
            CalendarContract.Events.DTSTART,
            CalendarContract.Events.DTEND,
            CalendarContract.Events.CALENDAR_COLOR,
    };

    class EventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView title;
        private final TextView start;
        private final TextView end;
        private final GradientDrawable background;

        EventViewHolder(@NonNull View view) {
            super(view);
            view.setOnClickListener(this);

            title = view.findViewById(R.id.event_title);
            start = view.findViewById(R.id.event_start);
            end = view.findViewById(R.id.event_end);
            background = (GradientDrawable) ContextCompat.getDrawable(context, R.drawable.adapter_item_event_bg);
        }

        @Override
        public void onClick(View view) {
            if(getAdapterPosition() == RecyclerView.NO_POSITION || title.getText() == "No Event") {
                return;
            }

            notifyItemChanged(selectedItemPosition);
            selectedItemPosition = getAdapterPosition();
            notifyItemChanged(selectedItemPosition);

            data.moveToPosition(getAdapterPosition());

            // TODO : dirty way to access the activity from adapter...
            Intent resultIntent = new Intent();
            resultIntent.putExtra(EventPickerActivity.EXTRA_SELECTED_EVENT_ID, data.getLong(data.getColumnIndex(CalendarContract.Events._ID)));

            if(context instanceof Activity) {
                ((Activity) context).setResult(Activity.RESULT_OK, resultIntent);
                ((Activity) context).finish();
            }
        }
    }

    private Cursor data;
    private Context context;
    private int selectedItemPosition = RecyclerView.NO_POSITION;

    public EventsAdapter(Context context) {
        this.context = context;
    }

    /**
     * Set the current data for the Adapter
     * @param data {@link Cursor} data cursor object
     */
    public void setData(Cursor data) {
        this.data = data;

        // reset selected position and notify data changed
        this.selectedItemPosition = RecyclerView.NO_POSITION;
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
                LayoutInflater.from(context).inflate(R.layout.adapter_item_event, parent, false)
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

            // set background color according to calendar color
            int calendarColor = data.getInt(data.getColumnIndex(CalendarContract.Events.CALENDAR_COLOR));
            if (holder.background != null) {
                if(position == selectedItemPosition) {
                    calendarColor = darkenColorForSelection(calendarColor);
                }
                holder.background.setColor(calendarColor);
                holder.itemView.setBackground(holder.background);
            }

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

    /**
     * Darkens the given color, used to emphasis selection of the current event
     * @param color initial color
     * @return darkened color
     */
    private @ColorInt int darkenColorForSelection(@ColorInt int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 1 - 0.2;
        return Color.HSVToColor(hsv);
    }
}