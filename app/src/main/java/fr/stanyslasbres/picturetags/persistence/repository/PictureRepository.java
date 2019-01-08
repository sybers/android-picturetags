package fr.stanyslasbres.picturetags.persistence.repository;

import android.arch.lifecycle.LiveData;
import android.database.sqlite.SQLiteConstraintException;

import java.util.List;

import fr.stanyslasbres.picturetags.PictureTagsApplication;
import fr.stanyslasbres.picturetags.persistence.dao.PicturesDao;
import fr.stanyslasbres.picturetags.persistence.entities.Picture;
import fr.stanyslasbres.picturetags.task.SimpleAsyncTask;

public class PictureRepository extends AbstractRepository<Picture, String> {

    private PicturesDao picturesDao = PictureTagsApplication.getDatabase().getPicturesDao();

    @Override
    public void insert(Picture... objects) {
        new SimpleAsyncTask(() -> {
            picturesDao.insert(objects);
        }).execute();
    }

    @Override
    public void update(Picture... objects) {
        new SimpleAsyncTask(() -> {
            picturesDao.update(objects);
        }).execute();
    }

    public void upsert(Picture... objects) {
        new SimpleAsyncTask(() -> {
            try {
                picturesDao.insert(objects);
            } catch(SQLiteConstraintException exception) {
                picturesDao.update(objects);
            }
        }).execute();
    }

    @Override
    public void delete(Picture... objects) {
        new SimpleAsyncTask(() -> {
            picturesDao.delete(objects);
        }).execute();
    }

    @Override
    public LiveData<List<Picture>> all() {
        return this.picturesDao.all();
    }

    @Override
    public LiveData<Picture> findById(String id) {
        return this.picturesDao.findByUri(id);
    }
}
