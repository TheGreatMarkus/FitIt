package com.ui.fitit.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.WriteBatch;
import com.ui.fitit.R;
import com.ui.fitit.data.model.Attendance;
import com.ui.fitit.data.model.ScheduleItem;
import com.ui.fitit.ui.main.MainActivity;
import com.ui.fitit.ui.main.ScheduleFragment;

import java.util.concurrent.atomic.AtomicInteger;

public class EventAdapter extends ArrayAdapter<ScheduleItem> {

    private ScheduleFragment scheduleFragment;
    private MainActivity activity;


    public EventAdapter(@NonNull MainActivity activity, ScheduleFragment scheduleFragment, int textViewResourceId) {
        super(activity, textViewResourceId, scheduleFragment.scheduleItems);
        this.scheduleFragment = scheduleFragment;
        this.activity = activity;

    }

    @Override
    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(activity).inflate(R.layout.schedule_item, parent, false);
        }
        ScheduleItem item = getItem(position);
        if (item != null) {
            initComponent(convertView, item);
        }

        return convertView;
    }

    private void initComponent(View view, ScheduleItem item) {
        // Find elements
        TextView itemTitle = view.findViewById(R.id.item_title);
        TextView itemTime = view.findViewById(R.id.item_time);
        TextView itemLocation = view.findViewById(R.id.item_location);
        // UI Elements
        TextView itemDay = view.findViewById(R.id.item_day);
        TextView itemMonth = view.findViewById(R.id.item_month);

        // Set element values
        itemTitle.setText(String.format("%s - %s", item.getName(), item.getAttendance()));
        // itemTitle.setText(String.format("%s - %s - %s", item.getName(), item.getFrequency(), item.getAttendance()));
        itemTime.setText(String.format("%s - %s",
                item.getStartTime().toString(),
                item.getEndTime().toString()));
        itemLocation.setText(item.getLocation());
        itemDay.setText(String.valueOf(item.getDate().getDayOfMonth()));
        itemMonth.setText(item.getDate().getMonth().toString());

        CheckBox itemCompleteCheckbox = view.findViewById(R.id.item_complete_checkbox);
        if (item.getAttendance() != Attendance.UPCOMING) {
            itemCompleteCheckbox.setVisibility(View.GONE);
        } else {
            itemCompleteCheckbox.setVisibility(View.VISIBLE);
        }
        itemCompleteCheckbox.setChecked(false);
        itemCompleteCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                WriteBatch batch = activity.db.batch();
                AtomicInteger updates = new AtomicInteger(1);
                scheduleFragment.onSessionAttended(item.getEvent(), item.getSession(), Attendance.COMPLETED, batch, updates);
            }
        });
    }

    @Override
    public ScheduleItem getItem(int i) {
        return scheduleFragment.scheduleItems.get(i);
    }

    @Override
    public int getCount() {
        return scheduleFragment.scheduleItems.size();
    }

}
