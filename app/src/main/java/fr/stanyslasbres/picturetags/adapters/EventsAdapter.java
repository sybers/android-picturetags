package fr.stanyslasbres.picturetags.adapters;

import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import fr.stanyslasbres.picturetags.R;
import fr.stanyslasbres.picturetags.viewmodel.EventViewModel;

public final class EventsAdapter extends SimpleListAdapter<EventViewModel, EventsAdapter.EventViewHolder> {
    class EventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final SimpleListAdapter.OnItemClickListener<EventViewModel> onItemClickListener;
        private final TextView title;
        private final TextView start;
        private final TextView end;
        private GradientDrawable background;

        EventViewHolder(@NonNull View view, SimpleListAdapter.OnItemClickListener<EventViewModel> onItemClickListener) {
            super(view);
            itemView.setOnClickListener(this);

            this.onItemClickListener = onItemClickListener;

            title = view.findViewById(R.id.event_title);
            start = view.findViewById(R.id.event_start);
            end = view.findViewById(R.id.event_end);
        }

        @Override
        public void onClick(View view) {
            if(getAdapterPosition() == RecyclerView.NO_POSITION) {
                return;
            }

            EventViewModel vm = data.get(getAdapterPosition());

            if(onItemClickListener != null) {
                onItemClickListener.onItemClick(view, getAdapterPosition(), vm);
            }
        }
    }

    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType, SimpleListAdapter.OnItemClickListener<EventViewModel> onItemClickListener) {
        EventViewHolder viewHolder = new EventViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_item_event, parent, false),
                onItemClickListener
        );

        viewHolder.background = (GradientDrawable) ContextCompat.getDrawable(parent.getContext(), R.drawable.adapter_item_event_bg);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, EventViewModel vm, int position) {
        if(vm == null) return;

        if (holder.background != null) {
            holder.background.setColor(vm.getCalendarColor());
            holder.itemView.setBackground(holder.background);
        }

        // set the event title
        holder.title.setText(vm.getTitle());

        // set the start date
        holder.start.setText(vm.getStartDateFormatted());

        // set the end date
        holder.end.setText(vm.getEndDateFormatted());
    }
}