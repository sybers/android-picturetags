package fr.stanyslasbres.picturetags.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import com.github.clans.fab.FloatingActionButton;
import com.squareup.picasso.Picasso;

import fr.stanyslasbres.picturetags.AsyncTaskResponseListener;
import fr.stanyslasbres.picturetags.PictureTagsApplication;
import fr.stanyslasbres.picturetags.R;
import fr.stanyslasbres.picturetags.persistence.dao.PicturesDao;
import fr.stanyslasbres.picturetags.persistence.entities.Picture;

public final class TagImageActivity extends AppCompatActivity {
    private static final int PICK_EVENT = 0;
    private static final int PICK_CONTACT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_image);

        String imageUriString = getIntent().getStringExtra(PicturesListActivity.EXTRA_IMAGE_URI);

        ImageView annotatedPictureImageView = findViewById(R.id.annotated_image_view);

        // load image with picasso
        Picasso.get()
                .load(Uri.parse(imageUriString))
                .fit()
                .centerCrop()
                .into(annotatedPictureImageView);

        // change scale type on image click (to view the full image)
        annotatedPictureImageView.setOnClickListener(view -> {
            annotatedPictureImageView.setScaleType(annotatedPictureImageView.getScaleType() == ImageView.ScaleType.CENTER_CROP
                    ? ImageView.ScaleType.CENTER_INSIDE
                    : ImageView.ScaleType.CENTER_CROP);
        });

        // save button
        Button saveButton = findViewById(R.id.annotated_image_save_button);
        saveButton.setOnClickListener(view -> {
            Picture pic = new Picture(Uri.parse(imageUriString));

            SaveImageTask task = new SaveImageTask();
            task.execute(pic);

            finish();
        });

        // cancel button
        Button cancelButton = findViewById(R.id.annotated_image_cancel_button);
        cancelButton.setOnClickListener(view -> showCancelConfirmDialog());

        // pick event FAB
        FloatingActionButton fabPickEvent = findViewById(R.id.fab_pick_event);
        fabPickEvent.setOnClickListener(view -> pickEvent());

        // pick contact FAB
        FloatingActionButton fabPickContact = findViewById(R.id.fab_pick_contact);
        fabPickContact.setOnClickListener(view -> pickContact());
    }

    /**
     * Start the activity to pick an event from the calendar
     */
    private void pickEvent() {
        Intent intent = new Intent(this, EventPickerActivity.class);
        startActivityForResult(intent, PICK_EVENT);
    }

    /**
     * Start the activity to pick a contact from the phone
     */
    private void pickContact() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
        startActivityForResult(intent, PICK_CONTACT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case PICK_EVENT:
                onEventPicked(resultCode, data);
                break;
            case PICK_CONTACT:
                onContactPicked(resultCode, data);
                break;
        }
    }

    /**
     * Method called when the user picked an event from the event picker
     * @param resultCode Activity result code
     * @param data Intent with extra data for the event
     */
    private void onEventPicked(int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK) {
            if(data != null) {
                long id = data.getLongExtra(EventPickerActivity.EXTRA_SELECTED_EVENT_ID, -1);
                new AlertDialog.Builder(this).setTitle("RESULT").setMessage("" + id).show();
            }
        }
    }

    /**
     * Method called when the user picked a contact
     * @param resultCode Activity result code
     * @param data Intent with extra data for the event
     */
    private void onContactPicked(int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK) {
            if(data != null) {
                long id = data.getLongExtra(EventPickerActivity.EXTRA_SELECTED_EVENT_ID, -1);
                new AlertDialog.Builder(this).setTitle("RESULT").setMessage("" + id).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        showCancelConfirmDialog();
    }

    private void showCancelConfirmDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert
                .setTitle("Wait a minute!")
                .setMessage("The annotations are not yet saved! are you sure you want to cancel?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Yes", (dialog, which) -> finish())
                .setNegativeButton("No", (dialog, which) -> dialog.cancel());

        alert.show();
    }

    /**
     * Load the list of images from the local database
     */
    private static class SaveImageTask extends AsyncTask<Picture, Void, Void> {

        private AsyncTaskResponseListener<Void> responseListener;

        SaveImageTask() {
            super();
        }

        SaveImageTask(AsyncTaskResponseListener<Void> listener) {
            super();
            this.responseListener = listener;
        }

        @Override
        protected Void doInBackground(Picture... pictures) {
            PicturesDao dao = PictureTagsApplication.getDatabase().getPicturesDao();
            dao.insert(pictures);

            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
            super.onPostExecute(param);

            if (this.responseListener != null) {
                this.responseListener.onAsyncTaskDone(null);
            }
        }
    }
}
