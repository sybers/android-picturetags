package fr.stanyslasbres.picturetags.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.CalendarView;

import java.util.Calendar;

import fr.stanyslasbres.picturetags.R;
import fr.stanyslasbres.picturetags.readers.CalendarEventsReader;
import fr.stanyslasbres.picturetags.adapters.EventsAdapter;

/**
 * Allows to pick an event from the calendar and returns the information about it
 */
public class EventPickerActivity extends AppCompatActivity {
    public static final String EXTRA_SELECTED_EVENT_ID = "fr.stanyslasbres.picturetags.SELECTED_EVENT_ID";

    private static final int PERMISSION_REQUEST_READ_CALENDAR = 1;
    private EventsAdapter adapter;
    private CalendarEventsReader eventsReader;

    private CalendarView calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_event);

        // create adapter and eventsReader
        adapter = new EventsAdapter();
        adapter.setOnItemClickListener((view, position, vm) -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra(EventPickerActivity.EXTRA_SELECTED_EVENT_ID, vm.getId());
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        });

        eventsReader = new CalendarEventsReader(this);

        // create the recyclerView and attach the adapter
        RecyclerView eventList = findViewById(R.id.eventList);
        eventList.setAdapter(adapter);
        eventList.setLayoutManager(new LinearLayoutManager(this));

        // Attach calendar event to recycler view data update
        calendar = findViewById(R.id.calendarView);
        calendar.setOnDateChangeListener((calendarView, year, month, day) -> {
            Calendar cal = Calendar.getInstance();
            cal.set(year, month, day);
            adapter.setData(eventsReader.readEventsForDay(cal));
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
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(calendar.getDate());
        adapter.setData(eventsReader.readEventsForDay(cal));
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_READ_CALENDAR: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(calendar.getDate());
                    adapter.setData(eventsReader.readEventsForDay(cal));

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    this.finish();
                }
            }
        }
    }
}
