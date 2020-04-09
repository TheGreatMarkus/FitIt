package com.ui.fitit.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.ui.fitit.R;
import com.ui.fitit.data.model.Event;
import com.ui.fitit.data.model.Frequency;

import java.time.LocalDate;
import java.time.LocalTime;

public class NewEventActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    EditText name;
    EditText description;
    EditText hour;
    EditText minute;
    EditText duration;
    EditText location;

    Spinner frequencySpinner;
    Button createEventButton;
    @Frequency
    String frequency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);

        initAllComponents();
    }

    public void initAllComponents() {
        createEventButton = findViewById(R.id.button);
        name = findViewById(R.id.name);
        description = findViewById(R.id.description);
        hour = findViewById(R.id.hour);
        minute = findViewById(R.id.minute);
        duration = findViewById(R.id.eventDuration);
        location = findViewById(R.id.location);

        frequencySpinner = findViewById(R.id.frequencyChoices);
        initSpinner();
        createEventButton.setOnClickListener(v -> onCreateEvent());

    }

    public void initSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.frequency_choices, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        frequencySpinner.setAdapter(adapter);
        frequencySpinner.setOnItemSelectedListener(this);
    }

    public void onCreateEvent() {
        boolean isInputValid = checkInputValidity();
        if (isInputValid) {
            String event_name = name.getText().toString();
            String event_description = description.getText().toString();
            String event_location = location.getText().toString();

            // Compute start time
            int event_hour = Integer.parseInt(hour.getText().toString());
            int event_minute = Integer.parseInt(minute.getText().toString());
            LocalTime event_startTime = LocalTime.of(event_hour, event_minute);

            // Compute end time
            int event_duration = Integer.parseInt(duration.getText().toString());

            event_hour = (event_duration + event_minute >= 60) ? (event_duration + event_minute) / 60 + event_hour : event_hour;
            event_minute = (event_duration + event_minute) % 60;
            LocalTime event_endTime = LocalTime.of(event_hour, event_minute);

            Event event = new Event(event_name, event_description, event_startTime, event_endTime, LocalDate.now(), event_location);
            System.out.println("New event created: " + event);
        }

    }

    public boolean checkInputValidity() {
        int event_hour = Integer.parseInt(hour.getText().toString());
        int event_minute = Integer.parseInt(minute.getText().toString());

        boolean isHourValid = event_hour >= 1 && event_hour <= 24;
        resetComponentState(hour, isHourValid);

        boolean isMinuteValid = event_minute >= 0 && event_minute < 60;
        resetComponentState(minute, isMinuteValid);

        return isHourValid && isMinuteValid;
    }

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
}
