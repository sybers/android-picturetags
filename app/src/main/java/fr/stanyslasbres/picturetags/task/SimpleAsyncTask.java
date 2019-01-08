package fr.stanyslasbres.picturetags.task;

import android.os.AsyncTask;

/**
 * @author Stanyslas Bres <stanyslas.bres@gmail.com>
 * @version 1.0.0
 * A simple async task that takes no parameters and returns nothing.
 * Can be initialized and executed in the same time.
 */
public class SimpleAsyncTask extends AsyncTask<Void, Void, Void> {
    private BackgroundJobTask job;
    private ResponseListener responseListener;

    public SimpleAsyncTask() {}

    public SimpleAsyncTask(BackgroundJobTask job) {
        this.job = job;
    }

    public void setBackgroundTask(BackgroundJobTask job) {
        this.job = job;
    }

    public void setResponseListener(ResponseListener listener) {
        this.responseListener = null;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        if(this.job != null) {
            this.job.doInBackground();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if(this.responseListener != null) {
            this.responseListener.onAsyncTaskDone();
        }
    }

    public interface BackgroundJobTask {
        void doInBackground();
    }

    public interface ResponseListener {
        void onAsyncTaskDone();
    }
}
