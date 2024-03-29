package fr.stanyslasbres.picturetags.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import fr.stanyslasbres.picturetags.persistence.repository.PictureRepository;
import fr.stanyslasbres.picturetags.R;
import fr.stanyslasbres.picturetags.adapter.ContactsAdapter;
import fr.stanyslasbres.picturetags.adapter.EventsAdapter;
import fr.stanyslasbres.picturetags.fragment.ContactListFragment;
import fr.stanyslasbres.picturetags.fragment.EventListFragment;
import fr.stanyslasbres.picturetags.persistence.entities.Picture;
import fr.stanyslasbres.picturetags.reader.CalendarEventsReader;
import fr.stanyslasbres.picturetags.reader.ContactsReader;
import fr.stanyslasbres.picturetags.viewmodel.ContactViewModel;

public final class TagImageActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_READ_CONTACTS = 1;
    private static final int PERMISSION_REQUEST_READ_CALENDAR = 2;

    private static final int PICK_EVENT = 0;
    private static final int PICK_CONTACT = 1;

    private PictureRepository pictureRepository = new PictureRepository();

    private FloatingActionButton fabPickEvent;
    private FloatingActionButton fabPickContact;

    private ViewPager pager;

    private Set<Long> pickedEventsIds = new HashSet<>();
    private Set<Long> pickedContactsIds = new HashSet<>();

    private EventsAdapter eventsAdapter;
    private CalendarEventsReader eventsReader;

    private ContactsAdapter contactsAdapter;
    private ContactsReader contactsReader;

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
        annotatedPictureImageView.setOnClickListener(view -> annotatedPictureImageView.setScaleType(annotatedPictureImageView.getScaleType() == ImageView.ScaleType.CENTER_CROP
                ? ImageView.ScaleType.CENTER_INSIDE
                : ImageView.ScaleType.CENTER_CROP));

        // save button
        Button saveButton = findViewById(R.id.annotated_image_save_button);
        saveButton.setOnClickListener(view -> {
            Picture pic = new Picture(Uri.parse(imageUriString));
            pic.events.addAll(pickedEventsIds);
            pic.contacts.addAll(pickedContactsIds);

            this.pictureRepository.upsert(pic);

            setResult(Activity.RESULT_OK);
            finish();
        });

        pictureRepository
                .findById(imageUriString)
                .observe(this, picture -> {
                    if(picture != null) {
                        pickedEventsIds = new HashSet<>(picture.events);
                        loadEvents();
                        pickedContactsIds = new HashSet<>(picture.contacts);
                        loadContacts();
                    }
                });

        // cancel button
        Button cancelButton = findViewById(R.id.annotated_image_cancel_button);
        cancelButton.setOnClickListener(view -> showCancelConfirmDialog());

        // pick event FAB
        fabPickEvent = findViewById(R.id.fab_pick_event);
        fabPickEvent.setOnClickListener(view -> pickEvent());

        // pick contact FAB
        fabPickContact = findViewById(R.id.fab_pick_contact);
        fabPickContact.setOnClickListener(view -> pickContact());

        // create the pager
        pager = findViewById(R.id.tag_pager);
        TagPicturePagerAdapter pagerAdapter = new TagPicturePagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);

        // create adapter and eventsReader
        eventsAdapter = pagerAdapter.eventListFragment.getAdapter();
        eventsAdapter.setOnItemClickListener((view, position, vm) -> {
            pickedEventsIds.remove(vm.getId());
            loadEvents();
        });
        eventsReader = new CalendarEventsReader(this);

        loadEvents();

        // create contacts adapter
        contactsAdapter = pagerAdapter.contactListFragment.getAdapter();
        contactsAdapter.setOnItemClickListener((view, position, vm) -> {
            pickedContactsIds.remove(vm.getId());
            loadContacts();
        });
        contactsReader = new ContactsReader(this);

        loadContacts();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_READ_CALENDAR: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadEvents();
                } else {
                    fabPickEvent.setVisibility(View.GONE);
                }
            }
            case PERMISSION_REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadContacts();
                } else {
                    fabPickContact.setVisibility(View.GONE);
                }
            }
        }
    }

    /**
     * check if the application has the requested permission
     * @param permission permission to check
     * @return boolean true if permission was granted, false otherwise
     */
    private boolean hasPermission(final String permission) {
        return ContextCompat.checkSelfPermission(getApplicationContext(), permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Loads the events data for the currently selected events.
     * Does nothing if the permission was not granted...
     */
    private void loadEvents() {
        if(!hasPermission(Manifest.permission.READ_CALENDAR)) return;
        eventsAdapter.setData(
                eventsReader.readEventsWithIds(new ArrayList<>(pickedEventsIds))
        );
    }

    /**
     * Loads the contacts data for the currently selected contacts.
     * Does nothing if the permission was not granted...
     */
    private void loadContacts() {
        if(!hasPermission(Manifest.permission.READ_CONTACTS)) return;
        contactsAdapter.setData(
                contactsReader.readContactsWithIds(new ArrayList<>(pickedContactsIds))
        );
    }

    /**
     * Start the activity to pick an event from the calendar
     */
    private void pickEvent() {
        if(!hasPermission(Manifest.permission.READ_CALENDAR)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CALENDAR},
                    PERMISSION_REQUEST_READ_CALENDAR);
        } else {
            Intent intent = new Intent(this, EventPickerActivity.class);
            startActivityForResult(intent, PICK_EVENT);
        }
    }

    /**
     * Start the activity to pick a contact from the phone
     */
    private void pickContact() {
        if(!hasPermission(Manifest.permission.READ_CONTACTS)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    PERMISSION_REQUEST_READ_CONTACTS);
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
            startActivityForResult(intent, PICK_CONTACT);
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
                Toast.makeText(this, R.string.toast_event_added, Toast.LENGTH_SHORT).show();
                pickedEventsIds.add(id);

                loadEvents();

                // move pager to events
                pager.setCurrentItem(TagPicturePagerAdapter.PAGE_EVENTS);
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
                Toast.makeText(this, R.string.toast_contact_added, Toast.LENGTH_SHORT).show();

                ContactViewModel vm = contactsReader.readByUri(data.getData());
                pickedContactsIds.add(vm.getId());

                // move pager to contacts
                pager.setCurrentItem(TagPicturePagerAdapter.PAGE_CONTACTS);

                loadContacts();
            }
        }
    }

    /**
     * Show the user a confirmation dialog to avoid quiting with unsaved changes
     */
    private void showCancelConfirmDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert
                .setTitle(R.string.confirm_unsaved_picture)
                .setMessage(R.string.confirm_unsaved_picture_hint)
                .setIcon(R.drawable.ic_warn_black)
                .setPositiveButton(R.string.yes, (dialog, which) -> finish())
                .setNegativeButton(R.string.no, (dialog, which) -> dialog.cancel());

        alert.show();
    }

    /**
     * Adapter class for the viewpager displaying the list of events and contacts
     */
    private class TagPicturePagerAdapter extends FragmentPagerAdapter {
        public static final int PAGE_EVENTS = 0;
        public static final int PAGE_CONTACTS = 1;

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
