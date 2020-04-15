package com.ui.fitit.ui.newevent;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ui.fitit.Constants;
import com.ui.fitit.R;
import com.ui.fitit.data.model.Attendance;
import com.ui.fitit.data.model.Event;
import com.ui.fitit.data.model.FitDate;
import com.ui.fitit.data.model.FitTime;
import com.ui.fitit.data.model.Frequency;
import com.ui.fitit.data.model.Session;
import com.ui.fitit.data.model.User;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class NewEventActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,
        TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    private static final String TAG = "NewEventActivity";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference users = db.collection(Constants.USERS_COLLECTION);
    private CollectionReference eventCollection;
    private CollectionReference sessionCollection;

    LocalTime startTime = LocalTime.now();
    LocalDate startDate = LocalDate.now();
    private Frequency frequency = Frequency.ONCE;
    private User user;

    private EditText name;
    private EditText description;
    private EditText location;
    private TextView startTimeView;
    private TextView startDateView;
    private EditText duration;
    private Spinner frequencySpinner;
    private Button createEventButton;
    private Button setStartTimeButton;
    private Button setStartDateButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);
        initViews();

        setupPersistentStorage();


    }

    private void setupPersistentStorage() {
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra(Constants.INTENT_EXTRA_USER);

        if (user != null && user.getUsername() != null) {
            eventCollection = users.document(user.getUsername()).collection(Constants.EVENTS_COLLECTION);
            sessionCollection = users.document(user.getUsername()).collection(Constants.SESSION_COLLECTION);
        } else {
            Toast.makeText(this, "Unexpected state. Redirecting to main screen.", Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    public void initViews() {
        name = findViewById(R.id.name);
        description = findViewById(R.id.description);
        location = findViewById(R.id.item_location);
        startTimeView = findViewById(R.id.event_start_time);
        setStartTimeViewText();
        startDateView = findViewById(R.id.event_start_date);
        setStartDateViewText();
        duration = findViewById(R.id.event_duration);

        createEventButton = findViewById(R.id.create_event_button);
        createEventButton.setOnClickListener(this::onCreateEvent);

        setStartTimeButton = findViewById(R.id.event_start_time_button);
        setStartTimeButton.setOnClickListener(this::showTimePickerDialog);

        setStartDateButton = findViewById(R.id.event_start_date_button);
        setStartDateButton.setOnClickListener(this::showDatePickerDialog);

        frequencySpinner = findViewById(R.id.frequency_spinner);
        initSpinner();


    }


    public void initSpinner() {
        ArrayAdapter<Frequency> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Frequency.values());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        frequencySpinner.setAdapter(adapter);
        frequencySpinner.setOnItemSelectedListener(this);
    }

    public void onCreateEvent(View view) {
        boolean isInputValid = checkInputValidity();
        if (isInputValid) {
            String eventName = name.getText().toString();
            String eventDurationText = duration.getText().toString();
            String eventDescription = description.getText().toString();
            String eventLocation = location.getText().toString();

            // Compute end time
            int eventDuration = Integer.parseInt(eventDurationText);

            long minutesTillNextDay = startTime.until(LocalTime.of(23, 59, 59), ChronoUnit.MINUTES);
            if (minutesTillNextDay < eventDuration) {
                Toast.makeText(this, "Event can't go over to the next day! Please adjust the duration or start time.", Toast.LENGTH_SHORT).show();
                return;
            }

            LocalTime endTime = startTime.plusMinutes(eventDuration);

            FitTime startFitTime = new FitTime(startTime);
            FitTime endFitTime = new FitTime(endTime);
            FitDate startFitDate = new FitDate(startDate);

            Event event = new Event(eventName, eventDescription, eventLocation, startFitTime, endFitTime, startFitDate, frequency);
            Session session = new Session(startFitDate, event.getId(), Attendance.UPCOMING);
            Log.d(TAG, "onCreateEvent: New Event created: " + event);
            Log.d(TAG, "onCreateEvent: New Session created: " + session);
            Toast.makeText(this, "Event Created successfully", Toast.LENGTH_SHORT).show();

            eventCollection.document(event.getId()).set(event).addOnFailureListener(e -> {
                Log.d(TAG, "onCreateEvent: Error occurred while saving Event!", e);
            });

            sessionCollection.document(session.getId()).set(session).addOnFailureListener(e -> {
                Log.d(TAG, "onCreateEvent: Error occurred while saving session!", e);
            });

            finish();
        }
    }

    public boolean checkInputValidity() {
        if (startTimeView.getText().toString().isEmpty()
                || name.getText().toString().isEmpty()
                || description.getText().toString().isEmpty()
                || duration.getText().toString().isEmpty()
                || location.getText().toString().isEmpty()
                || startDateView.getText().toString().isEmpty()
                || startTimeView.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please don't leave any empty fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        frequency = (Frequency) parent.getItemAtPosition(position);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        frequency = (Frequency) parent.getItemAtPosition(0);
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    private void showDatePickerDialog(View view) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    @Override
    public void onTimeSet(TimePicker view, int hour, int minute) {
        startTime = LocalTime.of(hour, minute);
        setStartTimeViewText();
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Log.d(TAG, String.format("onDateSet Called: %d, %d, %d", year, month, dayOfMonth));
        startDate = LocalDate.of(year, month + 1, dayOfMonth);
        setStartDateViewText();
    }


    private void setStartDateViewText() {
        startDateView.setText(startDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
    }

    private void setStartTimeViewText() {
        startTimeView.setText(startTime.format(DateTimeFormatter.ofPattern(Constants.TIME_FORMAT)));
    }


}
