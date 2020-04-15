package com.ui.fitit.ui.newevent;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class DatePickerFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        NewEventActivity activity = (NewEventActivity) requireActivity();


        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), (DatePickerDialog.OnDateSetListener) getActivity(),
                activity.startDate.getYear(), activity.startDate.getMonth().getValue() - 1, activity.startDate.getDayOfMonth());
    }
}
