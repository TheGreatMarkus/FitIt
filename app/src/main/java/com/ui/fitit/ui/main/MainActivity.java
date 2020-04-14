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
import com.ui.fitit.SPUtilities;
import com.ui.fitit.data.model.Feedback;
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
    User user;
    CollectionReference feedbackCollection;
    List<Feedback> userFeedback = new ArrayList<>();
    Timer timer;
    Handler notificationHandler;
    private DrawerLayout drawer;
    private SharedPreferences sp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initViews(savedInstanceState);

        fetchUserInformation();


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
        Log.d(TAG, "onResume Called");
        super.onResume();

        try {
            timer = new Timer();
            TimerTask checkFeedback = new TimerTask() {
                @Override
                public void run() {
                    checkForFeedback();
                }
            };
            timer.schedule(checkFeedback, TimeUnit.SECONDS.toMillis(3), TimeUnit.SECONDS.toMillis(5));
        } catch (Exception e) {
            Log.e(TAG, "onResume: Error starting checkFeedback", e);
        }

    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause Called");
        super.onPause();
        try {
            timer.cancel();
        } catch (Exception e) {
            Log.e(TAG, "onResume: Error stopping checkFeedback", e);
        }
    }


    private void checkForFeedback() {
        if (user != null && userFeedback != null) {
            LocalTime currentTime = LocalTime.now();
            boolean todaysFeedbackRequired = currentTime.getHour() >= 18;
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


    private void fetchUserInformation() {
        sp = getSharedPreferences(SPUtilities.SP_ID, Context.MODE_PRIVATE);
        String spUsername = SPUtilities.getLoggedInUserName(sp);

        Intent i = getIntent();
        user = (User) i.getSerializableExtra(Constants.INTENT_EXTRA_USER);


        userCollection.document(spUsername).addSnapshotListener(this, (document, e) -> {
            User updatedUser = document.toObject(User.class);
            if (updatedUser == null || spUsername.equals(SPUtilities.SP_NO_USER)) {
                Toast.makeText(this, "Unexpected state. Going back to login screen.", Toast.LENGTH_SHORT).show();
                logout();
            } else {
                user = document.toObject(User.class);
                feedbackCollection = userCollection.document(spUsername).collection(Constants.FEEDBACK_COLLECTION);
                feedbackCollection.addSnapshotListener((query, e1) ->
                        userFeedback = Objects.requireNonNull(query)
                                .toObjects(Feedback.class)
                                .stream().filter(Objects::nonNull).collect(Collectors.toList()));
            }
        });
    }

    private void initViews(Bundle savedInstanceState) {
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
                        new UserProfileFragment()).commit();
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
                startActivity(new Intent(MainActivity.this, FeedbackActivity.class));
                break;
        }
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    private void logout() {
        sp.edit().remove(SPUtilities.SP_USERNAME).putBoolean(SPUtilities.SP_LOGGED_IN, false).apply();
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


}
