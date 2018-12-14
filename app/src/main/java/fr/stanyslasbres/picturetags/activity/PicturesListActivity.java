package fr.stanyslasbres.picturetags.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.List;

import fr.stanyslasbres.picturetags.PictureTagsApplication;
import fr.stanyslasbres.picturetags.R;
import fr.stanyslasbres.picturetags.persistence.entities.Picture;

/**
 * PicturesListActivity presents the list of annotated pictures and allows the user to pick and annotate a picture
 */
public final class PicturesListActivity extends AppCompatActivity implements LoadImagesTask.AsyncTaskResponseListener {
    private static final int PICK_IMAGE_TO_ANNOTATE = 0;
    private static final int PICK_EVENT = 1;

    private ImageView imageView;
    private LoadImagesTask loadPicturesTask = new LoadImagesTask(this);
    private List<Picture> pictures;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pictures_list);

        FloatingActionButton fab = findViewById(R.id.fab_select_image);
        fab.setOnClickListener(view -> pickImageToAnnotate());

        FloatingActionButton fab2 = findViewById(R.id.fab_pick_event);
        fab2.setOnClickListener(view -> pickEvent());

        imageView = findViewById(R.id.image_view);

        // load pictures
        this.loadPicturesTask.execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case PICK_IMAGE_TO_ANNOTATE:
                onAnnotateImagePicked(resultCode, data);
                break;
            case PICK_EVENT:
                onEventPicked(resultCode, data);
                break;
        }
    }

    @Override
    public void onResponse(List<Picture> pictures) {
        this.pictures = pictures;
    }
    private void pickEvent() {
        Intent intent = new Intent(this, EventPickerActivity.class);
        startActivityForResult(intent, PICK_EVENT);
    }

    private void pickImageToAnnotate() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_TO_ANNOTATE);
    }

    private void onAnnotateImagePicked(int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK) {
            if(data != null) {
                imageView.setImageURI(data.getData());
            } else {
                Toast.makeText(this.getApplicationContext(), "Unable to pick the image...", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void onEventPicked(int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK) {
            if(data != null) {
                long id = data.getLongExtra(EventPickerActivity.EXTRA_SELECTED_EVENT_ID, -1);
                new AlertDialog.Builder(this).setTitle("RESULT").setMessage("" + id).show();
            }
        }
    }
}

/**
 * Load the list of images from the local database
 */
class LoadImagesTask extends AsyncTask<Void, Void, List<Picture>> {
    public interface AsyncTaskResponseListener {
        void onResponse(List<Picture> response);
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
            this.responseListener.onResponse(pictures);
        }
    }
}