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
        final String usernameText = this.usernameEditText.getText().toString();
        final String passwordText = this.passwordEditText.getText().toString();
        this.passwordEditText.setText("");
        final String fullNameText = this.fullNameEditText.getText().toString();
        if (usernameText.isEmpty() || passwordText.isEmpty()) {
            Toast.makeText(this, "Please fill in both the username and password fields", Toast.LENGTH_SHORT).show();
            return;
        }

        users.document(usernameText).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    User existingUser = documentSnapshot.toObject(User.class);
                    if (login && existingUser != null
                            && existingUser.getHashedPassword() != null
                            && existingUser.getHashedPassword().equals(hashPassword(passwordText))) {
                        Toast.makeText(LoginActivity.this, "Login Successful. Welcome, " + usernameText, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "Password incorrect: " + usernameText, Toast.LENGTH_SHORT).show();
                    }
                } else if (login) {
                    Toast.makeText(LoginActivity.this, "Login failed: User doesn't exist: " + usernameText, Toast.LENGTH_SHORT).show();
                } else {
                    String hashedPassword = hashPassword(passwordText);

                    User user = new User(usernameText, hashedPassword, fullNameText);
                    users.document(user.getUsername()).set(user);
                    Toast.makeText(LoginActivity.this, "User created successfully " + usernameText, Toast.LENGTH_SHORT).show();

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: Error fetching user document!");
            }
        });
    }


    public void onSwitch(View view) {
        login = !login;
        updateUI();
    }

    private void updateUI() {
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
