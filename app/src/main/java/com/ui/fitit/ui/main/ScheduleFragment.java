package com.ui.fitit.ui.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ui.fitit.R;
import com.ui.fitit.adapters.EventAdapter;
import com.ui.fitit.data.model.Event;
import com.ui.fitit.data.model.FitDate;
import com.ui.fitit.data.model.FitTime;
import com.ui.fitit.data.model.Session;
import com.ui.fitit.ui.newevent.NewEventActivity;

import java.time.Month;
import java.util.ArrayList;
import java.util.List;

public class ScheduleFragment extends Fragment {

    private static String TAG = "ScheduleActivity";

    private List<Session> sessions;
    private EventAdapter adapter;
    private ListView allSessions;
    private FloatingActionButton fabNewEvent;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);
        Context context = requireContext();
        MainActivity activity = (MainActivity) requireActivity();
        initEventData(activity);

        allSessions = view.findViewById(R.id.all_sessions);
        fabNewEvent = view.findViewById(R.id.fab_new_event);


        setEvents(context);
        return view;
    }


    private void initEventData(MainActivity activity) {
        Log.d(TAG, "initEventData: Username of logged in user: " + activity.user);

        sessions = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Event event = new Event();
            event.setDescription("This is a description");
            event.setName("At-home Workout");
            event.setLocation("Mi Casa");
            event.setStart(new FitTime(7, 30));
            event.setEnd(new FitTime(8, 30));
            Session session = new Session();
            session.setEvent(event);
            session.setDate(new FitDate(2020, Month.APRIL, 8));
            session.setAttended(false);
            sessions.add(session);
        }
    }

    private void setEvents(Context context) {
        // Android adapter for list view
        adapter = new EventAdapter(context, R.layout.schedule_item, sessions);
        allSessions.setAdapter(adapter);
        allSessions.setOnItemClickListener((adapterView, view, i, l) -> {

        });

        fabNewEvent.setOnClickListener(this::addNewEvent);
    }

    private void addNewEvent(View view) {
        Intent intent = new Intent(getActivity(), NewEventActivity.class);
        startActivity(intent);
    }

}
