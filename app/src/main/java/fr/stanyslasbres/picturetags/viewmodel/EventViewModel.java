package fr.stanyslasbres.picturetags.viewmodel;

import java.text.DateFormat;
import java.util.Calendar;

public class EventViewModel {
    private final long id;
    private String title;
    private long startDate;
    private long endDate;
    private int calendarColor;

    public EventViewModel(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getStartDate() {
        return startDate;
    }

    public String getStartDateFormatted() {
        return timestampToFormattedString(startDate);
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public String getEndDateFormatted() {
        return timestampToFormattedString(endDate);
    }

    public void setCalendarColor(int calendarColor) {
        this.calendarColor = calendarColor;
    }

    public int getCalendarColor() {
        return calendarColor;
    }

    /**
     * Get a human readable date representation for the given timestamp
     * @param timestamp timestamp
     * @return string representation of the given timestamp
     */
    private String timestampToFormattedString(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        DateFormat dateFormat = DateFormat.getInstance();

        calendar.setTimeInMillis(timestamp);
        return dateFormat.format(calendar.getTime());
    }
}
