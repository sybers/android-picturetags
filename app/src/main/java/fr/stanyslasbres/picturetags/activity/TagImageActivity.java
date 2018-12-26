package fr.stanyslasbres.picturetags.activity;

import android.app.Activity;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.clans.fab.FloatingActionButton;

import fr.stanyslasbres.picturetags.R;

public final class TagImageActivity extends AppCompatActivity {
    private static final int PICK_EVENT = 0;
    private static final int PICK_CONTACT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_image);

        FloatingActionButton fabPickEvent = findViewById(R.id.fab_pick_event);
        fabPickEvent.setOnClickListener(view -> pickEvent());

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
        // TODO : handle contact selection...
    }
}
