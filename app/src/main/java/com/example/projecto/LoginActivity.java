package com.example.projecto;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.example.projecto.dao.UserDAOImpl;
import com.example.projecto.model.User;
import com.example.projecto.utils.DBUtils;

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
//        String emailAddress = "ape1234@gmail.com";
//        String password = "password";
       String password = loginPasswordEditText.getText().toString().trim();



        if (loginRememberMeCheckbox.isChecked()) {
            loginPrefsEditor.putBoolean("saveLogin", true);
            loginPrefsEditor.putString("emailAddress", emailAddress);
            loginPrefsEditor.putString("password", password);
            loginPrefsEditor.commit();
        } else {
            loginPrefsEditor.clear();
            loginPrefsEditor.commit();
        }

        if(validateInput(emailAddress, password)) {
            boolean isValid = userHelper.login(new User(emailAddress, password));

            if(isValid) {
                Intent goToLoginIntent = new Intent(LoginActivity.this, ProjectActivity.class);
                goToLoginIntent.putExtra(DBUtils.AUTHENTICATED_USER, emailAddress);
                startActivity(goToLoginIntent);
                LoginActivity.this.finish();

            }
        }



    }

    private boolean validateInput(String emailAddress, String password) {

        boolean status = true;
        if(TextUtils.isEmpty(emailAddress)) {
            loginEmailAddressEditText.setError("Please enter a value");
            loginEmailAddressEditText.requestFocus();
            status = false;
        }

        if(TextUtils.isEmpty(password)) {
            loginPasswordEditText.setError("Please enter a value");
            loginPasswordEditText.requestFocus();
            status = false;
        }

        return status;
    }
}