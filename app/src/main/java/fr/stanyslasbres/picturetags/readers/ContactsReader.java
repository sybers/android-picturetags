package fr.stanyslasbres.picturetags.readers;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.List;

import fr.stanyslasbres.picturetags.viewmodel.ContactViewModel;

public class ContactsReader {
    private static final String[] CONTACT_PROJECTION = new String[]{
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME,
    };

    private Context context;

    /**
     * ContactsReader Constructor.
     * @param context {@link Context} to use
     */
    public ContactsReader(Context context) {
        this.context = context;
    }

    /**
     * Read the contacts with the given Ids
     * @param contactsIds ids of the contacts to retreive
     * @return List of found contacts
     */
    public List<ContactViewModel> readContactsWithIds(List<Long> contactsIds) {
        if (contactsIds == null || contactsIds.size() == 0) {
            return null;
        }

        String queryString = String.format("%s IN (" + makeInPlaceholders(contactsIds.size()) + ")", ContactsContract.Contacts._ID);

        // convert query args to string
        String[] queryArgs = new String[contactsIds.size()];
        for (int i = 0; i < contactsIds.size(); i++) {
            queryArgs[i] = String.valueOf(contactsIds.get(i));
        }

        Cursor data = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, CONTACT_PROJECTION, queryString, queryArgs, null);
        return inflateFromCursor(data);
    }

    /**
     * Find contact information by it's URI
     * @param uri uri of the contact
     * @return contact informations
     */
    public ContactViewModel readByUri(Uri uri) {
        if (uri == null) {
            return null;
        }

        Cursor data = context.getContentResolver().query(uri, CONTACT_PROJECTION, null, null, null);
        List<ContactViewModel> inflatedData = inflateFromCursor(data);

        if(inflatedData != null && inflatedData.size() <= 1) {
            return inflatedData.get(0);
        } else {
            return null;
        }
    }

    /**
     * Generate a list of inflated view models from the cursor
     * @param data Cursor with contacts data
     * @return inflated list
     */
    private List<ContactViewModel> inflateFromCursor(Cursor data) {
        List<ContactViewModel> contacts = new ArrayList<>();

        // return an empty list of the cursor is null
        if(data == null) return null;

        for(data.moveToFirst(); !data.isAfterLast(); data.moveToNext()) {
            long id = data.getLong(data.getColumnIndex(ContactsContract.Contacts._ID));
            ContactViewModel vm = new ContactViewModel(id);

            String displayName = data.getString(data.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            vm.setDisplayName(displayName);

            contacts.add(vm);
        }

        return contacts;
    }

    /**
     * Make the placeholders for the `IN` query
     * @param size number of placeholders to generate
     * @return placeholders string
     */
    private String makeInPlaceholders(int size) {
        StringBuilder sb = new StringBuilder(size * 2 - 1);
        sb.append("?");
        for (int i = 1; i < size; i++) sb.append(",?");
        return sb.toString();
    }
}
