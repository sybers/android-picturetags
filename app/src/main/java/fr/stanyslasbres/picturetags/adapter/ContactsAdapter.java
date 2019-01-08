package fr.stanyslasbres.picturetags.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import fr.stanyslasbres.picturetags.R;
import fr.stanyslasbres.picturetags.viewmodel.ContactViewModel;

public final class ContactsAdapter extends SimpleListAdapter<ContactViewModel, ContactsAdapter.ContactViewHolder> {

    class ContactViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final SimpleListAdapter.OnItemClickListener<ContactViewModel> onItemClickListener;
        private final TextView displayName;

        ContactViewHolder(@NonNull View view, SimpleListAdapter.OnItemClickListener<ContactViewModel> onItemClickListener) {
            super(view);
            itemView.setOnClickListener(this);
            this.onItemClickListener = onItemClickListener;

            displayName = view.findViewById(R.id.contact_display_name);
        }

        @Override
        public void onClick(View view) {
            if(getAdapterPosition() == RecyclerView.NO_POSITION) {
                return;
            }

            ContactViewModel vm = data.get(getAdapterPosition());

            if(onItemClickListener != null) {
                onItemClickListener.onItemClick(view, getAdapterPosition(), vm);
            }
        }
    }

    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType, SimpleListAdapter.OnItemClickListener<ContactViewModel> onItemClickListener) {
        return new ContactViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_item_contact, parent, false),
                onItemClickListener
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, ContactViewModel vm, int position) {
        if(vm == null) return;

        // set the event title
        holder.displayName.setText(vm.getDisplayName());
    }
}