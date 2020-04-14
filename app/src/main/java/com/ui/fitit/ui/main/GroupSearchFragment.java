package com.ui.fitit.ui.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.ui.fitit.Constants;
import com.ui.fitit.R;
import com.ui.fitit.data.model.Group;
import com.ui.fitit.data.model.User;
import com.ui.fitit.ui.GroupActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


public class GroupSearchFragment extends Fragment {

    private static final String TAG = "GroupFragment";

    private AutoCompleteTextView groupNameSearchText;

    private SharedPreferences sp;
    private CollectionReference groupCollection;
    private MainActivity activity;
    private User user;
    private List<Group> groups = new ArrayList<>();
    private List<String> groupNames = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_find_group, container, false);

        initViews(view);

        activity = (MainActivity) requireActivity();
        user = activity.user;

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        groupCollection = activity.db.collection(Constants.GROUPS_COLLECTION);
        groupCollection.addSnapshotListener(activity, this::fetchGroupInformation);

        groupCollection.whereArrayContains("users", user.getUsername()).get().addOnSuccessListener(query -> {
            Log.d(TAG, "onStart: Looking to see if user is already in a group");
            List<Group> groupsWithUser = query.toObjects(Group.class);
            if (groupsWithUser.size() == 1) {
                Group group = groupsWithUser.get(0);
                if (group != null && group.getId() != null) {
                    Log.d(TAG, "onStart: User already in a group. Going to activity");
                    goToGroupActivity(group);
                }

            }
        });
    }

    private void goToGroupActivity(@NonNull Group group) {
        Log.d(TAG, "goToGroupActivity Called");
        Intent intent = new Intent(activity, GroupActivity.class);
        intent.putExtra(Constants.INTENT_EXTRA_USER, user);
        intent.putExtra(Constants.INTENT_EXTRA_GROUP, group);
        activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new ScheduleFragment()).commit();
        startActivity(intent);

    }


    private void onGroupJoinAttempt(boolean createGroup) {
        String groupName = groupNameSearchText.getText().toString();
        if (!groupName.matches("^[a-zA-Z0-9-_ ]*$")) {
            Toast.makeText(activity, "Only alphanumeric characters for group names!", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean groupExists = groupNames.contains(groupName);
        if (groupExists != createGroup) {
            Toast.makeText(activity, "Joining Group!", Toast.LENGTH_SHORT).show();
            joinGroup(groupName);
        } else if (!groupExists) {
            Toast.makeText(activity, "Group doesn't exist!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(activity, "Group already exists!", Toast.LENGTH_SHORT).show();
        }
    }


    private void joinGroup(String groupName) {
        Group group = groups.stream().filter(g -> g.getName().equals(groupName)).findFirst()
                .orElseGet(() -> new Group(groupName));
        group.addUser(user.getUsername());
        groupCollection.document(group.getId()).set(group);
        goToGroupActivity(group);


    }


    private void fetchGroupInformation(QuerySnapshot query, FirebaseFirestoreException e) {
        Log.d(TAG, "fetchGroupInformation Called");
        groups = query.toObjects(Group.class).stream().filter(Objects::nonNull).collect(Collectors.toList());
        groupNames = groups.stream().map(Group::getName).collect(Collectors.toList());
        ArrayAdapter<String> groupArrayAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, groupNames);
        groupNameSearchText.setAdapter(groupArrayAdapter);
    }


    private void initViews(View view) {
        // No Group View
        groupNameSearchText = view.findViewById(R.id.group_name_search_text);
        Button joinButton = view.findViewById(R.id.group_join_button);
        Button createButton = view.findViewById(R.id.group_create_new_button);

        createButton.setOnClickListener(v -> onGroupJoinAttempt(true));
        joinButton.setOnClickListener(v -> onGroupJoinAttempt(false));


    }
}
