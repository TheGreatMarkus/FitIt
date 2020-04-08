package com.ui.fitit.ui;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.ui.fitit.R;
import com.ui.fitit.adapters.EventAdapter;
import com.ui.fitit.data.model.Event;
import com.ui.fitit.data.model.Session;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ScheduleActivity extends AppCompatActivity {

    List<Session> sessions;
    EventAdapter adapter;
    ListView allSessions;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_activity);
        initListOfSessions(); // TODO: remove, just for testing purposes
        allSessions = findViewById(R.id.all_sessions);
        setEventsAdapter();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initListOfSessions() {
        sessions = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Event event = new Event();
            event.setDescription("This is a description");
            event.setName("At-home Workout");
            event.setLocation("Mi Casa");
            event.setStart(LocalTime.of(7, 30, 0, 0));
            event.setEnd(LocalTime.of(8, 30, 0, 0));
            Session session = new Session();
            session.setEvent(event);
            session.setDate(LocalDate.of(2020, 4, 8));
            session.setAttended(false);
            sessions.add(session);
        }
    }

    private void setEventsAdapter() {

        // Android adapter for list view
        adapter = new EventAdapter(this, R.layout.schedule_item, sessions);
        allSessions.setAdapter(adapter);
        allSessions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });
    }
}
