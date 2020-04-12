package com.ui.fitit.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.ui.fitit.R;
import com.ui.fitit.data.model.Session;

import java.util.List;

public class EventAdapter extends ArrayAdapter<Session> {
    private Context context;
    private List<Session> sessions;

    // UI Elements
    private TextView name;
    private TextView time;
    private TextView location;
    private TextView day;
    private TextView month;

    public EventAdapter(@NonNull Context context, int textViewResourceId, @NonNull List<Session> sessions) {
        super(context, textViewResourceId, sessions);
        this.sessions = sessions;
        this.context = context;
    }

    @Override
    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.schedule_item, parent, false);
        }

        initComponent(convertView);

        Session session = getItem(position);

        if (session != null)
            setUIElements(session);

        return convertView;
    }

    private void initComponent(View convertView) {
        name = convertView.findViewById(R.id.eventTitle);
        time = convertView.findViewById(R.id.time);
        location = convertView.findViewById(R.id.location);
        day = convertView.findViewById(R.id.day);
        month = convertView.findViewById(R.id.month);
    }

    @Override
    public Session getItem(int i) {
        return sessions.get(i);
    }

    @Override
    public int getCount() {
        return sessions.size();
    }

    public void setUIElements(Session session) {
        name.setText(session.getEvent().getName());
        time.setText(session.getEvent().getStartTime().toLocalTime().toString() + " - " + session.getEvent().getEndTime().toLocalTime().toString());
        location.setText(session.getEvent().getLocation());
        day.setText(String.valueOf(session.getDate().getDay()));
        month.setText(session.getDate().getMonth().toString());
    }
}
