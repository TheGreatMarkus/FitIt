package com.ui.fitit.ui.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.ui.fitit.Constants;
import com.ui.fitit.R;
import com.ui.fitit.adapters.EventAdapter;
import com.ui.fitit.data.model.Attendance;
import com.ui.fitit.data.model.Event;
import com.ui.fitit.data.model.ScheduleItem;
import com.ui.fitit.data.model.Session;
import com.ui.fitit.ui.newevent.NewEventActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ScheduleFragment extends Fragment {

    private static String TAG = "ScheduleActivity";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference eventCollection;
    private CollectionReference sessionCollection;

    private ListMultimap<Event, Session> scheduleMap = ArrayListMultimap.create();
    private List<ScheduleItem> scheduleItems;


    private ListView sessionListView;
    private FloatingActionButton fabNewEvent;
    private EventAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);
        initViews(view);

        Context context = requireContext();
        MainActivity activity = (MainActivity) requireActivity();
        scheduleItems = new ArrayList<>();
        String username = activity.user.getUsername();

        setupListView(context, username);


        eventCollection = db.collection(Constants.USERS_COLLECTION)
                .document(username).collection(Constants.EVENTS_COLLECTION);

        sessionCollection = db.collection(Constants.USERS_COLLECTION)
                .document(username).collection(Constants.SESSION_COLLECTION);

        sessionCollection.addSnapshotListener((d, e) -> updateScheduleData());


        return view;
    }

    private void initViews(View view) {
        sessionListView = view.findViewById(R.id.all_sessions);
        fabNewEvent = view.findViewById(R.id.fab_new_event);
        fabNewEvent.setOnClickListener(this::addNewEvent);
    }


    private void updateScheduleData() {
        Task<QuerySnapshot> getEvents = eventCollection.get();
        Task<QuerySnapshot> getSessions = sessionCollection.get();

        Tasks.whenAllComplete(getEvents, getSessions).addOnCompleteListener(command -> {
            scheduleMap = ArrayListMultimap.create();
            scheduleItems.removeAll(scheduleItems);

            List<Event> events = getEvents.getResult().toObjects(Event.class);
            List<Session> sessions = getSessions.getResult().toObjects(Session.class);

            events.forEach(event -> {
                List<Session> eventSessions = sessions.stream()
                        .filter(session -> session.getEventId().equals(event.getId()))
                        .sorted((s1, s2) -> s2.getDate().compareTo(s1.getDate()))
                        .collect(Collectors.toList());
                scheduleMap.putAll(event, eventSessions);
                ScheduleItem item = new ScheduleItem(event, eventSessions.get(0));
                scheduleItems.add(item);
            });
            scheduleItems.removeIf(scheduleItem -> scheduleItem.getSession().getAttendance() != Attendance.UPCOMING);
            scheduleItems.sort((o1, o2) -> o1.getEvent().getStartTime().compareTo(o2.getEvent().getStartTime()));
            scheduleItems.sort((o1, o2) -> o1.getSession().getDate().compareTo(o2.getSession().getDate()));
            adapter.notifyDataSetChanged();
        });

    }


    private void setupListView(Context context, String username) {
        // Android adapter for list view
        adapter = new EventAdapter(context, R.layout.schedule_item, scheduleItems, username);
        sessionListView.setAdapter(adapter);
        sessionListView.setOnItemClickListener((adapterView, view, i, l) -> {
            // TODO: Show extra information on click
        });

    }

    private void addNewEvent(View view) {
        Intent intent = new Intent(getActivity(), NewEventActivity.class);
        startActivity(intent);
    }

}
