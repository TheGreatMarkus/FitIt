package com.ui.fitit.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ui.fitit.R;
import com.ui.fitit.data.model.User;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference users = db.collection("users/");

    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText fullNameEditText;
    private Button confirmButton;
    private Button switchButton;

    private boolean login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.login_username);
        passwordEditText = findViewById(R.id.login_password);
        fullNameEditText = findViewById(R.id.login_full_name);
        confirmButton = findViewById(R.id.confirm_button);
        switchButton = findViewById(R.id.switch_button);
    }

    @Override
    protected void onStart() {
        super.onStart();
        login = true;
        updateUI();
    }

    public void onConfirm(View view) {
        final String username = this.usernameEditText.getText().toString();
        final String password = this.passwordEditText.getText().toString();

        final String fullName = this.fullNameEditText.getText().toString();
        if (!validateForm(username, password)) {
            return;
        }

        users.document(username).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    User existingUser = documentSnapshot.toObject(User.class);
                    if (login) {
                        attemptLogin(existingUser, username, password);
                    } else {
                        userAlreadyExists(username);
                    }
                } else if (login) {
                    nonExistentUser(username);
                } else {
                    signUpNewUser(username, password, fullName);
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: Error fetching user document!");
            }
        });
    }

    private void signUpNewUser(String username, String password, String fullName) {
        String hashedPassword = hashPassword(password);

        User user = new User(username, hashedPassword, fullName);
        users.document(user.getUsername()).set(user);
        // TODO Redirect to landing page when account creation successful
        Toast.makeText(LoginActivity.this, "User created successfully: " + username, Toast.LENGTH_SHORT).show();
    }

    private void nonExistentUser(String username) {
        usernameEditText.setText("");
        Toast.makeText(LoginActivity.this, "Login failed: User doesn't exist: " + username, Toast.LENGTH_SHORT).show();
    }

    private void userAlreadyExists(String username) {
        usernameEditText.setText("");
        Toast.makeText(LoginActivity.this, "User " + username + " already exists!", Toast.LENGTH_SHORT).show();
    }

    private void attemptLogin(User existingUser, String username, String password) {
        if (existingUser != null
                && existingUser.getHashedPassword() != null
                && existingUser.getHashedPassword().equals(hashPassword(password))) {
            // TODO Redirect to landing page when login successful
            Toast.makeText(LoginActivity.this, "Login Successful. Welcome, " + username, Toast.LENGTH_SHORT).show();
        } else {
            passwordEditText.setText("");
            Toast.makeText(LoginActivity.this, "Password incorrect: " + username, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateForm(String usernameText, String passwordText) {
        if (usernameText.isEmpty()) {
            Toast.makeText(this, "Please write a username", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!usernameText.matches("^[a-zA-Z0-9-]*$")) {
            Toast.makeText(this, "Your username should only have alphanumeric characters and dashes '-'", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (passwordText.length() < 8) {
            Toast.makeText(this, "Your password cannot be shorter than 8 characters", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    public void onSwitch(View view) {
        login = !login;
        updateUI();
    }

    private void updateUI() {
        usernameEditText.setText("");
        passwordEditText.setText("");
        fullNameEditText.setText("");
        if (login) {
            fullNameEditText.setVisibility(View.GONE);
            confirmButton.setText(R.string.login_button_text);
            switchButton.setText(R.string.switch_text_login);

        } else {
            fullNameEditText.setVisibility(View.VISIBLE);
            confirmButton.setText(R.string.sign_up_button_text);
            switchButton.setText(R.string.switch_text_sign_up);
        }
    }

    private String hashPassword(String password) {
        String hashedPassword = "";
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] bytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            hashedPassword = sb.toString();

        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "hashPassword: Error!", e);
        }
        return hashedPassword;
    }
}
