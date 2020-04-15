package com.ui.fitit.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.ui.fitit.R;
import com.ui.fitit.data.model.Attendance;
import com.ui.fitit.data.model.ScheduleItem;
import com.ui.fitit.ui.main.ScheduleFragment;

public class EventAdapter extends ArrayAdapter<ScheduleItem> {

    private ScheduleFragment scheduleFragment;
    private Context context;


    public EventAdapter(@NonNull Context context, ScheduleFragment scheduleFragment, int textViewResourceId) {
        super(context, textViewResourceId, scheduleFragment.scheduleItems);
        this.scheduleFragment = scheduleFragment;
        this.context = context;
    }

    @Override
    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.schedule_item, parent, false);
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
                scheduleFragment.onSessionAttended(item.getEvent(), item.getSession(), Attendance.COMPLETED);
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
