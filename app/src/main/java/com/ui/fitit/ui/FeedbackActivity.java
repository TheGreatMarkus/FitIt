package com.ui.fitit.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ui.fitit.Constants;
import com.ui.fitit.R;
import com.ui.fitit.SPUtilities;
import com.ui.fitit.data.model.Feedback;
import com.ui.fitit.data.model.FitDate;
import com.ui.fitit.data.model.Motivation;
import com.ui.fitit.data.model.PostSessionFeeling;

import java.time.LocalDate;

public class FeedbackActivity extends AppCompatActivity {

    private static final String TAG = "FeedbackActivity";
    RadioGroup motivationButtonsGroup;
    RadioGroup feelingGroup;
    SeekBar successSeekBar;
    TextView progressTextView;
    Button submitButton;

    @Motivation
    int motivationSelected;
    double successPercentage;
    @PostSessionFeeling
    int postSessionFeeling;


    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference users = db.collection(Constants.USERS_COLLECTION);
    private CollectionReference feedback;
    private SharedPreferences spLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_feedback);

        // Set the form to be 50% of the screen
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * 0.9), (int) (height * 0.5));

        init();
        setupPersistentStorage();
    }

    private void init() {
        motivationButtonsGroup = findViewById(R.id.motivationButtonsGroup);
        successSeekBar = findViewById(R.id.successSeekbar);
        progressTextView = findViewById(R.id.progress);
        feelingGroup = findViewById(R.id.feelingGroup);
        submitButton = findViewById(R.id.submit_button);

        setUpMotivationListener();
        setupSucessListener();
        setupFeelingsListener();
        setupOnSubmitListener();
    }

    private void setupPersistentStorage() {
        spLogin = getSharedPreferences(SPUtilities.SP_ID, Context.MODE_PRIVATE);
        String username = SPUtilities.getLoggedInUserName(spLogin);
        if (!username.equals(SPUtilities.SP_NO_USER)) {
            feedback = users.document(username).collection(Constants.FEEDBACK_COLLECTION);
        } else {
            Toast.makeText(this, "Unexpected state. You are not logged in. Redirecting to main screen", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setIconAsSelected(int id) {
        findViewById(id).getBackground().setTint(getColor(R.color.colorAccent));
    }

    private void setIconAsUnselected(int id) {
        findViewById(id).getBackground().setTint(getColor(R.color.black));
    }

    private void setUpMotivationListener() {
        motivationButtonsGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.m1_button:
                    motivationSelected = Motivation.L1;
                    break;
                case R.id.m2_button:
                    motivationSelected = Motivation.L2;
                    break;
                case R.id.m3_button:
                    motivationSelected = Motivation.L3;
                    break;
                case R.id.m4_button:
                    motivationSelected = Motivation.L4;
                    break;
                case R.id.m5_button:
                    motivationSelected = Motivation.L5;
                    break;
                default:
                    break;
            }
            submitButton.setEnabled(isAllQuestionsAnswered());
        });
    }

    private void setupSucessListener() {
        successSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                successPercentage = progress;
                progressTextView.setText(progress + "%");
                submitButton.setEnabled(isAllQuestionsAnswered());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void setupFeelingsListener() {

        feelingGroup.setOnCheckedChangeListener((group, checkedId) -> {
            setIconAsSelected(checkedId);
            switch (checkedId) {
                case R.id.notWellButton:
                    postSessionFeeling = PostSessionFeeling.NOT_WELL;
                    setIconAsUnselected(R.id.neutralButton);
                    setIconAsUnselected(R.id.wellButton);
                    break;
                case R.id.neutralButton:
                    postSessionFeeling = PostSessionFeeling.NEUTRAL;
                    setIconAsUnselected(R.id.notWellButton);
                    setIconAsUnselected(R.id.wellButton);
                    break;
                case R.id.wellButton:
                    postSessionFeeling = PostSessionFeeling.WELL;
                    setIconAsUnselected(R.id.notWellButton);
                    setIconAsUnselected(R.id.neutralButton);
                    break;
            }
            submitButton.setEnabled(isAllQuestionsAnswered());
        });
    }

    private void setupOnSubmitListener() {
        submitButton.setOnClickListener(v -> {
            String userName = SPUtilities.getLoggedInUserName(getSharedPreferences(SPUtilities.SP_ID, Context.MODE_PRIVATE));
            Feedback newFeedback = new Feedback(userName, new FitDate(LocalDate.now()), motivationSelected, successPercentage, postSessionFeeling);
            Log.d(TAG, "onSubmitFeedback: New Feedback submitted: " + newFeedback);
            Toast.makeText(this, "Feedback submitted successfully", Toast.LENGTH_SHORT).show();

            this.feedback.document(newFeedback.getId()).set(newFeedback).addOnFailureListener(e -> {
                Log.d(TAG, "onSubmitFeedback: Error occured while submitting feedback!", e);
            });
            finish();
        });
    }

    private boolean isAllQuestionsAnswered() {
        return motivationSelected != 0 && successPercentage != 0.0 && postSessionFeeling != 0;
    }
}
