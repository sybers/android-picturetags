package fr.stanyslasbres.picturetags;

import android.app.Application;
import android.arch.persistence.room.Room;

import fr.stanyslasbres.picturetags.persistence.AppDatabase;

public class PictureTagsApplication extends Application {
    private static Application self;
    private static AppDatabase databaseInstance;

    public void onCreate() {
        super.onCreate();
        self = this;
    }

    /**
     * Get the application context
     * @return context
     */
    public static Application getApplication() {
        return self;
    }

    /**
     * Get the application database instance
     * @return database instance
     */
    public static AppDatabase getDatabase() {
        if(databaseInstance == null) {
            databaseInstance = Room.databaseBuilder(
                    getApplication().getApplicationContext(),
                    AppDatabase.class,
                    "picture-tags"
            ).build();
        }

        return databaseInstance;
    }
}
