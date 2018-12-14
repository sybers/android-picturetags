package fr.stanyslasbres.picturetags.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.CalendarView;

import fr.stanyslasbres.picturetags.R;
import fr.stanyslasbres.picturetags.eventpicker.CalendarEventsReader;
import fr.stanyslasbres.picturetags.eventpicker.EventsAdapter;

/**
 * Allows to pick an event from the calendar and returns the information about it
 */
public class EventPickerActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_READ_CALENDAR = 1 ;
    private EventsAdapter adapter;
    private CalendarEventsReader eventsReader;

    private CalendarView calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_picker);

        // create adapter and eventsReader
        adapter = new EventsAdapter(this);
        eventsReader = new CalendarEventsReader(this);
        eventsReader.setProjection(EventsAdapter.CALENDAR_PROJECTION);

        // create the recyclerView and attach the adapter
        RecyclerView eventList = findViewById(R.id.eventList);
        eventList.setAdapter(adapter);
        eventList.setLayoutManager(new LinearLayoutManager(this));

        // Attach calendar event to recycler view data update
        calendar = findViewById(R.id.calendarView);
        calendar.setOnDateChangeListener((calendarView, year, month, day) -> {
            adapter.setData(eventsReader.readEventsForDay(year, month, day));
        });

        int calendarPermissionStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR);
        if (calendarPermissionStatus != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CALENDAR)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CALENDAR},
                        PERMISSION_REQUEST_READ_CALENDAR);
            }

            return;
        }

        // the permission is available, load the events !
        adapter.setData(eventsReader.readEventsForDay(calendar.getDate()));
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_READ_CALENDAR: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    adapter.setData(eventsReader.readEventsForDay(calendar.getDate()));

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    this.finish();
                }
            }
        }
    }
}
