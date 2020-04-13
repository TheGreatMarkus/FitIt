package com.ui.fitit.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ui.fitit.R;
import com.ui.fitit.data.model.User;

public class UserProfileFragment extends Fragment {

    private static final String TAG = "UserProfileFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        MainActivity activity = (MainActivity) requireActivity();
        initViews(view, activity.user);

        return view;
    }


    private void initViews(View view, User user) {
        TextView usernameTextView = view.findViewById(R.id.profile_username);
        TextView fullNameTextView = view.findViewById(R.id.profile_full_name);
        TextView levelTextView = view.findViewById(R.id.profile_level_text);
        ProgressBar progressBar = view.findViewById(R.id.profile_progress_bar);

        usernameTextView.setText(user.getUsername());
        fullNameTextView.setText(user.getFullName());
        levelTextView.setText(user.getLevel().toString());
        progressBar.setProgress(user.getLevelProgress().intValue());


    }


}
