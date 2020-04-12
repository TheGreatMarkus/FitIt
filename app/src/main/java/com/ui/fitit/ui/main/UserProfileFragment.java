package com.ui.fitit.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ui.fitit.R;
import com.ui.fitit.data.model.User;

public class UserProfileFragment extends Fragment {

    private static final String TAG = "UserProfileFragment";


    private TextView usernameTextView;
    private TextView fullNameTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        initViews(view);

        MainActivity activity = (MainActivity) requireActivity();
        fetchUserInformation(activity);

        return view;
    }


    private void initViews(View view) {
        usernameTextView = view.findViewById(R.id.profile_username);
        fullNameTextView = view.findViewById(R.id.profile_full_name);

    }

    private void fetchUserInformation(MainActivity activity) {
        populateView(activity.user);


    }

    private void populateView(User user) {
        usernameTextView.setText(user.getUsername());
        fullNameTextView.setText(user.getFullName());
    }


}
