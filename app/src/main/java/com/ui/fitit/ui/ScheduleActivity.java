package com.ui.fitit.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ui.fitit.Adapters.EventAdapter;
import com.ui.fitit.R;
import com.ui.fitit.data.model.Event;
import java.util.List;

public class ScheduleActivity extends AppCompatActivity {

    List<Event> events;
    EventAdapter adapter;
    ListView allEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_activity);
        allEvents = findViewById(R.id.allEvents);
        setEventsAdapter();
    }

    private void setEventsAdapter() {
        // Android adapter for list view
        adapter = new EventAdapter(this, R.layout.list_events, events);
        allEvents.setAdapter(adapter);
        allEvents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });
    }
}
