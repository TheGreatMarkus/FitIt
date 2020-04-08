package com.ui.fitit.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ui.fitit.R;
import com.ui.fitit.data.model.Event;

import java.util.List;

import androidx.annotation.NonNull;

public class EventAdapter extends ArrayAdapter<Event> {
    private Context context;
    private List<Event> events;

    // UI Elements
    private TextView title;
    private TextView time;
    private TextView location;
    private TextView day;
    private TextView month;

    public EventAdapter(@NonNull Context context, int textViewResourceId, @NonNull List<Event> events) {
        super(context, textViewResourceId, events);
        this.events = events;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View viewToDisplay = LayoutInflater.from(this.getContext()).inflate(R.layout.list_events, parent, false);
        initComponent(viewToDisplay);

        Event event = getItem(position);

        if (event != null)
            setUIElements(event);

        return viewToDisplay;
    }

    private void initComponent(View convertView) {
        title = convertView.findViewById(R.id.eventTitle);
        time = convertView.findViewById(R.id.time);
        location = convertView.findViewById(R.id.location);
        day = convertView.findViewById(R.id.day);
        month = convertView.findViewById(R.id.month);
    }

    @Override
    public Event getItem(int i) {
        return events.get(i);
    }

    @Override
    public int getCount() {
        return events.size();
    }

    public void setUIElements(Event event) {
    }
}
