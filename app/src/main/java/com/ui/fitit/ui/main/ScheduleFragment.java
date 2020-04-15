package com.ui.fitit.ui.main;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.ui.fitit.Constants;
import com.ui.fitit.R;
import com.ui.fitit.adapters.EventAdapter;
import com.ui.fitit.data.model.Attendance;
import com.ui.fitit.data.model.Event;
import com.ui.fitit.data.model.FitDate;
import com.ui.fitit.data.model.Frequency;
import com.ui.fitit.data.model.Group;
import com.ui.fitit.data.model.Message;
import com.ui.fitit.data.model.ScheduleItem;
import com.ui.fitit.data.model.Session;
import com.ui.fitit.data.model.User;
import com.ui.fitit.ui.newevent.NewEventActivity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ScheduleFragment extends Fragment {

    private static final String TAG = "ScheduleActivity";
    public List<ScheduleItem> scheduleItems = new ArrayList<>();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference eventCollection;
    private CollectionReference sessionCollection;
    private ListenerRegistration sessionCollectionRegistration;
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

        eventCollection = db.collection(Constants.USERS_COLLECTION)
                .document(activity.user.getUsername()).collection(Constants.EVENTS_COLLECTION);
        sessionCollection = db.collection(Constants.USERS_COLLECTION)
                .document(activity.user.getUsername()).collection(Constants.SESSION_COLLECTION);

        setupListView(activity);

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        sessionCollectionRegistration = sessionCollection.addSnapshotListener(this::updateScheduleData);
    }

    @Override
    public void onStop() {
        super.onStop();
        sessionCollectionRegistration.remove();
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

    private void updateScheduleData(QuerySnapshot sessionQuery, FirebaseFirestoreException e) {
        Log.d(TAG, "updateScheduleData Called");
        sessions = Objects.requireNonNull(sessionQuery).toObjects(Session.class);

        eventCollection.get().addOnSuccessListener(eventQuery -> {
            events = Objects.requireNonNull(eventQuery).toObjects(Event.class);
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
        Log.d(TAG, "verifySchedule Called");
        scheduleMap.asMap().forEach((event, eventSessions) -> {
            eventSessions.forEach(session -> {
                LocalDateTime currentDT = LocalDateTime.now();
                LocalDateTime itemDT = LocalDateTime.of(session.getDate().toLocalDate(), event.getEndTime().toLocalTime());
                if (itemDT.isBefore(currentDT) && session.getAttendance() == Attendance.UPCOMING) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setPositiveButton("Completed", (dialog, which) -> {
                        onSessionAttended(event, session, Attendance.COMPLETED);
                        updateShownSchedule();
                    }).setNegativeButton("Missed", (dialog, which) -> {
                        onSessionAttended(event, session, Attendance.MISSED);
                        updateShownSchedule();
                    }).setTitle("An upcoming session now passed!")
                            .setMessage(String.format("\"%s\" was an upcoming event that now passed. Did you complete or miss it?", event.getName())).show();
                }
            });
        });
        updateShownSchedule();
    }

    private void updateShownSchedule() {
        Log.d(TAG, "updateShownSchedule Called");
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

    public void onSessionAttended(Event event, Session session, Attendance newAttendance) {
        Log.d(TAG, "onSessionAttended Called");
        // Set current session as completed
        if (session.getAttendance() == Attendance.UPCOMING) {
            WriteBatch batch = db.batch();

            session.setAttendance(newAttendance);
            batch.set(sessionCollection.document(session.getId()), session);

            // Create new session as needed
            if (event.getFrequency() != Frequency.ONCE) {
                int daysTillNextSession = event.getFrequency().getDaysTillNext();
                FitDate newDate = new FitDate(session.getDate().toLocalDate().plusDays(daysTillNextSession));
                Session newSession = new Session(newDate, event.getId(), Attendance.UPCOMING);
                batch.set(sessionCollection.document(newSession.getId()), newSession);
            }
            updateGroupPoints(newAttendance, event);
            batch.commit();
        }
    }

    private void updateGroupPoints(Attendance newAttendance, Event event) {
        Log.d(TAG, "updateGroupPoints Called");
        CollectionReference groupCollection = db.collection(Constants.GROUPS_COLLECTION);

        groupCollection.whereArrayContains("users", activity.user.getUsername()).get().addOnSuccessListener(query -> {
            Log.d(TAG, "updateGroupPoints: Fetching groups for which the user is a part of");
            List<Group> groupsWithUser = Objects.requireNonNull(query.toObjects(Group.class)).stream().filter(Objects::nonNull).collect(Collectors.toList());
            if (groupsWithUser.size() == 1) {
                Log.d(TAG, "updateGroupPoints: User is in a group");
                Group group = groupsWithUser.get(0);
                WriteBatch batch = db.batch();
                AtomicInteger updatedUsers = new AtomicInteger();
                DocumentReference groupDocument = groupCollection.document(group.getId());

                group.getUsers().forEach(username -> {
                    activity.userCollection.document(username).get().addOnSuccessListener(document -> {
                        User user = document.toObject(User.class);
                        if (user != null) {
                            user.updatePoints(newAttendance, event, activity.userCollection);
                            updatedUsers.getAndIncrement();
                            batch.set(activity.userCollection.document(user.getUsername()), user);
                            if (updatedUsers.get() == group.getUsers().size()) {
                                batch.commit();
                            }
                        }
                    });
                });

                CollectionReference messageCollection = groupDocument.collection(Constants.MESSAGE_COLLECTION);
                String messageString = activity.user.getUsername()
                        + (newAttendance == Attendance.MISSED
                        ? " missed a session! Everyone loses points..."
                        : " completed a session! Everyone gets some points!");
                Message message = new Message(System.currentTimeMillis(), messageString, getString(R.string.app_name));
                messageCollection.document(message.getId()).set(message);

            } else if (groupsWithUser.size() == 0) {
                Log.d(TAG, "updateGroupPoints: User is not in a group");
                activity.user.updatePoints(newAttendance, event, activity.userCollection);
                activity.userCollection.document(activity.user.getUsername()).set(activity.user);
            }
        });

    }


    private void setupListView(MainActivity activity) {
        // Android adapter for list view
        adapter = new EventAdapter(activity, this, R.layout.schedule_item);
        sessionListView.setAdapter(adapter);
        sessionListView.setOnItemClickListener((adapterView, view, i, l) -> {
            // TODO: Show extra information on click
        });

    }

    private void addNewEvent(View view) {
        Intent intent = new Intent(getActivity(), NewEventActivity.class);
        intent.putExtra(Constants.INTENT_EXTRA_USER, activity.user);
        startActivity(intent);
    }

    public enum ScheduleMode {
        FUTURE,
        PAST,
    }


}
