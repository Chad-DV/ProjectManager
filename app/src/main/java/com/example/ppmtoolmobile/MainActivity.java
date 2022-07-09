package com.example.ppmtoolmobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // redirect to Launcher screen after 3 seconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent goToMenuActivity = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(goToMenuActivity);
            }
        }, 5000);
    }
}