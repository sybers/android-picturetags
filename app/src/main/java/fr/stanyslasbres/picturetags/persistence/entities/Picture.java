package fr.stanyslasbres.picturetags.persistence.entities;

import android.arch.persistence.room.*;
import android.net.Uri;
import android.support.annotation.NonNull;

@Entity(tableName = "pictures")
public class Picture {
    @PrimaryKey
    @NonNull
    public final Uri uri;

    public Picture(@NonNull Uri uri) {
        this.uri = uri;
    }
}
