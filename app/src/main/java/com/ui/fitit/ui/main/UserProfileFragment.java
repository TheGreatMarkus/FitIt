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
    private ProgressBar progressBar;
    private MainActivity activity;
    private TextView levelTextView;
    private TextView pointTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        activity = (MainActivity) requireActivity();
        initViews(view);

        return view;
    }

    public void updateProgressBar(User user) {
        if (progressBar != null && user != null && levelTextView != null && pointTextView != null) {
            progressBar.setProgress(user.getLevelProgress().intValue());
            levelTextView.setText(activity.user.getLevel().toString());
            pointTextView.setText(activity.user.getPoints() + " " + activity.getString(R.string.points_unit));
        }
    }

    private void initViews(View view) {
        TextView usernameTextView = view.findViewById(R.id.profile_username);
        TextView fullNameTextView = view.findViewById(R.id.profile_full_name);
        levelTextView = view.findViewById(R.id.profile_level_text);
        pointTextView = view.findViewById(R.id.point_text);
        progressBar = view.findViewById(R.id.profile_progress_bar);

        usernameTextView.setText(activity.user.getUsername());
        fullNameTextView.setText(activity.user.getFullName());
        updateProgressBar(activity.user);


    }


}
