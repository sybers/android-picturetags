package fr.stanyslasbres.picturetags.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import fr.stanyslasbres.picturetags.R;
import fr.stanyslasbres.picturetags.persistence.entities.Picture;

public class PicturesAdapter extends RecyclerView.Adapter<PicturesAdapter.PictureViewHolder> {

    class PictureViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;

        PictureViewHolder(@NonNull View view) {
            super(view);
            imageView = view.findViewById(R.id.picture_image_view);
        }
    }

    private List<Picture> data;
    private Context context;

    public PicturesAdapter(Context context) {
        this.context = context;
    }

    /**
     * Set the current data for the Adapter
     * @param data {@link List<Picture>} data pictures list
     */
    public void setData(List<Picture> data) {
        this.data = data;
        this.notifyDataSetChanged();
    }

    /**
     * Get the {@link Cursor} data object
     * @return Cursor data
     */
    public List<Picture> getData() {
        return this.data;
    }

    @NonNull
    @Override
    public PictureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PictureViewHolder(
                LayoutInflater.from(context).inflate(R.layout.adapter_item_picture, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull PictureViewHolder holder, int position) {
        if (data != null) {
            holder.imageView.setImageURI(data.get(position).uri);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}