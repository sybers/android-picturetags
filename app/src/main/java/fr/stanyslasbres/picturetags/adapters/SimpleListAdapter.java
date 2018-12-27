package fr.stanyslasbres.picturetags.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public abstract class SimpleListAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    public interface OnItemClickListener<T> {
        void onItemClick(View view, int position, T vm);
    }

    protected List<T> data;
    protected OnItemClickListener<T> onItemClickListener;

    public abstract VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType, OnItemClickListener<T> onItemClickListener);

    public abstract void onBindViewHolder(@NonNull VH holder, T data, int position);

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return onCreateViewHolder(parent, viewType, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        onBindViewHolder(holder, data != null ? data.get(position) : null, position);
    }

    /**
     * Attach the item click listener to the list
     * @param listener listener
     */
    public void setOnItemClickListener(OnItemClickListener<T> listener) {
        this.onItemClickListener = listener;
    }

    /**
     * Set the current data for the Adapter
     * @param data data list
     */
    public void setData(List<T> data) {
        this.data = data;
        this.notifyDataSetChanged();
    }

    /**
     * Get the current data object
     * @return Cursor data
     */
    public List<T> getData() {
        return this.data;
    }

    public T getItem(int position) {
        return data.get(position);
    }

    @Override
    public int getItemCount() {
        if(data == null) {
            return 0;
        }

        return data.size();
    }
}