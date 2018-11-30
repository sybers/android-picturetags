package fr.stanyslasbres.picturetags.persistence.entities;

import android.arch.persistence.room.*;
import android.net.Uri;

@Entity(tableName = "pictures")
public class Picture {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public Uri uri;

    public Picture(Uri uri) {
        this.uri = uri;
    }
}
