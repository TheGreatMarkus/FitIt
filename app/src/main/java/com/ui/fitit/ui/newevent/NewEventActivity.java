package com.ui.fitit.ui.newevent;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
        TimePickerDialog.OnTimeSetListener {

    private static final String TAG = "NewEventActivity";
    private static final String TIME_FORMAT = "hh:mm a";


    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference users = db.collection(Constants.USERS_COLLECTION);
    private CollectionReference eventCollection;
    private CollectionReference sessionCollection;
    private SharedPreferences spLogin;

    EditText name;
    EditText description;
    EditText location;

    TextView eventTime;
    EditText duration;
    Spinner frequencySpinner;
    Button createEventButton;

    Frequency frequency = Frequency.ONCE;

    private LocalTime startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);
        initAllComponents();

        setupPersistentStorage();

        startTime = LocalTime.now();
        eventTime.setText(startTime.format(DateTimeFormatter.ofPattern(TIME_FORMAT)));
    }

    private void setupPersistentStorage() {
        spLogin = getSharedPreferences(SPUtilities.SP_LOGIN, Context.MODE_PRIVATE);
        String username = SPUtilities.getLoggedInUserName(spLogin);
        if (!username.equals(SPUtilities.SP_LOGIN_NO_USER)) {
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
        eventTime = findViewById(R.id.event_time);
        duration = findViewById(R.id.event_duration);

        createEventButton = findViewById(R.id.button);
        createEventButton.setOnClickListener(this::onCreateEvent);

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
            FitDate startFitDate = new FitDate(LocalDate.now());

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
        if (eventTime.getText().toString().isEmpty()
                || name.getText().toString().isEmpty()
                || description.getText().toString().isEmpty()
                || duration.getText().toString().isEmpty()
                || location.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please don't leave any empty fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    // TODO we could style the components based on if they are invalid, but i don't think this is necessary
    private void resetComponentState(EditText component, boolean isValid) {
        if (isValid) {
            component.setTextColor(getColor(R.color.default_outline));
        } else {
            component.setTextColor(getColor(R.color.error));
        }
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

    @Override
    public void onTimeSet(TimePicker view, int hour, int minute) {
        startTime = LocalTime.of(hour, minute);
        eventTime.setText(startTime.format(DateTimeFormatter.ofPattern(TIME_FORMAT)));
    }
}
