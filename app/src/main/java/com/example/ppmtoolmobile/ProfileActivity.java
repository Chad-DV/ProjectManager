package com.example.ppmtoolmobile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.ppmtoolmobile.dao.DaoHelper;
import com.example.ppmtoolmobile.model.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText profileFirstNameEditText, profileLastNameEditText, profileEmailAddressEditText;
    private BottomNavigationView bottomNavView;
    private ImageView profileNavigationBack;
    private DaoHelper daoHelper;
    private String authenticatedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
//
        profileFirstNameEditText = findViewById(R.id.profileFirstNameEditText);
        profileLastNameEditText = findViewById(R.id.profileLastNameEditText);
        profileEmailAddressEditText = findViewById(R.id.profileEmailAddressEditText);
        profileNavigationBack = findViewById(R.id.profileNavigationBack);

        // getting current username through intent from ProjectActivity.class
        authenticatedUser = getIntent().getStringExtra("authenticatedUser");

        Toast.makeText(this, "profile activity: " + authenticatedUser, Toast.LENGTH_SHORT).show();
        daoHelper = new DaoHelper(this);
        loadUserDetails();

        profileNavigationBack.setOnClickListener(this);

        bottomNavView = findViewById(R.id.bottomNavView);
        bottomNavView.setSelectedItemId(R.id.nav_profile);


        // Perform item selected listener
        bottomNavView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId())
                {
                    case R.id.nav_home:
                        finish();
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.nav_profile:
                        return true;
                    case R.id.nav_settings:
                        startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View view) {
        if(view == profileNavigationBack) {
            finish();
        }
    }


    private void loadUserDetails() {
        User user = daoHelper.getUserDetails(authenticatedUser);
        profileFirstNameEditText.setText(user.getFirstName());
        profileLastNameEditText.setText(user.getLastName());
        profileEmailAddressEditText.setText(user.getEmailAddress());



    }


}