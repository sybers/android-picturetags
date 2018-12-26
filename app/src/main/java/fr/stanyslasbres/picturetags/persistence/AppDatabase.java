package fr.stanyslasbres.picturetags.persistence;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import fr.stanyslasbres.picturetags.persistence.dao.ContactsDao;
import fr.stanyslasbres.picturetags.persistence.dao.EventsDao;
import fr.stanyslasbres.picturetags.persistence.dao.PicturesDao;
import fr.stanyslasbres.picturetags.persistence.entities.Picture;
import fr.stanyslasbres.picturetags.persistence.entities.Contact;
import fr.stanyslasbres.picturetags.persistence.entities.Event;

/**
 * Application database class, will be inflated by the RoomDao
 */
@Database(entities = {Picture.class, Contact.class, Event.class}, version = 3, exportSchema = false)
@TypeConverters({AppConverters.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract PicturesDao getPicturesDao();
    public abstract ContactsDao getContactDao();
    public abstract EventsDao getEventsDao();
}