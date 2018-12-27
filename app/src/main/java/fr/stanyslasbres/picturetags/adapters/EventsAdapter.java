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

import java.util.List;

import fr.stanyslasbres.picturetags.R;
import fr.stanyslasbres.picturetags.activity.EventPickerActivity;
import fr.stanyslasbres.picturetags.viewmodel.EventViewModel;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventViewHolder> {
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
            if(getAdapterPosition() == RecyclerView.NO_POSITION) {
                return;
            }

            notifyItemChanged(selectedItemPosition);
            selectedItemPosition = getAdapterPosition();
            notifyItemChanged(selectedItemPosition);

            EventViewModel vm = data.get(getAdapterPosition());

            // FIXME : dirty way to access the activity from adapter...
            Intent resultIntent = new Intent();
            resultIntent.putExtra(EventPickerActivity.EXTRA_SELECTED_EVENT_ID, vm.getId());

            if(context instanceof Activity) {
                ((Activity) context).setResult(Activity.RESULT_OK, resultIntent);
                ((Activity) context).finish();
            }
        }
    }

    private List<EventViewModel> data;
    private Context context;
    private int selectedItemPosition = RecyclerView.NO_POSITION;

    public EventsAdapter(Context context) {
        this.context = context;
    }

    /**
     * Set the current data for the Adapter
     * @param data {@link List<EventViewModel>} data list
     */
    public void setData(List<EventViewModel> data) {
        this.data = data;

        // reset selected position and notify data changed
        this.selectedItemPosition = RecyclerView.NO_POSITION;
        this.notifyDataSetChanged();
    }

    /**
     * Get the {@link Cursor} data object
     * @return Cursor data
     */
    public List<EventViewModel> getData() {
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
        if (data == null || data.size() == 0) {
            return;
        }

        EventViewModel vm = data.get(position);

        if (holder.background != null) {
            boolean isSelected = position == selectedItemPosition;
            holder.background.setColor(isSelected ? darkenColorForSelection(vm.getCalendarColor()) : vm.getCalendarColor());
            holder.itemView.setBackground(holder.background);
        }

        // set the event title
        holder.title.setText(vm.getTitle());

        // set the start date
        holder.start.setText(vm.getStartDateFormatted());

        // set the end date
        holder.end.setText(vm.getEndDateFormatted());
    }

    @Override
    public int getItemCount() {
        if(data == null) {
            return 0;
        }

        return data.size();
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