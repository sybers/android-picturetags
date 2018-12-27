package fr.stanyslasbres.picturetags.persistence;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import fr.stanyslasbres.picturetags.persistence.dao.PicturesDao;
import fr.stanyslasbres.picturetags.persistence.entities.Picture;

/**
 * Application database class, will be inflated by the RoomDao
 */
@Database(entities = {Picture.class}, version = 4, exportSchema = false)
@TypeConverters({AppConverters.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract PicturesDao getPicturesDao();
}