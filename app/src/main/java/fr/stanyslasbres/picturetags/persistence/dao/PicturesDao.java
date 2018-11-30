package fr.stanyslasbres.picturetags.persistence.dao;

import android.arch.persistence.room.*;

import java.util.List;

import fr.stanyslasbres.picturetags.persistence.entities.Picture;

@Dao
public interface PicturesDao {

    @Insert
    void insert(Picture... picture);

    @Update
    void update(Picture... pictures);

    @Delete
    void delete(Picture... pictures);

    @Query("SELECT * FROM pictures")
    List<Picture> all();

}
