package com.ui.fitit.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ui.fitit.Adapters.EventAdapter;
import com.ui.fitit.R;
import com.ui.fitit.data.model.Event;
import java.util.List;

public class ScheduleActivity extends AppCompatActivity {

    List<Event> events;
    EventAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_activity);
    }

    private void setEventsAdapter() {
// Android adapter for list view
        adapter = new RoutesAdapter(this, R.layout.list_routes, mViewModel.getRouteOptions());
        allRoutes.setAdapter(adapter);
        allRoutes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (carButton.isSelected() || transitButton.isSelected() || walkButton.isSelected()) {
                    Intent openPaths = new Intent(RoutesActivity.this,
                            PathsActivity.class);
                    DirectionsRoute directionsResult = mViewModel.getDirectionsResult().routes[i];
                    openPaths.putExtra("directionsResult", directionsResult);
                    openPaths.putExtra("shuttle", false);
                    startActivity(openPaths);
                } else {
                    if (mViewModel.getShuttles() == null) {
                        Toast.makeText(getApplicationContext(), "Paths view for Shuttle route is not available if no shuttles exist.", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent openPaths = new Intent(RoutesActivity.this,
                                PathsActivity.class);
                        openPaths.putExtra("shuttle", true);
                        startActivity(openPaths);
                    }
                }
            }
        });
    }
}
