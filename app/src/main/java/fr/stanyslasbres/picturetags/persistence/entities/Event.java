package fr.stanyslasbres.picturetags.persistence.entities;

import android.arch.persistence.room.*;

import java.util.Date;

@Entity(tableName = "events")
public class Event {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "start_date")
    public Date startDate;

    @ColumnInfo(name = "end_date")
    public Date endDate;

    public Event(Date startDate, Date endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
