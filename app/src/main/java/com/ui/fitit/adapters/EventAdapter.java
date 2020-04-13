package com.ui.fitit.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ui.fitit.Constants;
import com.ui.fitit.R;
import com.ui.fitit.data.model.Attendance;
import com.ui.fitit.data.model.Event;
import com.ui.fitit.data.model.FitDate;
import com.ui.fitit.data.model.Frequency;
import com.ui.fitit.data.model.ScheduleItem;
import com.ui.fitit.data.model.Session;

import java.util.List;

public class EventAdapter extends ArrayAdapter<ScheduleItem> {
    private Context context;
    private List<ScheduleItem> scheduleItems;
    private String username;

    // UI Elements
    private TextView itemDay;
    private TextView itemMonth;
    private TextView itemTitle;
    private TextView itemTime;
    private TextView itemLocation;
    private CheckBox itemCompleteCheckbox;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference sessions;


    public EventAdapter(@NonNull Context context, int textViewResourceId, @NonNull List<ScheduleItem> scheduleItems, String username) {
        super(context, textViewResourceId, scheduleItems);
        this.scheduleItems = scheduleItems;
        this.context = context;
        this.username = username;
    }

    @Override
    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.schedule_item, parent, false);
        }
        ScheduleItem item = getItem(position);
        sessions = db.collection(Constants.USERS_COLLECTION).document(username).collection(Constants.SESSION_COLLECTION);

        initComponent(convertView, item);


        if (item != null) {
            setUIElements(item);
        }
        return convertView;
    }

    private void initComponent(View view, ScheduleItem item) {
        itemTitle = view.findViewById(R.id.item_title);
        itemTime = view.findViewById(R.id.item_time);
        itemLocation = view.findViewById(R.id.item_location);
        itemDay = view.findViewById(R.id.item_day);
        itemMonth = view.findViewById(R.id.item_month);
        itemCompleteCheckbox = view.findViewById(R.id.item_complete_checkbox);
        itemCompleteCheckbox.setChecked(false);
        itemCompleteCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Toast.makeText(context, item.getEvent().getFrequency().toString(), Toast.LENGTH_SHORT).show();
                Session session = item.getSession();
                Event event = item.getEvent();

                // Set current session as completed
                item.getSession().setAttendance(Attendance.COMPLETED);
                sessions.document(session.getId()).set(session);

                // Create new session as needed
                if (event.getFrequency() == Frequency.DAILY) {
                    FitDate newDate = new FitDate(session.getDate().toLocalDate().plusDays(1));
                    Session newSession = new Session(newDate, event.getId(), Attendance.UPCOMING);
                    sessions.document(newSession.getId()).set(newSession);
                    Toast.makeText(context, "NEW DAILY", Toast.LENGTH_SHORT).show();
                } else if (event.getFrequency() == Frequency.WEEKLY) {
                    FitDate newDate = new FitDate(session.getDate().toLocalDate().plusDays(7));
                    Session newSession = new Session(newDate, event.getId(), Attendance.UPCOMING);
                    sessions.document(newSession.getId()).set(newSession);
                    Toast.makeText(context, "NEW WEEKLY", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public ScheduleItem getItem(int i) {
        return scheduleItems.get(i);
    }

    @Override
    public int getCount() {
        return scheduleItems.size();
    }

    public void setUIElements(ScheduleItem scheduleItem) {
        itemTitle.setText(scheduleItem.getEvent().getName());
        itemTime.setText(String.format("%s - %s",
                scheduleItem.getEvent().getStartTime().toLocalTime().toString(),
                scheduleItem.getEvent().getEndTime().toLocalTime().toString()));
        itemLocation.setText(scheduleItem.getEvent().getLocation());
        itemDay.setText(String.valueOf(scheduleItem.getSession().getDate().getDay()));
        itemMonth.setText(scheduleItem.getSession().getDate().getMonth().toString());
    }
}
