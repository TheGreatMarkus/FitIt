package com.ui.fitit.ui.newevent;

import android.app.TimePickerDialog;
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

import com.ui.fitit.R;
import com.ui.fitit.data.model.Event;
import com.ui.fitit.data.model.Frequency;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class NewEventActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,
        TimePickerDialog.OnTimeSetListener {

    private static final String TAG = "NewEventActivity";
    private static final String TIME_FORMAT = "hh:mm a";


    EditText name;
    EditText description;
    EditText location;

    TextView eventTime;
    EditText duration;
    Spinner frequencySpinner;
    Button createEventButton;

    @Frequency
    String frequency;

    private LocalTime startTime = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);
        initAllComponents();

        startTime = LocalTime.now();
        eventTime.setText(startTime.format(DateTimeFormatter.ofPattern(TIME_FORMAT)));
    }

    public void initAllComponents() {
        name = findViewById(R.id.name);
        description = findViewById(R.id.description);
        location = findViewById(R.id.location);
        eventTime = findViewById(R.id.event_time);
        duration = findViewById(R.id.event_duration);

        createEventButton = findViewById(R.id.button);

        frequencySpinner = findViewById(R.id.frequencyChoices);
        initSpinner();


    }

    public void initSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.frequency_choices, android.R.layout.simple_spinner_item);
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

            Event event = new Event(eventName, eventDescription, startTime, endTime, LocalDate.now(), eventLocation);
            Log.d(TAG, "onCreateEvent: New Event created: " + event);
            Toast.makeText(this, "Event Created successfully", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public boolean checkInputValidity() {
        if (eventTime.getText().toString().isEmpty()
                || name.getText().toString().isEmpty()
                || description.getText().toString().isEmpty()
                || duration.getText().toString().isEmpty()
                || location.getText().toString().isEmpty()
                || frequency.isEmpty()) {
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
        frequency = (String) parent.getItemAtPosition(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        frequency = (String) parent.getItemAtPosition(0);
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
