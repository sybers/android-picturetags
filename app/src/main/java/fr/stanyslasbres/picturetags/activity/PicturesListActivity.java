package fr.stanyslasbres.picturetags.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import com.github.clans.fab.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.util.List;

import fr.stanyslasbres.picturetags.PictureTagsApplication;
import fr.stanyslasbres.picturetags.R;
import fr.stanyslasbres.picturetags.adapters.EventsAdapter;
import fr.stanyslasbres.picturetags.adapters.PicturesAdapter;
import fr.stanyslasbres.picturetags.persistence.entities.Picture;
import fr.stanyslasbres.picturetags.readers.CalendarEventsReader;

/**
 * PicturesListActivity presents the list of annotated pictures and allows the user to pick and annotate a picture
 */
public final class PicturesListActivity extends AppCompatActivity implements LoadImagesTask.AsyncTaskResponseListener {
    private static final int PICK_IMAGE_TO_ANNOTATE = 0;

    private LoadImagesTask loadPicturesTask = new LoadImagesTask(this);
    private PicturesAdapter adapter;

    private List<Picture> pictures;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pictures_list);

        FloatingActionButton fabPickImage = findViewById(R.id.fab_pick_image);
        fabPickImage.setOnClickListener(view -> pickImageToAnnotate());

        // create adapter and attach it to the recyclerView
        adapter = new PicturesAdapter(this);
        RecyclerView eventList = findViewById(R.id.pictures_list);
        eventList.setAdapter(adapter);
        eventList.setLayoutManager(new GridLayoutManager(this, 2));

        // load pictures
        this.loadPicturesTask.execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case PICK_IMAGE_TO_ANNOTATE:
                onImageToAnnotatePicked(resultCode, data);
                break;
        }
    }

    @Override
    public void onAsyncTaskDone(List<Picture> pictures) {
        if(pictures != null) {
            this.pictures = pictures;
            adapter.setData(pictures);
        }
    }

    private void pickImageToAnnotate() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_TO_ANNOTATE);
    }

    private void onImageToAnnotatePicked(int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK) {
            if(data != null) {
                Toast.makeText(this.getApplicationContext(), "Good !", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this.getApplicationContext(), "Unable to pick the image...", Toast.LENGTH_LONG).show();
            }
        }
    }
}

/**
 * Load the list of images from the local database
 */
class LoadImagesTask extends AsyncTask<Void, Void, List<Picture>> {
    public interface AsyncTaskResponseListener {
        void onAsyncTaskDone(List<Picture> response);
    }

    private AsyncTaskResponseListener responseListener;

    LoadImagesTask(AsyncTaskResponseListener listener) {
        super();
        this.responseListener = listener;
    }

    @Override
    protected List<Picture> doInBackground(Void... params) {
        return PictureTagsApplication.getDatabase().getPicturesDao().all();
    }

    @Override
    protected void onPostExecute(List<Picture> pictures) {
        super.onPostExecute(pictures);

        if (this.responseListener != null) {
            this.responseListener.onAsyncTaskDone(pictures);
        }
    }
}