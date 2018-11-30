package fr.stanyslasbres.picturetags.persistence.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import fr.stanyslasbres.picturetags.persistence.entities.Contact;

@Dao
public interface ContactsDao {

    @Insert
    void insert(Contact... picture);

    @Update
    void update(Contact... pictures);

    @Delete
    void delete(Contact... pictures);

    @Query("SELECT * FROM contacts")
    List<Contact> all();

}
