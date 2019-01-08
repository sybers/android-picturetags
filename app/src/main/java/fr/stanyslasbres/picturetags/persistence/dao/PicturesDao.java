package fr.stanyslasbres.picturetags.persistence.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.*;

import java.util.List;

import fr.stanyslasbres.picturetags.persistence.entities.Picture;

@Dao
public interface PicturesDao {

    @Insert(onConflict = OnConflictStrategy.FAIL)
    void insert(Picture... picture);

    @Update(onConflict = OnConflictStrategy.FAIL)
    void update(Picture... pictures);

    @Delete
    void delete(Picture... pictures);

    @Query("SELECT * FROM pictures")
    LiveData<List<Picture>> all();

    @Query("SELECT * FROM pictures WHERE uri = :uri LIMIT 1")
    LiveData<Picture> findByUri(String uri);
}
