package com.ui.fitit.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.ui.fitit.R;
import com.ui.fitit.data.model.Event;
import java.util.List;

public class ScheduleActivity extends AppCompatActivity {

    List<Event> events;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_activity);
    }

    private void initComponents() {

    }
}
