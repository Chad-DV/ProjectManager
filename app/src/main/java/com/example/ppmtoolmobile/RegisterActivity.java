package com.example.ppmtoolmobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ppmtoolmobile.dao.DaoHelper;
import com.example.ppmtoolmobile.model.User;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private Button registerBtn;
    private EditText firstNameEditText, lastNameEditText, emailAddressEditText, passwordEditText;
    private TextView loginPromptTextView2;
    private ProgressBar registerProgressBar;
    private DaoHelper daoHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        daoHelper = new DaoHelper(this);

        loginPromptTextView2 = (TextView) findViewById(R.id.loginPromptTextView2);
        registerBtn = (Button) findViewById(R.id.registerBtn);
        firstNameEditText = (EditText) findViewById(R.id.registerFirstNameEditText);
        lastNameEditText = (EditText) findViewById(R.id.registerLastNameEditText);
        emailAddressEditText = (EditText) findViewById(R.id.registerEmailAddressEditText);
        passwordEditText = (EditText) findViewById(R.id.registerPasswordEditText);

        loginPromptTextView2.setOnClickListener(RegisterActivity.this);
        registerBtn.setOnClickListener(RegisterActivity.this);
    }

    @Override
    public void onClick(View view) {
        if(view == loginPromptTextView2) {
            Intent goToLoginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(goToLoginIntent);
        } else if(view == registerBtn) {
            registerUser();
        }
    }

    private void registerUser() {

        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String emailAddress = emailAddressEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if(TextUtils.isEmpty(firstName)) {
            firstNameEditText.setError("Please enter a value");
            firstNameEditText.requestFocus();
            return;
        }

        if(TextUtils.isEmpty(lastName)) {
            lastNameEditText.setError("Please enter a value");
            lastNameEditText.requestFocus();
            return;
        }

        if(TextUtils.isEmpty(emailAddress)) {
            emailAddressEditText.setError("Please enter a value");
            emailAddressEditText.requestFocus();
            return;
        }

        if(TextUtils.isEmpty(password)) {
            passwordEditText.setError("Please enter a value");
            passwordEditText.requestFocus();
            return;
        }

        if(password.length() < 6) {
            passwordEditText.setError("Must be longer than 6 characters");
            passwordEditText.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()) {
            emailAddressEditText.setError("Invalid email address");
            emailAddressEditText.requestFocus();
            return;
        }

        if(!daoHelper.isEmailExists(emailAddress)) {
            daoHelper.register(new User(firstName, lastName, emailAddress, password));
        } else {
            Toast.makeText(this, "User with email " + emailAddress + " already exists!", Toast.LENGTH_LONG).show();
        }







    }
}