package com.ui.fitit.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.ui.fitit.Constants;
import com.ui.fitit.R;
import com.ui.fitit.data.model.Group;
import com.ui.fitit.data.model.Message;
import com.ui.fitit.data.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GroupActivity extends AppCompatActivity {

    private static final String TAG = "GroupActivity";
    final FirebaseFirestore db = FirebaseFirestore.getInstance();

    DocumentReference groupDocument;
    CollectionReference messageCollection;


    private boolean showMessages = false;
    private Group group;
    private User user;

    private ArrayAdapter<String> userAdapter;
    private ArrayAdapter<String> messageAdapter;
    private List<String> messageStrings = new ArrayList<>();
    private List<String> users = new ArrayList<>();

    private TextView groupNameView;
    private ListView listView;
    private LinearLayout newMessageLayout;
    private EditText newMessageText;
    private Button newMessageButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        initViews();

        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra(Constants.INTENT_EXTRA_USER);
        group = (Group) intent.getSerializableExtra(Constants.INTENT_EXTRA_GROUP);

        userAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, group.getUsers());
        messageAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, messageStrings);

        groupDocument = db.collection(Constants.GROUPS_COLLECTION).document(group.getId());
        groupDocument.addSnapshotListener(this, this::onGroupUpdate);

        messageCollection = groupDocument.collection(Constants.MESSAGE_COLLECTION);
        messageCollection.addSnapshotListener(this, this::onMessageUpdate);


    }

    private void initViews() {
        // Existing Group View
        groupNameView = findViewById(R.id.group_name);
        Button leaveGroupButton = findViewById(R.id.group_leave_button);
        Button deleteGroupButton = findViewById(R.id.group_delete_button);
        Button seeUsersButton = findViewById(R.id.group_see_users);
        Button seeMessagesButton = findViewById(R.id.group_see_messages);
        listView = findViewById(R.id.group_list_view);
        newMessageLayout = findViewById(R.id.group_new_message_lin_layout);
        newMessageText = findViewById(R.id.group_new_message_text);
        newMessageButton = findViewById(R.id.group_new_message_button);

        deleteGroupButton.setOnClickListener(v -> {
            groupDocument.delete();
            finish();
        });

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
        group = documentSnapshot.toObject(Group.class);
        if (group != null) {
            groupNameView.setText(group.getName());
            users.removeAll(users);
            users.addAll(group.getUsers());
            updateTabView();
        }
    }


    private void onMessageUpdate(QuerySnapshot query, FirebaseFirestoreException e) {
        Log.d(TAG, "onMessageUpdate Called");
        List<Message> m = query.toObjects(Message.class);

        m.sort(Message::compareTo);
        List<String> mStrings = m.stream().map(Message::toString).collect(Collectors.toList());

        messageStrings.removeAll(messageStrings);
        messageStrings.addAll(mStrings);

        updateTabView();

    }


    private void createNewMessage() {
        String messageText = newMessageText.getText().toString();
        if (messageText.isEmpty()) {
            Toast.makeText(this, "You can't send an empty message!", Toast.LENGTH_SHORT).show();
        } else {
            newMessageText.setText("");
            Message message = new Message(System.currentTimeMillis(), messageText, user.getUsername());
            messageCollection.document(message.getId()).set(message);
        }

    }

    private void leaveGroup() {
        group.removeUser(user.getUsername());
        groupDocument.set(group);
        finish();
    }

    private void setShowMessages(boolean showMessages) {
        this.showMessages = showMessages;
        updateTabView();
    }

    private void updateTabView() {
        if (showMessages) {
            newMessageLayout.setVisibility(View.VISIBLE);
            listView.setAdapter(messageAdapter);
        } else {
            newMessageLayout.setVisibility(View.GONE);
            listView.setAdapter(userAdapter);
        }

    }


}