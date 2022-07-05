package com.example.ppmtoolmobile;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ppmtoolmobile.dao.ProjectAndUserDAOImpl;
import com.example.ppmtoolmobile.model.Project;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class ProjectActivity extends AppCompatActivity implements View.OnClickListener, MyRecyclerAdapter.OnProjectClickListener {

    private Button projectAddBtn1;
    private TextView displayUserProjectCountTextView,welcomeUserTextView1;
    private EditText searchProjectEditText;
    private ProjectAndUserDAOImpl databaseHelper;
    private BottomNavigationView bottomNavView;
    private List<Project> projectList;
    private MyRecyclerAdapter adapter;

    private RecyclerView recyclerView;
    private String authenticatedUser;
    private int projectCount;
    private long userId;
    private String userFirstName;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        searchProjectEditText = findViewById(R.id.searchProjectEditText);
        projectAddBtn1 = findViewById(R.id.projectAddBtn1);
        displayUserProjectCountTextView = findViewById(R.id.displayUserProjectCountTextView);
        welcomeUserTextView1 = findViewById(R.id.welcomeUserTextView1);
        bottomNavView = findViewById(R.id.bottomNavView);
        bottomNavView.setSelectedItemId(R.id.nav_home);

        databaseHelper = new ProjectAndUserDAOImpl(this);

        // getting current username through intent from LoginActivity.class
        authenticatedUser = getIntent().getStringExtra("authenticatedUser");

        Toast.makeText(this, "project activity: " + authenticatedUser, Toast.LENGTH_SHORT).show();

        // current user id
        userId = databaseHelper.getCurrentUserId(authenticatedUser);

        // Getting users first name and amount of projects (This will be displayed in the heading of the main screen)
        userFirstName = databaseHelper.getCurrentUserFirstName(authenticatedUser);
        projectCount = databaseHelper.getProjectCount(userId);

        welcomeUserTextView1.setText("Welcome " + userFirstName + ", " + userId);
        displayUserProjectCountTextView.setText("You currently have " + projectCount + " projects");
//
//
//        if(projectCount <= 0) {
//            loadFragment(new EmptyProjectListFragment());
//        } else {
//            loadFragment(new ProjectFragment());
//        }

        loadFragment(new ProjectFragment());

        // Perform item selected listener
        bottomNavView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId())
                {
                    case R.id.nav_profile:
                        Intent goToProfileActivityIntent = new Intent(ProjectActivity.this, ProfileActivity.class);
                        moveToIntent(goToProfileActivityIntent);
                        return true;
                    case R.id.nav_home:
                        return true;
                    case R.id.nav_settings:
                        Intent goToSettingsActivityIntent = new Intent(ProjectActivity.this, SettingsActivity.class);
                        moveToIntent(goToSettingsActivityIntent);
                        return true;
                }
                return false;
            }
        });


        searchProjectEditText.setOnClickListener(this);
        projectAddBtn1.setOnClickListener(this);

    }


    private void moveToIntent(Intent intent) {
//        Intent goToSettingsActivityIntent = new Intent(ProjectActivity.this, ProfileActivity.class);

        intent.putExtra("authenticatedUser", authenticatedUser);
        startActivity(intent);
        overridePendingTransition(0,0);
    }

    @Override
    public void onBackPressed() {
        return;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View view) {
        if(view == projectAddBtn1) {
            Intent goToAddProjectIntent = new Intent(this, AddProjectActivity.class);
            goToAddProjectIntent.putExtra("authenticatedUser", authenticatedUser);
            startActivity(goToAddProjectIntent);
        } else if(view == searchProjectEditText) {
//            searchProjects();
        }
    }


    private void loadFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.projectFrameLayout, fragment);
        fragmentTransaction.commit(); // save the changes
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void searchProjects() {


    }


    @Override
    public void onProjectClick(View view, int position) {

    }

    @Override
    public void onProjectLongClick(View view, int position) {

    }
}