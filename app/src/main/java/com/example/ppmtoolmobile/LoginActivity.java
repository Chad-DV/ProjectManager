package com.example.ppmtoolmobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.ppmtoolmobile.dao.ProjectAndUserDAOImpl;
import com.example.ppmtoolmobile.model.User;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button loginBtn;
    private EditText loginEmailAddressEditText, loginPasswordEditText;
    private TextView registerPromptTextView2;
    private ProjectAndUserDAOImpl databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        databaseHelper = new ProjectAndUserDAOImpl(this);

        loginBtn = (Button) findViewById(R.id.loginBtn);
        loginEmailAddressEditText = (EditText) findViewById(R.id.loginEmailAddressEditText);
        loginPasswordEditText = (EditText) findViewById(R.id.loginPasswordEditText);
        registerPromptTextView2 = (TextView) findViewById(R.id.registerPromptTextView2);

        loginBtn.setOnClickListener(this);
        registerPromptTextView2.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        if(view == loginBtn) {
            loginUser();
        } else if(view == registerPromptTextView2) {
            Intent goToLoginIntent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(goToLoginIntent);
        }
    }

    private void loginUser() {

//        String emailAddress = loginEmailAddressEditText.getText().toString().trim();
        String emailAddress = "johnz@gmail.com";
        String password = loginPasswordEditText.getText().toString().trim();
//
//        if(TextUtils.isEmpty(emailAddress)) {
//            loginEmailAddressEditText.setError("Please enter a value");
//            loginEmailAddressEditText.requestFocus();
//            return;
//        }
//
//        if(TextUtils.isEmpty(password)) {
//            loginPasswordEditText.setError("Please enter a value");
//            loginPasswordEditText.requestFocus();
//            return;
//        }
//
//        if(!Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()) {
//            loginEmailAddressEditText.setError("Invalid email address");
//            loginEmailAddressEditText.requestFocus();
//            return;
//        }

        boolean isValid = databaseHelper.login(new User(emailAddress, "password"));

        if(isValid) {
            Intent goToLoginIntent = new Intent(LoginActivity.this, ProjectActivity.class);
            goToLoginIntent.putExtra("authenticatedUser", emailAddress);
            startActivity(goToLoginIntent);
        } else {
            System.out.println("Failure");
        }





    }
}