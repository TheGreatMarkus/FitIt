package com.ui.fitit.ui.main;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.ui.fitit.Constants;
import com.ui.fitit.R;
import com.ui.fitit.data.model.Group;
import com.ui.fitit.data.model.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GroupFragment extends Fragment {

    private static final String TAG = "GroupActivity";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private MainActivity activity;

    private DocumentReference groupDocument;
    private ListenerRegistration groupDocumentRegistration;
    private CollectionReference messageCollection;
    private ListenerRegistration messageCollectionRegistration;


    private boolean showMessages = false;
    private ArrayAdapter<String> userAdapter;
    private ArrayAdapter<String> messageAdapter;
    private List<String> messageStrings = new ArrayList<>();
    private List<String> users = new ArrayList<>();

    private TextView groupNameView;
    private ListView listView;
    private LinearLayout newMessageLayout;
    private EditText newMessageText;
    private Button newMessageButton;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_group, container, false);

        activity = (MainActivity) requireActivity();

        if (activity.user == null || activity.user.getUsername() == null
                || activity.group == null || activity.group.getId() == null) {
            Toast.makeText(activity, "Unexpected State! Going back to the schedule view.", Toast.LENGTH_SHORT).show();
            exitGroupView();
        }

        initViews(view);

        userAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, users);
        messageAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, messageStrings);

        groupDocument = db.collection(Constants.GROUPS_COLLECTION).document(activity.group.getId());


        messageCollection = groupDocument.collection(Constants.MESSAGE_COLLECTION);


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        groupDocumentRegistration = groupDocument.addSnapshotListener(this::onGroupUpdate);
        messageCollectionRegistration = messageCollection.addSnapshotListener(this::onMessageUpdate);
    }

    @Override
    public void onStop() {
        super.onStop();
        groupDocumentRegistration.remove();
        messageCollectionRegistration.remove();
    }

    private void initViews(View view) {
        // Existing Group View
        groupNameView = view.findViewById(R.id.group_name);
        Button leaveGroupButton = view.findViewById(R.id.group_leave_button);
        Button seeUsersButton = view.findViewById(R.id.group_see_users);
        Button seeMessagesButton = view.findViewById(R.id.group_see_messages);
        listView = view.findViewById(R.id.group_list_view);
        newMessageLayout = view.findViewById(R.id.group_new_message_lin_layout);
        newMessageText = view.findViewById(R.id.group_new_message_text);
        newMessageButton = view.findViewById(R.id.group_new_message_button);


        leaveGroupButton.setOnClickListener(v -> {
            leaveGroup();
        });

        seeUsersButton.setOnClickListener(v -> setShowMessages(false));
        seeMessagesButton.setOnClickListener(v -> setShowMessages(true));

        newMessageButton.setOnClickListener(v -> {
            createNewMessage();
        });

    }

    private void onGroupUpdate(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
        Log.d(TAG, "onGroupUpdate Called");

        Group newGroup = documentSnapshot.toObject(Group.class);
        if (newGroup != null) {
            activity.group = newGroup;
            Log.d(TAG, "Users: " + activity.group.getUsers());
            groupNameView.setText(activity.group.getName());

            users.removeAll(users);
            users.addAll(activity.group.getUsers());
            updateTabView();
        } else {
            Toast.makeText(activity, "Group no longer exists! Exiting", Toast.LENGTH_SHORT).show();
            exitGroupView();
        }
    }


    private void onMessageUpdate(QuerySnapshot query, FirebaseFirestoreException e) {
        Log.d(TAG, "onMessageUpdate Called");
        List<Message> m = query.toObjects(Message.class);

        m.sort(Message::compareTo);
        List<String> mStrings = m.stream().map(Message::toString).collect(Collectors.toList());

        boolean b = messageStrings.removeAll(messageStrings);
        messageStrings.addAll(mStrings);

        updateTabView();

    }


    private void createNewMessage() {
        String messageText = newMessageText.getText().toString();
        if (messageText.isEmpty()) {
            Toast.makeText(activity, "You can't send an empty message!", Toast.LENGTH_SHORT).show();
        } else {
            newMessageText.setText("");
            Message message = new Message(System.currentTimeMillis(), messageText, activity.user.getUsername());
            messageCollection.document(message.getId()).set(message);
        }

    }

    private void leaveGroup() {
        activity.group.removeUser(activity.user.getUsername());
        groupDocument.set(activity.group);

        Message leaveMessage = new Message(System.currentTimeMillis(), String.format("User %s has left the group.", activity.user.getUsername()), "FitIt");
        messageCollection.add(leaveMessage);
        exitGroupView();
    }

    private void setShowMessages(boolean showMessages) {
        this.showMessages = showMessages;
        updateTabView();
    }

    private void updateTabView() {
        if (showMessages) {
            newMessageLayout.setVisibility(View.VISIBLE);
            listView.setAdapter(messageAdapter);
            messageAdapter.notifyDataSetChanged();
        } else {
            newMessageLayout.setVisibility(View.GONE);
            listView.setAdapter(userAdapter);
            userAdapter.notifyDataSetChanged();
        }

    }

    private void exitGroupView() {
        activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new ScheduleFragment()).commit();
    }
}