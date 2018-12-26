package fr.stanyslasbres.picturetags.persistence.entities;

import android.arch.persistence.room.*;
import android.net.Uri;
import android.support.annotation.NonNull;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(
        tableName = "contacts",
        foreignKeys = @ForeignKey(entity = Picture.class, parentColumns = "uri", childColumns = "pictureUri", onDelete = CASCADE)
)
public class Contact {

    @PrimaryKey
    @NonNull
    public final Uri uri;

    public final Uri pictureUri;

    public Contact(@NonNull final Uri uri, @NonNull final Uri pictureUri) {
        this.uri = uri;
        this.pictureUri = pictureUri;
    }
}
