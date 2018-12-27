package fr.stanyslasbres.picturetags.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fr.stanyslasbres.picturetags.AsyncTaskResponseListener;
import fr.stanyslasbres.picturetags.PictureTagsApplication;
import fr.stanyslasbres.picturetags.R;
import fr.stanyslasbres.picturetags.adapters.EventsAdapter;
import fr.stanyslasbres.picturetags.fragment.ContactListFragment;
import fr.stanyslasbres.picturetags.fragment.EventListFragment;
import fr.stanyslasbres.picturetags.persistence.dao.PicturesDao;
import fr.stanyslasbres.picturetags.persistence.entities.Picture;
import fr.stanyslasbres.picturetags.readers.CalendarEventsReader;
import fr.stanyslasbres.picturetags.viewmodel.EventViewModel;

public final class TagImageActivity extends AppCompatActivity {
    private static final int PICK_EVENT = 0;
    private static final int PICK_CONTACT = 1;

    private ViewPager pager;

    private Set<Long> pickedEventsIds = new HashSet<>();

    private EventsAdapter eventsAdapter;
    private CalendarEventsReader eventsReader;

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

            setResult(Activity.RESULT_OK);
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

        // create the pager
        pager = findViewById(R.id.tag_pager);
        TagPicturePagerAdapter pagerAdapter = new TagPicturePagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);

        // create adapter and eventsReader
        eventsAdapter = pagerAdapter.eventListFragment.getAdapter();
        eventsReader = new CalendarEventsReader(this);

        // display the events related to the picture
        List<EventViewModel> events = eventsReader.readEventsWithIds(new ArrayList<>(pickedEventsIds));
        eventsAdapter.setData(events);

    }

    @Override
    public void onBackPressed() {
        if(pager.getCurrentItem() == 0) {
            showCancelConfirmDialog();
        } else {
            pager.setCurrentItem(pager.getCurrentItem() - 1);
        }
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

    /**
     * Method called when the user picked an event from the event picker
     * @param resultCode Activity result code
     * @param data Intent with extra data for the event
     */
    private void onEventPicked(int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK) {
            if(data != null) {
                long id = data.getLongExtra(EventPickerActivity.EXTRA_SELECTED_EVENT_ID, -1);
                Toast.makeText(this, "Event added", Toast.LENGTH_SHORT).show();
                pickedEventsIds.add(id);

                List<EventViewModel> events = eventsReader.readEventsWithIds(new ArrayList<>(pickedEventsIds));
                eventsAdapter.setData(events);
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
                Toast.makeText(this, "Event added", Toast.LENGTH_SHORT).show();
                // TODO : add picked contact to a set
            }
        }
    }

    /**
     * Show the user a confirmation dialog to avoid quiting with unsaved changes
     */
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

    /**
     * Adapter class for the viewpager displaying the list of events and contacts
     */
    private class TagPicturePagerAdapter extends FragmentPagerAdapter {
        private EventListFragment eventListFragment;
        private ContactListFragment contactListFragment;

        public TagPicturePagerAdapter(FragmentManager fm) {
            super(fm);

            eventListFragment = EventListFragment.newInstance();
            contactListFragment = ContactListFragment.newInstance();
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: return eventListFragment;
                case 1: return contactListFragment;
                default: return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
