package com.example.ppmtoolmobile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ppmtoolmobile.dao.DaoHelper;
import com.example.ppmtoolmobile.model.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {

    private EditText profileFirstNameEditText, profileLastNameEditText, profileEmailAddressEditText;
    private BottomNavigationView bottomNavView;
    private DaoHelper daoHelper;
    private String authenticatedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileFirstNameEditText = findViewById(R.id.profileFirstNameEditText);
        profileLastNameEditText = findViewById(R.id.profileLastNameEditText);
        profileEmailAddressEditText = findViewById(R.id.profileEmailAddressEditText);

        authenticatedUser = getIntent().getStringExtra("authenticatedUser");
//        authenticatedUser = "jake@gmail.com";

        daoHelper = new DaoHelper(this);

        System.out.println("from profile activity: " + authenticatedUser);

        loadUserDetails();

//        userId
        bottomNavView = findViewById(R.id.bottomNavView);
        bottomNavView.setSelectedItemId(R.id.nav_profile);


        // Perform item selected listener
        bottomNavView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId())
                {
                    case R.id.nav_home:
                        startActivity(new Intent(getApplicationContext(), ProjectActivity.class));
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


    private void loadUserDetails() {
        User user = daoHelper.getUserDetails(authenticatedUser);



        profileFirstNameEditText.setText(user.getFirstName());
        profileLastNameEditText.setText(user.getLastName());
        profileEmailAddressEditText.setText(user.getEmailAddress());


    }
}