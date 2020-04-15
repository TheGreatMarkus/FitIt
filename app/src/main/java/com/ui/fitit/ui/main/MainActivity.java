package com.ui.fitit.ui.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ui.fitit.Constants;
import com.ui.fitit.R;
import com.ui.fitit.data.model.Feedback;
import com.ui.fitit.data.model.Group;
import com.ui.fitit.data.model.User;
import com.ui.fitit.ui.FeedbackActivity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";
    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    final CollectionReference userCollection = db.collection(Constants.USERS_COLLECTION);
    final CollectionReference groupCollection = db.collection(Constants.GROUPS_COLLECTION);
    CollectionReference feedbackCollection;

    User user;
    Group group;
    List<Feedback> userFeedback = new ArrayList<>();
    Timer timer;
    Handler notificationHandler;

    private DrawerLayout drawer;
    private SharedPreferences sp;
    private UserProfileFragment userProfileFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews(savedInstanceState);

        initInformation();

        notificationHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                Toast.makeText(MainActivity.this, msg.obj.toString(), Toast.LENGTH_LONG).show();
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupFeedbackCheck();
    }


    @Override
    protected void onPause() {
        super.onPause();
        removeFeedbackCheck();
    }


    private void initInformation() {
        sp = getSharedPreferences(Constants.SP_ID, Context.MODE_PRIVATE);
        String username = sp.getString(Constants.SP_USERNAME, null);
        boolean loggedIn = sp.getBoolean(Constants.SP_LOGGED_IN, false);
        Intent i = getIntent();
        user = (User) i.getSerializableExtra(Constants.INTENT_EXTRA_USER);

        if (username == null || !loggedIn || user == null || user.getUsername() == null) {
            Toast.makeText(this, "Error during login. Can't fetch user information! Redirecting to login.", Toast.LENGTH_SHORT).show();
            logout();
        }

        feedbackCollection = userCollection.document(user.getUsername()).collection(Constants.FEEDBACK_COLLECTION);

        feedbackCollection.addSnapshotListener(this, (query, e1) ->
                userFeedback = Objects.requireNonNull(query)
                        .toObjects(Feedback.class)
                        .stream().filter(Objects::nonNull).collect(Collectors.toList())
        );

        userCollection.document(user.getUsername()).addSnapshotListener(this, (document, e) -> {
            User updatedUser = document.toObject(User.class);
            if (updatedUser != null) {
                user = updatedUser;
                userProfileFragment.updateProgressBar(user);
            } else {
                Toast.makeText(this, "User " + user.getUsername() + "no longer exist! Redirecting", Toast.LENGTH_SHORT).show();
                logout();
            }
        });
    }

    private void initViews(Bundle savedInstanceState) {
        userProfileFragment = new UserProfileFragment();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.nav_drawer_open, R.string.nav_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new ScheduleFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_schedule);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_schedule:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ScheduleFragment()).commit();
                break;
            case R.id.nav_user_profile:

                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        userProfileFragment).commit();
                break;
            case R.id.nav_group:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new GroupSearchFragment()).commit();
                break;
            case R.id.nav_settings:
                // TODO implement settings fragment
                Toast.makeText(this, "Opening Settings", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_logout:
                logout();
                break;
            case R.id.nav_feedback:
                Intent intent = new Intent(MainActivity.this, FeedbackActivity.class);
                intent.putExtra(Constants.INTENT_EXTRA_USER, user);
                startActivity(intent);
                break;
        }
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    private void logout() {
        sp.edit().remove(Constants.SP_USERNAME).putBoolean(Constants.SP_LOGGED_IN, false).apply();
        finish();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
        }
    }


    private void checkForFeedback() {
        if (user != null && userFeedback != null) {
            LocalTime currentTime = LocalTime.now();
            boolean todaysFeedbackRequired = currentTime.getHour() >= 16;
            if (todaysFeedbackRequired) {
                if (userFeedback.size() == 0) {
                    notificationHandler.obtainMessage(1, "You have not yet given feedback on your experience! Please open up the side menu and leave feedback for today!").sendToTarget();
                } else {
                    userFeedback.sort((o1, o2) -> o2.compareTo(o1));
                    Feedback latestFeedback = userFeedback.get(0);
                    LocalDate today = LocalDate.now();

                    if (latestFeedback.getDate().toLocalDate().isBefore(today)) {
                        notificationHandler.obtainMessage(1, "Please complete the feedback form for today!").sendToTarget();
                    }

                }
            }
        }
    }

    private void setupFeedbackCheck() {
        Log.d(TAG, "setupFeedbackCheck Called");
        try {
            timer = new Timer();
            TimerTask checkFeedback = new TimerTask() {
                @Override
                public void run() {
                    checkForFeedback();
                }
            };
            timer.schedule(checkFeedback, TimeUnit.SECONDS.toMillis(3), TimeUnit.MINUTES.toMillis(3));
        } catch (Exception e) {
            Log.e(TAG, "onResume: Error starting checkFeedback", e);
        }
    }

    private void removeFeedbackCheck() {
        Log.d(TAG, "removeFeedbackCheck Called");
        try {
            timer.cancel();
        } catch (Exception e) {
            Log.e(TAG, "onResume: Error stopping checkFeedback", e);
        }
    }

}
