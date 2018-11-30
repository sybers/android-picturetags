package fr.stanyslasbres.picturetags.persistence.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import fr.stanyslasbres.picturetags.persistence.entities.Event;

@Dao
public interface EventsDao {

    @Insert
    void insert(Event... picture);

    @Update
    void update(Event... pictures);

    @Delete
    void delete(Event... pictures);

    @Query("SELECT * FROM events")
    List<Event> all();

}
