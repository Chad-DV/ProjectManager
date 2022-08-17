package com.example.projecto;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.projecto.dao.UserDAOImpl;
import com.example.projecto.model.User;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private Button registerBtn;
    private EditText firstNameEditText, lastNameEditText, emailAddressEditText, passwordEditText;
    private TextView loginPromptTextView2;
    private ProgressBar registerProgressBar;
    private UserDAOImpl userHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userHelper = new UserDAOImpl(this);
        loginPromptTextView2 = (TextView) findViewById(R.id.loginPromptTextView2);
        registerBtn = (Button) findViewById(R.id.registerBtn);
        firstNameEditText = (EditText) findViewById(R.id.registerFirstNameEditText);
        lastNameEditText = (EditText) findViewById(R.id.registerLastNameEditText);
        emailAddressEditText = (EditText) findViewById(R.id.registerEmailAddressEditText);
        passwordEditText = (EditText) findViewById(R.id.registerPasswordEditText);



        loginPromptTextView2.setOnClickListener(RegisterActivity.this);
        registerBtn.setOnClickListener(RegisterActivity.this);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View view) {
        if(view == loginPromptTextView2) {
            Intent goToLoginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(goToLoginIntent);
        } else if(view == registerBtn) {
            registerUser();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void registerUser() {

        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String emailAddress = emailAddressEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();



        if(validateInput(firstName, lastName, emailAddress, password)) {
            Boolean isValid = userHelper.register(new User(firstName, lastName, emailAddress, password));
            if(isValid) {
                clearInput();
            }
        }
    }

    private void clearInput() {
        firstNameEditText.setText("");
        lastNameEditText.setText("");
        emailAddressEditText.setText("");
        passwordEditText.setText("");
    }

    private boolean validateInput(String firstName, String lastName, String emailAddress, String password) {

        boolean status = true;

        if(TextUtils.isEmpty(firstName)) {
            firstNameEditText.setError("Please enter a value");
            firstNameEditText.requestFocus();
            status = false;
        }

        if(TextUtils.isEmpty(lastName)) {
            lastNameEditText.setError("Please enter a value");
            lastNameEditText.requestFocus();
            status = false;
        }

        if(TextUtils.isEmpty(emailAddress)) {
            emailAddressEditText.setError("Please enter a value");
            emailAddressEditText.requestFocus();
            status = false;
        }

        if(TextUtils.isEmpty(password)) {
            passwordEditText.setError("Please enter a value");
            passwordEditText.requestFocus();
            status = false;
        }

        if(password.length() < 6) {
            passwordEditText.setError("Must be longer than 6 characters");
            passwordEditText.requestFocus();
            status = false;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()) {
            emailAddressEditText.setError("Invalid email address");
            emailAddressEditText.requestFocus();
            status = false;
        }

        return status;
    }
}