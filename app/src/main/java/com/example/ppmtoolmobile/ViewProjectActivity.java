package com.example.ppmtoolmobile;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewProjectActivity extends AppCompatActivity {

    private ImageView viewProjectNavigationBack;
    private TextView viewProjectTitleTextView, viewProjectDescriptionTextView, viewProjectDueDateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_project);


    }
}