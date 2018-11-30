package fr.stanyslasbres.picturetags.entities;

import java.util.Date;

public class Event {
    private Date startDate;
    private Date endDate;

    public Event() {}

    public Event(Date startDate, Date endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
