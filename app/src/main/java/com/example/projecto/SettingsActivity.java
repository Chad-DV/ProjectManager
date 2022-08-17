package com.example.projecto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.projecto.utils.DBUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    private BottomNavigationView bottomNavView;
    private CardView settingsProfileCardView, settingsMyProjectsCardView, settingsNotificationsCardView, settingsLogoutCardView;
    private ImageView settingsNavigationBack;
    private String authenticatedUser;
    private NotificationManagerCompat notificationManagerCompat;
    private Notification notification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        bottomNavView = findViewById(R.id.bottomNavView);
        settingsNavigationBack = findViewById(R.id.settingsNavigationBack);
        settingsProfileCardView = findViewById(R.id.settingsProfileCardView);
        settingsMyProjectsCardView = findViewById(R.id.settingsMyProjectsCardView);
        settingsLogoutCardView = findViewById(R.id.settingsLogoutCardView);

        authenticatedUser = getIntent().getStringExtra(DBUtils.AUTHENTICATED_USER);

        Toast.makeText(this, "settings activity: " + authenticatedUser, Toast.LENGTH_SHORT).show();

        bottomNavView.setSelectedItemId(R.id.nav_settings);

        settingsNavigationBack.setOnClickListener(this);
        settingsProfileCardView.setOnClickListener(this);
        settingsMyProjectsCardView.setOnClickListener(this);
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
        intent.putExtra(DBUtils.AUTHENTICATED_USER, authenticatedUser);
        startActivity(intent);
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
        }else if(view == settingsLogoutCardView) {
            logout();
        }
    }

    private void logout() {
        SharedPreferences preferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("saveLogin", false);
        editor.apply();

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }


}