package com.ui.fitit.ui.newevent;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
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
import com.ui.fitit.SPUtilities;
import com.ui.fitit.data.model.Attendance;
import com.ui.fitit.data.model.Event;
import com.ui.fitit.data.model.FitDate;
import com.ui.fitit.data.model.FitTime;
import com.ui.fitit.data.model.Frequency;
import com.ui.fitit.data.model.Session;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class NewEventActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,
        TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    private static final String TAG = "NewEventActivity";

    EditText name;
    EditText description;
    EditText location;
    TextView startTimeView;
    TextView startDateView;
    EditText duration;
    Spinner frequencySpinner;
    Button createEventButton;
    Button setStartTimeButton;
    Button setStartDateButton;
    Frequency frequency = Frequency.ONCE;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference users = db.collection(Constants.USERS_COLLECTION);
    private CollectionReference eventCollection;
    private CollectionReference sessionCollection;
    private SharedPreferences spLogin;
    private LocalTime startTime;
    private LocalDate startDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);
        initAllComponents();

        setupPersistentStorage();

        startTime = LocalTime.now();
        startTimeView.setText(startTime.format(DateTimeFormatter.ofPattern(Constants.TIME_FORMAT)));
    }

    private void setupPersistentStorage() {
        spLogin = getSharedPreferences(SPUtilities.SP_ID, Context.MODE_PRIVATE);
        String username = SPUtilities.getLoggedInUserName(spLogin);
        if (!username.equals(SPUtilities.SP_NO_USER)) {
            eventCollection = users.document(username).collection(Constants.EVENTS_COLLECTION);
            sessionCollection = users.document(username).collection(Constants.SESSION_COLLECTION);
        } else {
            Toast.makeText(this, "Unexpected state. You are not logged in. Redirecting to main screen", Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    public void initAllComponents() {
        name = findViewById(R.id.name);
        description = findViewById(R.id.description);
        location = findViewById(R.id.item_location);
        startTimeView = findViewById(R.id.event_start_time);
        startDateView = findViewById(R.id.event_start_date);
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
        startTimeView.setText(startTime.format(DateTimeFormatter.ofPattern(Constants.TIME_FORMAT)));
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Log.d(TAG, String.format("onDateSet Called: %d, %d, %d", year, month, dayOfMonth));
        startDate = LocalDate.of(year, month + 1, dayOfMonth);
        startDateView.setText(startDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
    }
}
