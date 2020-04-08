package com.ui.fitit.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.ui.fitit.R;
import com.ui.fitit.data.model.Event;
import com.ui.fitit.data.model.Frequency;

import java.time.LocalTime;

public class CreateEvent extends AppCompatActivity {
    EditText name;
    EditText description;
    EditText time;
    EditText location;

    Spinner frequencySpinner;
    Button createEventButton;
    @Frequency String frequency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        initAllComponents();
    }

    public void initAllComponents(){
        createEventButton = findViewById(R.id.button);
        name = findViewById(R.id.name);
        description = findViewById(R.id.description);
        time = findViewById(R.id.time);
        location = findViewById(R.id.location);

        frequencySpinner = findViewById(R.id.frequencyChoices);
        initSpinner();
        createEventButton.setOnClickListener(v -> onCreateEvent());

//        createEventButton.
    }

    public void initSpinner(){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.frequency_choices, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        frequencySpinner.setAdapter(adapter);
        frequencySpinner.setOnItemClickListener((parent, view, position, id) -> frequency = (String) parent.getItemAtPosition(position));
    }

    public void onCreateEvent(){
        String event_name = name.getText().toString();
        String event_description = description.getText().toString();
        LocalTime event_start = LocalTime.of(0,0,0,0);

        Event event = new Event( );
    }
}
