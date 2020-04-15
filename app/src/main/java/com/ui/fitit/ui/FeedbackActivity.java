package com.ui.fitit.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ui.fitit.Constants;
import com.ui.fitit.R;
import com.ui.fitit.data.model.Feedback;
import com.ui.fitit.data.model.FitDate;
import com.ui.fitit.data.model.Motivation;
import com.ui.fitit.data.model.PostSessionFeeling;
import com.ui.fitit.data.model.User;

import java.time.LocalDate;

public class FeedbackActivity extends AppCompatActivity {

    private static final String TAG = "FeedbackActivity";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference users = db.collection(Constants.USERS_COLLECTION);
    private CollectionReference feedbackCollection;

    @Motivation
    private int motivationSelected;
    private double successPercentage;
    @PostSessionFeeling
    private int postSessionFeeling;
    private User user;

    private RadioGroup motivationButtonsGroup;
    private RadioGroup feelingGroup;
    private SeekBar successSeekBar;
    private TextView progressTextView;
    private Button submitButton;
    private CheckBox workedTodayCheckBox;
    private LinearLayout feedbackSuccessQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_feedback);

        initView();
        setupPersistentStorage();
    }

    private void initView() {
        motivationButtonsGroup = findViewById(R.id.motivationButtonsGroup);
        successSeekBar = findViewById(R.id.successSeekbar);
        progressTextView = findViewById(R.id.progress);
        feelingGroup = findViewById(R.id.feelingGroup);
        submitButton = findViewById(R.id.submit_button);
        workedTodayCheckBox = findViewById(R.id.workedTodayCheckBox);
        feedbackSuccessQuestion = findViewById(R.id.progressQuestion);

        setUpMotivationListener();
        setupCheckBoxListener();
        setupSuccessListener();
        setupFeelingsListener();
        setupOnSubmitListener();
    }

    private void setupPersistentStorage() {
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra(Constants.INTENT_EXTRA_USER);
        if (user != null && user.getUsername() != null) {
            feedbackCollection = users.document(user.getUsername()).collection(Constants.FEEDBACK_COLLECTION);
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


    private void setupCheckBoxListener() {
        workedTodayCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            feedbackSuccessQuestion.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            submitButton.setEnabled(isAllQuestionsAnswered());
        });
    }

    private void setupSuccessListener() {
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
            successPercentage = !workedTodayCheckBox.isChecked() ? 0.0 : successPercentage;
            Feedback newFeedback = new Feedback(user.getUsername(), new FitDate(LocalDate.now()), motivationSelected, successPercentage, postSessionFeeling);
            Log.d(TAG, "onSubmitFeedback: New Feedback submitted: " + newFeedback);
            Toast.makeText(this, "Feedback submitted successfully", Toast.LENGTH_SHORT).show();

            this.feedbackCollection.document(newFeedback.getId()).set(newFeedback).addOnFailureListener(e -> {
                Log.d(TAG, "onSubmitFeedback: Error occured while submitting feedback!", e);
            });
            finish();
        });
    }

    private boolean isAllQuestionsAnswered() {
        return motivationSelected != 0 && successPercentage != 0.0 && postSessionFeeling != 0 || motivationSelected != 0 && !workedTodayCheckBox.isChecked() && postSessionFeeling != 0;
    }
}
