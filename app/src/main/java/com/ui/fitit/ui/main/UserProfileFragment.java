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

public class UserProfileFragment extends Fragment {
    TextView userName;
    TextView fullName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        userName = view.findViewById(R.id.profile_username);
        fullName = view.findViewById(R.id.profile_fullname);

        userName.setText("Potato Couch");
        fullName.setText("John Smith");

        return view;
    }


}
