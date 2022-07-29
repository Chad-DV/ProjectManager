package com.example.ppmtoolmobile;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ppmtoolmobile.dao.ProjectAndUserDAOImpl;
import com.example.ppmtoolmobile.model.Project;

public class ViewProjectActivity extends AppCompatActivity {

    private ImageView viewProjectNavigationBack;
    private TextView viewProjectTitleTextView, viewProjectDescriptionTextView, viewProjectDueDateTextView;
    private String authenticatedUser;
    private long userId;
    private long projectId;
    private ProjectAndUserDAOImpl databasehelper;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_project);

        viewProjectTitleTextView = findViewById(R.id.viewProjectTitleTextView);
        viewProjectDescriptionTextView = findViewById(R.id.viewProjectDescriptionTextView);
        viewProjectDueDateTextView = findViewById(R.id.viewProjectDateDueTextView2);


        databasehelper = new ProjectAndUserDAOImpl(this);
        authenticatedUser = getIntent().getStringExtra("authenticatedUser");
        userId = databasehelper.getCurrentUserId(authenticatedUser);
        projectId = getIntent().getLongExtra("projectId", 0);

        Toast.makeText(this, "view project activity: " + projectId, Toast.LENGTH_SHORT).show();

        loadProjectDetails();



    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void loadProjectDetails() {
        Project project = databasehelper.getProjectById(projectId);

        viewProjectTitleTextView.setText(project.getTitle());
        viewProjectDescriptionTextView.setText(project.getDescription());


    }
}