package fr.stanyslasbres.picturetags;

public interface AsyncTaskResponseListener<T> {
    void onAsyncTaskDone(T response);
}
