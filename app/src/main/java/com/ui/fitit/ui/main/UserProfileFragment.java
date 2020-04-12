package com.ui.fitit.ui.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ui.fitit.Constants;
import com.ui.fitit.R;
import com.ui.fitit.data.model.User;

public class UserProfileFragment extends Fragment {

    private static final String TAG = "UserProfileFragment";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference users = db.collection("users/");
    private SharedPreferences spLogin;
    private User user;

    private TextView usernameTextView;
    private TextView fullnameTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        Context context = requireContext();
        initViews(view);

        fetchUserInformation(context);

        return view;
    }

    private void initViews(View view) {
        usernameTextView = view.findViewById(R.id.profile_username);
        fullnameTextView = view.findViewById(R.id.profile_full_name);

    }

    private void fetchUserInformation(Context context) {
        spLogin = context.getSharedPreferences(Constants.SP_LOGIN, Context.MODE_PRIVATE);
        if (spLogin.getBoolean(Constants.SP_LOGIN_LOGGED_IN, false)) {
            String username = spLogin.getString(Constants.SP_LOGIN_USERNAME, Constants.SP_LOGIN_NO_USER);
            Log.d(TAG, "initEventData: Username of logged in user: " + username);

            users.document(username).get().addOnSuccessListener(documentSnapshot -> {
                user = documentSnapshot.toObject(User.class);
                populateView(user);
            }).addOnFailureListener(e -> Log.e(TAG, "fetchUserInformation: Error fetching user data!", e));

        }
    }

    private void populateView(User user) {
        usernameTextView.setText(user.getUsername());
        fullnameTextView.setText(user.getFullName());
    }


}
