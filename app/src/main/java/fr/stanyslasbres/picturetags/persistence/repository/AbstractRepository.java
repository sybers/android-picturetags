package fr.stanyslasbres.picturetags.persistence.repository;

import android.arch.lifecycle.LiveData;

import java.util.List;

@SuppressWarnings("unchecked")
abstract public class AbstractRepository<T, U> {
    abstract public void insert(T... objects);

    abstract public void update(T... objects);

    abstract public void delete(T... objects);

    abstract public LiveData<List<T>> all();

    abstract public LiveData<T> findById(U id);
}
