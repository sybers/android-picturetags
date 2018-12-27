package fr.stanyslasbres.picturetags.persistence.entities;

import android.arch.persistence.room.*;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "pictures")
public class Picture {
    @PrimaryKey
    @NonNull
    public final Uri uri;

    public List<Long> contacts;

    public List<Long> events;

    public Picture(@NonNull Uri uri) {
        this.uri = uri;
        contacts = new ArrayList<>();
        events = new ArrayList<>();
    }
}
