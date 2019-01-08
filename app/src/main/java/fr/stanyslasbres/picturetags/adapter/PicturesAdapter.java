package fr.stanyslasbres.picturetags.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import fr.stanyslasbres.picturetags.R;
import fr.stanyslasbres.picturetags.persistence.entities.Picture;
import fr.stanyslasbres.picturetags.widget.SquareImageView;

public final class PicturesAdapter extends SimpleListAdapter<Picture, PicturesAdapter.PictureViewHolder> {
    public interface OnItemLongClickListener<T> {
        void onItemLongClick(View view, int position, T vm);
    }

    class PictureViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private final SimpleListAdapter.OnItemClickListener<Picture> onItemClickListener;
        private final SquareImageView imageView;

        PictureViewHolder(@NonNull View view, SimpleListAdapter.OnItemClickListener<Picture> onItemClickListener) {
            super(view);
            itemView.setOnClickListener(this);

            if(onItemLongClickListener != null) {
                itemView.setOnLongClickListener(this);
            }

            this.onItemClickListener = onItemClickListener;

            imageView = view.findViewById(R.id.picture_image_view);
        }

        @Override
        public void onClick(View view) {
            if(onItemClickListener != null) {
                Picture vm = data.get(getAdapterPosition());
                onItemClickListener.onItemClick(view, getAdapterPosition(), vm);
            }
        }

        @Override
        public boolean onLongClick(View view) {
            Picture vm = data.get(getAdapterPosition());
            if(onItemLongClickListener != null) {
                onItemLongClickListener.onItemLongClick(view, getAdapterPosition(), vm);
            }
            return true;
        }
    }

    private OnItemLongClickListener<Picture> onItemLongClickListener;

    public void setOnItemLongClickListener(OnItemLongClickListener<Picture> onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    @Override
    public PictureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType, OnItemClickListener<Picture> onItemClickListener) {
        return new PictureViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_item_picture, parent, false),
                onItemClickListener
        );
    }

    @Override
    public void onBindViewHolder(@NonNull PictureViewHolder holder, Picture vm, int position) {
        if (vm != null) {
            Picasso
                    .get()
                    .load(vm.uri)
                    .fit()
                    .centerCrop()
                    .into(holder.imageView);
        }
    }
}