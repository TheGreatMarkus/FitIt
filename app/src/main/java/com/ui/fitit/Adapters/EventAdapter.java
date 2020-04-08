package com.ui.fitit.Adapters;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.ui.fitit.R;
import com.ui.fitit.data.model.Session;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View viewToDisplay = LayoutInflater.from(this.getContext()).inflate(R.layout.list_sessions, parent, false);
        initComponent(viewToDisplay);

        Session session = getItem(position);

        if (session != null)
            setUIElements(session);

        return viewToDisplay;
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setUIElements(Session session) {
        name.setText(session.getEvent().getName());
        time.setText(session.getEvent().getStart().toString() + " - " + session.getEvent().getEnd().toString());
        location.setText(session.getEvent().getLocation());
        day.setText(String.valueOf(session.getDate().getDayOfMonth()));
        month.setText(session.getDate().getMonth().toString());
    }
}
