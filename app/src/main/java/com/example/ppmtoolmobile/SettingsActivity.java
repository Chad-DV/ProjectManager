package com.example.ppmtoolmobile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    private BottomNavigationView bottomNavView;
    private CardView settingsProfileCardView, settingsMyProjectsCardView, settingsNotificationsCardView, settingsLogoutCardView;
    private ImageView settingsNavigationBack;
    private String authenticatedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        bottomNavView = findViewById(R.id.bottomNavView);
        settingsNavigationBack = findViewById(R.id.settingsNavigationBack);
        settingsProfileCardView = findViewById(R.id.settingsProfileCardView);
        settingsMyProjectsCardView = findViewById(R.id.settingsMyProjectsCardView);
        settingsNotificationsCardView = findViewById(R.id.settingsNotificationsCardView);
        settingsLogoutCardView = findViewById(R.id.settingsLogoutCardView);

        authenticatedUser = getIntent().getStringExtra("authenticatedUser");

        Toast.makeText(this, "settings activity: " + authenticatedUser, Toast.LENGTH_SHORT).show();

        bottomNavView.setSelectedItemId(R.id.nav_settings);

        settingsNavigationBack.setOnClickListener(this);
        settingsProfileCardView.setOnClickListener(this);
        settingsMyProjectsCardView.setOnClickListener(this);
        settingsNotificationsCardView.setOnClickListener(this);
        settingsLogoutCardView.setOnClickListener(this);

        // Perform item selected listener
        bottomNavView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId())
                {
                    case R.id.nav_profile:
                        Intent goToProfileActivityIntent = new Intent(SettingsActivity.this, ProfileActivity.class);
                        moveToIntent(goToProfileActivityIntent);
                        return true;
                    case R.id.nav_home:
                        Intent goToProjectActivityIntent = new Intent(SettingsActivity.this, ProjectActivity.class);
                        moveToIntent(goToProjectActivityIntent);
                        return true;
                    case R.id.nav_settings:
                        return true;
                }
                return false;
            }
        });


    }

    private void moveToIntent(Intent intent) {
//        Intent goToSettingsActivityIntent = new Intent(ProjectActivity.this, ProfileActivity.class);

        intent.putExtra("authenticatedUser", authenticatedUser);
        startActivity(intent);
        overridePendingTransition(0,0);
    }

    @Override
    public void onClick(View view) {
        if(view == settingsNavigationBack) {
            finish();
        } else if(view == settingsProfileCardView) {
            Intent goToProfileActivityIntent = new Intent(SettingsActivity.this, ProfileActivity.class);
            moveToIntent(goToProfileActivityIntent);
        } else if(view == settingsMyProjectsCardView) {
            Intent goToProjectActivityIntent = new Intent(SettingsActivity.this, ProjectActivity.class);
            moveToIntent(goToProjectActivityIntent);
        } else if(view == settingsNotificationsCardView) {

        } else if(view == settingsLogoutCardView) {

        }
    }
}