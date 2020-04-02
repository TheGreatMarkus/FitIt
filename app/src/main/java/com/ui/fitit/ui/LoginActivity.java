package com.ui.fitit.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.ui.fitit.R;

public class LoginActivity extends AppCompatActivity {

    private EditText username;
    private EditText password;
    private Button confirmButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = findViewById(R.id.login_username);
        password = findViewById(R.id.login_password);
        confirmButton = findViewById(R.id.login_confirm_button);


        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usernameText = username.getText().toString();
                String passwordText = password.getText().toString();
                // TODO: Will need to check if user/pass exists in db and check if credentials are correct.
                new AlertDialog.Builder(LoginActivity.this).setTitle("You have pressed the login button")
                        .setMessage("Username: \"" + usernameText + "\" ; Password: \"" + passwordText + "\"").show();

            }
        });
    }
}
