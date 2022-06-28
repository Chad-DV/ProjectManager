package com.example.ppmtoolmobile;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class NavActivity extends AppCompatActivity {


    private BottomNavigationView bottomNavView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);

        bottomNavView = findViewById(R.id.bottomNavView);



//        getSupportFragmentManager().beginTransaction().replace(R.id.navframeLayout, new ProjectFragment()).commit();

//        bottomNavView.setOnItemSelectedListener(item -> {
//            switch(item.getItemId()) {
//                case R.id.home:
//                    Toast.makeText(NavActivity.this, "clicked home", Toast.LENGTH_LONG).show();
//                    getSupportFragmentManager().beginTransaction().replace(R.id.navframeLayout, new ProjectFragment()).commit();
//                    break;
//                case R.id.profile:
//                    Toast.makeText(NavActivity.this, "clicked profile", Toast.LENGTH_LONG).show();
//                    getSupportFragmentManager().beginTransaction().replace(R.id.navframeLayout, new ProfileFragment()).commit();
//                    break;
//                case R.id.settings:
//                    Toast.makeText(NavActivity.this, "clicked settings", Toast.LENGTH_LONG).show();
//                    getSupportFragmentManager().beginTransaction().replace(R.id.navframeLayout, new SettingsFragment()).commit();
//                    break;
//            }
//
//            return true;
//        });
    }
}