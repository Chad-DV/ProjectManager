package com.example.ppmtoolmobile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    private BottomNavigationView bottomNavView;
    private CardView settingsProfileCardView, settingsMyProjectsCardView, settingsNotificationsCardView, settingsLogoutCardView;
    private ImageView settingsNavigationBack;

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
                        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.nav_home:
                        finish();
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.nav_settings:
                        return true;
                }
                return false;
            }
        });


    }

    @Override
    public void onClick(View view) {
        if(view == settingsNavigationBack) {
            finish();
        } else if(view == settingsProfileCardView) {
            startActivity(new Intent(this, ProfileActivity.class));
        } else if(view == settingsMyProjectsCardView) {
            startActivity(new Intent(this, ProjectActivity.class));
        } else if(view == settingsNotificationsCardView) {

        } else if(view == settingsLogoutCardView) {

        }
    }
}