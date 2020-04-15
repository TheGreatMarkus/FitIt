package com.ui.fitit.ui.newevent;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class TimePickerFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        NewEventActivity activity = (NewEventActivity) requireActivity();

        return new TimePickerDialog(getActivity(), (TimePickerDialog.OnTimeSetListener) getActivity(),
                activity.startTime.getHour(), activity.startTime.getMinute(), DateFormat.is24HourFormat(getActivity()));
    }
}