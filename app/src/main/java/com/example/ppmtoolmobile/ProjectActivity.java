package com.example.ppmtoolmobile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.ppmtoolmobile.dao.DaoHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Set;

public class ProjectActivity extends AppCompatActivity implements View.OnClickListener {

    private Button projectAddBtn1;
    private TextView displayUserProjectCountTextView;
    private EditText searchProjectEditText;
    private DaoHelper daoHelper;
    private BottomNavigationView bottomNavView;

    private int projectCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        projectAddBtn1 = findViewById(R.id.projectAddBtn1);
        displayUserProjectCountTextView = findViewById(R.id.displayUserProjectCountTextView);
        searchProjectEditText = findViewById(R.id.searchProjectEditText);
        bottomNavView = findViewById(R.id.bottomNavView);
        bottomNavView.setSelectedItemId(R.id.nav_home);
        daoHelper = new DaoHelper(this);

        projectCount = daoHelper.getProjectCount();

        displayUserProjectCountTextView.setText("You currently have " + projectCount + " projects");


        if(projectCount <= 0) {
            loadFragment(new EmptyProjectListFragment());
        } else {
            loadFragment(new ProjectFragment());
        }



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
                        return true;
                    case R.id.nav_settings:
                        startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });


        projectAddBtn1.setOnClickListener(this);



    }

    @Override
    public void onClick(View view) {
        if(view == projectAddBtn1) {
            Intent goToAddProjectIntent = new Intent(this, AddProjectActivity.class);
            startActivity(goToAddProjectIntent);
        }
    }


    private void loadFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.projectFrameLayout, fragment);
        fragmentTransaction.commit(); // save the changes
    }




}