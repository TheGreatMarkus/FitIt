package com.ui.fitit.ui.main;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.ui.fitit.Constants;
import com.ui.fitit.R;
import com.ui.fitit.data.model.Group;
import com.ui.fitit.data.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class GroupFragment extends Fragment {

    private static final String TAG = "GroupFragment";
    private ConstraintLayout noGroupView;
    private ConstraintLayout existingGroupPage;
    private AutoCompleteTextView groupNameSearchText;
    private ArrayAdapter<String> groupArrayAdapter;
    private TextView groupNameView;
    private ListView listView;

    private CollectionReference groupCollection;
    private MainActivity activity;
    private User user;
    private Group group;
    private List<Group> groups = new ArrayList<>();
    private List<String> groupNames = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group, container, false);

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
    }


    private void initViews(View view) {
        // No Group View
        noGroupView = view.findViewById(R.id.no_group_view);
        groupNameSearchText = view.findViewById(R.id.group_name_search_text);
        Button joinButton = view.findViewById(R.id.group_join_button);
        Button createButton = view.findViewById(R.id.group_create_new_button);

        createButton.setOnClickListener(v -> onGroupJoinAttempt(true));
        joinButton.setOnClickListener(v -> onGroupJoinAttempt(false));

        // Existing Group View
        existingGroupPage = view.findViewById(R.id.existing_group_page);
        groupNameView = view.findViewById(R.id.group_name);
        Button leaveGroupButton = view.findViewById(R.id.group_leave_button);
        Button deleteGroupButton = view.findViewById(R.id.group_delete_button);
        Button seeUsersButton = view.findViewById(R.id.group_see_users);
        Button seeMessagesButton = view.findViewById(R.id.group_see_messages);
        listView = view.findViewById(R.id.group_list_view);

        deleteGroupButton.setOnClickListener(v -> {
            groupCollection.document(group.getId()).delete();
        });
        leaveGroupButton.setOnClickListener(v -> {
            group.removeUser(user.getUsername());
            groupCollection.document(group.getId()).set(group);
        });

        seeUsersButton.setOnClickListener(v -> {
            ArrayAdapter<String> userAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, group.getUsers());
            listView.setAdapter(userAdapter);
        });

        seeMessagesButton.setOnClickListener(v -> {
            List<String> messages = new ArrayList<>(group.getMessages().values());
            ArrayAdapter<String> userAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1, messages);
            listView.setAdapter(userAdapter);
        });

    }

    private void onGroupJoinAttempt(boolean createGroup) {
        String groupName = groupNameSearchText.getText().toString();
        boolean groupExists = groupExists(groupName);
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
        group = groups.stream().filter(g -> g.getName().equals(groupName)).findFirst().orElseGet(() -> {
            Group newGroup = new Group(groupName);
            newGroup.addUser(user.getUsername());
            return newGroup;
        });

        groupCollection.document(group.getId()).set(group);
    }

    private boolean groupExists(String groupName) {
        return groupNames.contains(groupName);
    }


    private void fetchGroupInformation(QuerySnapshot query, FirebaseFirestoreException e) {
        groups = query.toObjects(Group.class);
        groupNames = groups.stream().map(Group::getName).collect(Collectors.toList());
        groupArrayAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, groupNames);
        groupNameSearchText.setAdapter(groupArrayAdapter);

        List<Group> userJoinedGroups = groups.stream().filter(group1 -> group1.getUsers()
                .contains(user.getUsername())).collect(Collectors.toList());
        if (userJoinedGroups.isEmpty()) {
            // User is not in a group
            noGroupView.setVisibility(View.VISIBLE);
            existingGroupPage.setVisibility(View.GONE);
        } else if (userJoinedGroups.size() == 1) {
            // User is already in a group
            group = query.toObjects(Group.class).get(0);
            noGroupView.setVisibility(View.GONE);
            existingGroupPage.setVisibility(View.VISIBLE);
            groupNameView.setText(group.getName());
        } else {
            Log.wtf(TAG, String.format("User %s is part of multiple groups.", user.getUsername()));
        }
    }
}
