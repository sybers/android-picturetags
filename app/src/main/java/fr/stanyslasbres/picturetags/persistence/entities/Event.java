package fr.stanyslasbres.picturetags.persistence.entities;

import android.arch.persistence.room.*;
import android.net.Uri;
import android.support.annotation.NonNull;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(
        tableName = "events",
        foreignKeys = @ForeignKey(entity = Picture.class, parentColumns = "uri", childColumns = "pictureUri", onDelete = CASCADE)
)
public class Event {
    @PrimaryKey
    public final long id;

    public final Uri pictureUri;

    public Event(final long id, @NonNull final Uri pictureUri) {
        this.id = id;
        this.pictureUri = pictureUri;
    }
}
