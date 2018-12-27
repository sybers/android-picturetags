package fr.stanyslasbres.picturetags.adapters;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import fr.stanyslasbres.picturetags.R;
import fr.stanyslasbres.picturetags.viewmodel.ContactViewModel;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.EventViewHolder> {
    public interface OnItemClickListener {
        void onItemClick(View view, int position, ContactViewModel vm);
    }

    class EventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView displayName;

        EventViewHolder(@NonNull View view) {
            super(view);
            view.setOnClickListener(this);

            displayName = view.findViewById(R.id.contact_display_name);
        }

        @Override
        public void onClick(View view) {
            if(getAdapterPosition() == RecyclerView.NO_POSITION) {
                return;
            }

            notifyItemChanged(selectedItemPosition);
            selectedItemPosition = getAdapterPosition();
            notifyItemChanged(selectedItemPosition);

            ContactViewModel vm = data.get(getAdapterPosition());


            if(onItemClickListener != null) {
                onItemClickListener.onItemClick(view, getAdapterPosition(), vm);
            }
        }
    }

    private List<ContactViewModel> data;
    private OnItemClickListener onItemClickListener;
    private int selectedItemPosition = RecyclerView.NO_POSITION;

    /**
     * Attach the item click listener to the list
     * @param listener listener
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    /**
     * Set the current data for the Adapter
     * @param data {@link List<ContactViewModel>} data list
     */
    public void setData(List<ContactViewModel> data) {
        this.data = data;

        // reset selected position and notify data changed
        this.selectedItemPosition = RecyclerView.NO_POSITION;
        this.notifyDataSetChanged();
    }

    /**
     * Get the {@link Cursor} data object
     * @return Cursor data
     */
    public List<ContactViewModel> getData() {
        return this.data;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new EventViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_item_contact, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        if (data == null || data.size() == 0) {
            return;
        }

        ContactViewModel vm = data.get(position);

        // set the event title
        holder.displayName.setText(vm.getDisplayName());
    }

    @Override
    public int getItemCount() {
        if(data == null) {
            return 0;
        }

        return data.size();
    }
}