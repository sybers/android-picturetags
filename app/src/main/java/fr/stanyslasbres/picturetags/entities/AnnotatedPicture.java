package fr.stanyslasbres.picturetags.entities;

import android.arch.persistence.room.*;
import android.net.Uri;
import java.util.List;

@Entity
public class AnnotatedPicture {
    @PrimaryKey
    private Uri uri;

    public AnnotatedPicture() {}

    public AnnotatedPicture(Uri uri) {
        this.uri = uri;
    }
}
