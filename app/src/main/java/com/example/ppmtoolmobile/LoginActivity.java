package com.example.ppmtoolmobile;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.example.ppmtoolmobile.dao.UserDAOImpl;
import com.example.ppmtoolmobile.model.User;
import com.example.ppmtoolmobile.utils.DBUtils;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button loginBtn;
    private EditText loginEmailAddressEditText, loginPasswordEditText;
    private TextView registerPromptTextView2;
    private UserDAOImpl userHelper;
    private CheckBox loginRememberMeCheckbox;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    private boolean saveLogin;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userHelper = new UserDAOImpl(this);

        loginBtn = findViewById(R.id.loginBtn);
        loginEmailAddressEditText = findViewById(R.id.loginEmailAddressEditText);
        loginPasswordEditText = findViewById(R.id.loginPasswordEditText);
        registerPromptTextView2 = findViewById(R.id.registerPromptTextView2);
        loginRememberMeCheckbox = findViewById(R.id.loginRememberMeCheckbox);

        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();
        saveLogin = loginPreferences.getBoolean("saveLogin", false);

        if (saveLogin == true) {
            String emailAddress = loginPreferences.getString("emailAddress", "");
            String password = loginPreferences.getString("password", "");
            boolean isValid = userHelper.login(new User(emailAddress, password));
            loginRememberMeCheckbox.setChecked(true);
            if(isValid) {
                Intent goToLoginIntent = new Intent(LoginActivity.this, ProjectActivity.class);
                goToLoginIntent.putExtra(DBUtils.AUTHENTICATED_USER, emailAddress);
                startActivity(goToLoginIntent);
                LoginActivity.this.finish();
            }
        }

        loginBtn.setOnClickListener(this);
        registerPromptTextView2.setOnClickListener(this);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.out.println("going back to login state..");
        finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View view) {
        if(view == loginBtn) {
            loginUser();
        } else if(view == registerPromptTextView2) {
            Intent goToLoginIntent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(goToLoginIntent);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void loginUser() {

       String emailAddress = loginEmailAddressEditText.getText().toString().trim();
//        String emailAddress = "john@gmail.com";
        String password = "password";
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


        if (loginRememberMeCheckbox.isChecked()) {
            loginPrefsEditor.putBoolean("saveLogin", true);
            loginPrefsEditor.putString("emailAddress", emailAddress);
            loginPrefsEditor.putString("password", password);
            loginPrefsEditor.commit();
        } else {
            loginPrefsEditor.clear();
            loginPrefsEditor.commit();
        }


        boolean isValid = userHelper.login(new User(emailAddress, password));

        if(isValid) {
            Intent goToLoginIntent = new Intent(LoginActivity.this, ProjectActivity.class);
            goToLoginIntent.putExtra(DBUtils.AUTHENTICATED_USER, emailAddress);
            startActivity(goToLoginIntent);
            LoginActivity.this.finish();

        }




    }
}