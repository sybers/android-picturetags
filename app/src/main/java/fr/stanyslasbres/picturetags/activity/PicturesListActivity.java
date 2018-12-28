package fr.stanyslasbres.picturetags.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import com.github.clans.fab.FloatingActionButton;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import fr.stanyslasbres.picturetags.AsyncTaskResponseListener;
import fr.stanyslasbres.picturetags.R;
import fr.stanyslasbres.picturetags.PictureTagsApplication;
import fr.stanyslasbres.picturetags.adapters.PicturesAdapter;
import fr.stanyslasbres.picturetags.persistence.entities.Picture;

/**
 * PicturesListActivity presents the list of annotated pictures and allows the user to pick and annotate a picture
 */
public final class PicturesListActivity extends AppCompatActivity {
    private static final int RESULT_PICK_IMAGE_TO_ANNOTATE = 0;
    private static final int RESULT_IMAGE_ANNOTATED = 1;

    public static final String EXTRA_IMAGE_URI = "fr.stanyslasbres.picturetags.EXTRA_IMAGE_URI";
    private PicturesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pictures_list);

        FloatingActionButton fabPickImage = findViewById(R.id.fab_pick_image);
        fabPickImage.setOnClickListener(view -> pickImageToAnnotate());

        // create adapter and attach it to the recyclerView
        adapter = new PicturesAdapter();
        adapter.setOnItemClickListener((view, position, vm) -> goToAnnotationActivity(vm.uri));
        adapter.setOnItemLongClickListener((view, position, vm) -> deleteAnnotatedPicture(vm));
        RecyclerView eventList = findViewById(R.id.pictures_list);
        eventList.setAdapter(adapter);
        eventList.setLayoutManager(new GridLayoutManager(this, 2));

        // load pictures
        new LoadImagesTask(pictures -> adapter.setData(pictures)).execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case RESULT_PICK_IMAGE_TO_ANNOTATE:
                onImageToAnnotatePicked(resultCode, data);
                break;
            case RESULT_IMAGE_ANNOTATED:
                onImageAnnotated(resultCode, data);
        }
    }

    /**
     * Start an intent to pick an image from the filesystem
     */
    private void pickImageToAnnotate() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        startActivityForResult(intent, RESULT_PICK_IMAGE_TO_ANNOTATE);
    }

    /**
     * Start annotating the image !
     * @param uri image Uri
     */
    private void goToAnnotationActivity(Uri uri) {
        Intent intent = new Intent(this, TagImageActivity.class);
        intent.putExtra(EXTRA_IMAGE_URI, uri.toString());

        // start the image annotation UI
        startActivityForResult(intent, RESULT_IMAGE_ANNOTATED);
    }

    /**
     * Delete the annotated picture from the
     * @param picture picture to delete
     */
    private void deleteAnnotatedPicture(Picture picture) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert
                .setTitle("Delete this annotated picture?")
                .setMessage("Are you sure you want to delete this picture?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Yes", (dialog, which) -> {
                    new DeleteImageTask(response -> {
                        adapter.getData().remove(picture);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(this, "Picture deleted", Toast.LENGTH_LONG).show();
                    })
                            .execute(picture);
                })
                .setNegativeButton("No", (dialog, which) -> dialog.cancel());

        alert.show();
    }

    /**
     * When the user picked an image, just redirect him to the annotation UI or show a message if something went wrong
     * @param resultCode Activity result code
     * @param data Data containing the picked image URI
     */
    private void onImageToAnnotatePicked(int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK) {
            if(data != null && data.getData() != null) {
                // ask the content resolver to keep permission to read this Uri across device reboots
                getContentResolver().takePersistableUriPermission(data.getData(), Intent.FLAG_GRANT_READ_URI_PERMISSION);

                // start annotation activity
                goToAnnotationActivity(data.getData());
            } else {
                Toast.makeText(this.getApplicationContext(), "Unable to pick the image...", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * After user annotated the image, this methods gets called.
     * @param resultCode Activity result code
     * @param data Data from called activity
     */
    private void onImageAnnotated(int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK) {
            // reload the pictures
            // TODO : use observable data to avoid this kind of code duplication...
            new LoadImagesTask(pictures -> adapter.setData(pictures)).execute();
        }
    }

    /**
     * Load the list of images from the local database
     */
    private static class LoadImagesTask extends AsyncTask<Void, Void, List<Picture>> {

        private AsyncTaskResponseListener<List<Picture>> responseListener;

        LoadImagesTask(AsyncTaskResponseListener<List<Picture>> listener) {
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

    /**
     * delete Pictures from the database
     */
    private static class DeleteImageTask extends AsyncTask<Picture, Void, Void> {
        private AsyncTaskResponseListener<Void> responseListener;

        DeleteImageTask(AsyncTaskResponseListener<Void> listener) {
            super();
            this.responseListener = listener;
        }

        @Override
        protected Void doInBackground(Picture... picture) {
            try {
                PictureTagsApplication.getDatabase().getPicturesDao().delete(picture);
            } catch(Exception e) {
                Log.e("PicturesListActivity", "Unable to delete the picture from room database...");
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void params) {
            super.onPostExecute(params);

            if (this.responseListener != null) {
                this.responseListener.onAsyncTaskDone(params);
            }
        }
    }
}