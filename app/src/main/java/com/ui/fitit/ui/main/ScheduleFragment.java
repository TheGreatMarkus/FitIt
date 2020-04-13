package com.ui.fitit.ui.main;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

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
import com.ui.fitit.data.model.FitDate;
import com.ui.fitit.data.model.Frequency;
import com.ui.fitit.data.model.ScheduleItem;
import com.ui.fitit.data.model.Session;
import com.ui.fitit.ui.newevent.NewEventActivity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class ScheduleFragment extends Fragment {

    private static final String TAG = "ScheduleActivity";
    public List<ScheduleItem> scheduleItems;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference eventCollection;
    private CollectionReference sessionCollection;
    private EventAdapter adapter;
    private MainActivity activity;

    private ScheduleMode mode = ScheduleMode.FUTURE;
    private List<Event> events;
    private List<Session> sessions;
    private ListMultimap<Event, Session> scheduleMap;
    private ListView sessionListView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);
        initViews(view);

        activity = (MainActivity) requireActivity();
        scheduleItems = new ArrayList<>();

        eventCollection = db.collection(Constants.USERS_COLLECTION)
                .document(activity.user.getUsername()).collection(Constants.EVENTS_COLLECTION);
        sessionCollection = db.collection(Constants.USERS_COLLECTION)
                .document(activity.user.getUsername()).collection(Constants.SESSION_COLLECTION);

        sessionCollection.addSnapshotListener((d, e) -> updateScheduleData());

        setupListView(activity);

        return view;
    }

    private void initViews(View view) {
        sessionListView = view.findViewById(R.id.all_sessions);
        FloatingActionButton fabNewEvent = view.findViewById(R.id.fab_new_event);
        Button futureSessionButton = view.findViewById(R.id.schedule_future_button);
        Button pastSessionButton = view.findViewById(R.id.schedule_past_button);

        fabNewEvent.setOnClickListener(this::addNewEvent);
        futureSessionButton.setOnClickListener(v -> {
            if (mode == ScheduleMode.PAST) {
                mode = ScheduleMode.FUTURE;
                updateShownSchedule();
            }
        });
        pastSessionButton.setOnClickListener(v -> {
            if (mode == ScheduleMode.FUTURE) {
                mode = ScheduleMode.PAST;
                updateShownSchedule();
            }
        });
    }

    private void updateScheduleData() {
        Task<QuerySnapshot> getEvents = eventCollection.get();
        Task<QuerySnapshot> getSessions = sessionCollection.get();

        Tasks.whenAllComplete(getEvents, getSessions).addOnCompleteListener(command -> {
            events = Objects.requireNonNull(getEvents.getResult()).toObjects(Event.class);
            sessions = Objects.requireNonNull(getSessions.getResult()).toObjects(Session.class);
            scheduleMap = ArrayListMultimap.create();

            events.forEach(event -> {
                List<Session> eventSessions = sessions.stream()
                        .filter(session -> session.getEventId().equals(event.getId()))
                        .sorted((s1, s2) -> s2.getDate().compareTo(s1.getDate()))
                        .collect(Collectors.toList());
                scheduleMap.putAll(event, eventSessions);

            });
            verifySchedule();
        });
    }

    private void verifySchedule() {
        AtomicBoolean updated = new AtomicBoolean(false);
        scheduleMap.asMap().forEach((event, eventSessions) -> {
            eventSessions.forEach(session -> {
                LocalDateTime currentDT = LocalDateTime.now();
                LocalDateTime itemDT = LocalDateTime.of(session.getDate().toLocalDate(), event.getEndTime().toLocalTime());
                if (itemDT.isBefore(currentDT) && session.getAttendance() == Attendance.UPCOMING) {
                    Toast.makeText(activity, "Found an upcoming workout that passed: " + event.getName(), Toast.LENGTH_SHORT).show();

                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setPositiveButton("Completed", (dialog, which) -> {
                        session.setAttendance(Attendance.COMPLETED);
                        sessionCollection.document(session.getId()).set(session);
                        updated.set(true);
                    }).setNegativeButton("Missed", (dialog, which) -> {
                        session.setAttendance(Attendance.MISSED);
                        sessionCollection.document(session.getId()).set(session);
                        updated.set(true);
                    }).setTitle("A workout has passed!")
                            .setMessage("Found an upcoming workout that passed. " +
                                    "Did you complete it?" + itemDT.toString()).show();
                }
            });
        });
        if (!updated.get()) {
            updateShownSchedule();
        }
    }

    private void updateShownSchedule() {
        scheduleItems = new ArrayList<>();

        scheduleMap.asMap().forEach((event, eventSessions) -> {

            if (mode == ScheduleMode.FUTURE) {
                eventSessions.stream()
                        .filter(session -> session.getAttendance() == Attendance.UPCOMING)
                        .max((s1, s2) -> s1.getDate().toLocalDate().compareTo(s2.getDate().toLocalDate()))
                        .ifPresent(lastSession -> scheduleItems.add(new ScheduleItem(event, lastSession)));
            } else {
                eventSessions.stream().filter(session -> session.getAttendance() != Attendance.UPCOMING).forEach(session -> {
                    scheduleItems.add(new ScheduleItem(event, session));
                });
            }
        });
        scheduleItems.sort((o1, o2) -> mode == ScheduleMode.FUTURE ? o1.compareTo(o2) : o2.compareTo(o1));
        adapter.notifyDataSetChanged();
    }

    public void onSessionComplete(ScheduleItem item) {
        Session session = sessions.stream().filter(temp -> item.getSessionId().equals(temp.getId())).findFirst().get();
        Event event = events.stream().filter(temp -> item.getEventId().equals(temp.getId())).findFirst().get();

        // Set current session as completed
        if (session.getAttendance() != Attendance.COMPLETED) {
            session.setAttendance(Attendance.COMPLETED);
            sessionCollection.document(session.getId()).set(session);

            // Create new session as needed
            if (event.getFrequency() == Frequency.DAILY) {
                FitDate newDate = new FitDate(session.getDate().toLocalDate().plusDays(1));
                Session newSession = new Session(newDate, event.getId(), Attendance.UPCOMING);
                sessionCollection.document(newSession.getId()).set(newSession);
            } else if (event.getFrequency() == Frequency.WEEKLY) {
                FitDate newDate = new FitDate(session.getDate().toLocalDate().plusDays(7));
                Session newSession = new Session(newDate, event.getId(), Attendance.UPCOMING);
                sessionCollection.document(newSession.getId()).set(newSession);
            }
        }
    }

    private void setupListView(MainActivity activity) {
        // Android adapter for list view
        adapter = new EventAdapter(activity, this, R.layout.schedule_item);
        sessionListView.setAdapter(adapter);
        sessionListView.setOnItemClickListener((adapterView, view, i, l) -> {
            // TODO: Show extra information on click
        });

    }


    public enum ScheduleMode {
        FUTURE,
        PAST,
    }

    private void addNewEvent(View view) {
        Intent intent = new Intent(getActivity(), NewEventActivity.class);
        startActivity(intent);
    }


}
