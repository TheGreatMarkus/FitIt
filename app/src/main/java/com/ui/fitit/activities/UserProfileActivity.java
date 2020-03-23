package com.ui.fitit.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ui.fitit.R;

public class UserProfileActivity extends AppCompatActivity {

    TextView userName;
    TextView fullName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        userName = findViewById(R.id.user_name);
        fullName = findViewById(R.id.full_name);

        userName.setText("Potato Couch");
        fullName.setText("John Smith");
    }
}
