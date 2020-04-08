package com.ui.fitit.ui;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import com.ui.fitit.Adapters.EventAdapter;
import com.ui.fitit.R;
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
        allSessions = findViewById(R.id.allSessions);
        setEventsAdapter();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initListOfSessions() {
        sessions = new ArrayList<>();

        Event event1 = new Event();
        event1.setDescription("This is a description");
        event1.setName("At-home Workout");
        event1.setLocation("Mi Casa");
        event1.setStart(LocalTime.of(7, 30, 0,0));
        event1.setEnd(LocalTime.of(8, 30, 0,0));
        Session session1 = new Session();
        session1.setEvent(event1);
        session1.setDate(LocalDate.of(2020, 4, 8));
        session1.setAttended(false);

        Event event2 = new Event();
        event2.setDescription("This is a description");
        event2.setName("At-home Workout");
        event2.setLocation("Mi Casa");
        event2.setStart(LocalTime.of(7, 30, 0,0));
        event2.setEnd(LocalTime.of(8, 30, 0,0));
        Session session2 = new Session();
        session2.setEvent(event2);
        session2.setDate(LocalDate.of(2020, 4, 8));
        session2.setAttended(false);

        sessions.add(session1);
        sessions.add(session2);

    }

    private void setEventsAdapter() {
        // Android adapter for list view
        adapter = new EventAdapter(this, R.layout.list_sessions, sessions);
        allSessions.setAdapter(adapter);
        allSessions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });
    }
}
