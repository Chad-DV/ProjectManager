package com.example.projecto;

import static com.example.projecto.utils.ArrayConversionUtils.convertStringToArray;
import static com.example.projecto.utils.DBUtils.PROJECT_ID;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projecto.dao.ProjectDAOImpl;
import com.example.projecto.dao.UserDAOImpl;
import com.example.projecto.model.Project;
import com.example.projecto.utils.DBUtils;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ViewProjectActivity extends AppCompatActivity{

    private TextView viewProjectDescriptionTextView, viewProjectDateCreatedTextView, viewProjectDateDueTextView, viewProjectPriorityTextView;
    private CollapsingToolbarLayout viewProjectCollapsingToolbarLayout;
    private String authenticatedUser;
    private Button viewProjectDueStatusInfoBtn;
    private long userId;
    private long projectId;
    private UserDAOImpl userHelper;
    private ProjectDAOImpl projectHelper;
    private ListView viewProjectChecklistListView;
    private List<String> checklistItemList;
    private ProjectChecklistItemAdapter checklistItemAdapter;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_project);

        viewProjectDescriptionTextView = findViewById(R.id.viewProjectDescriptionTextView);
        viewProjectCollapsingToolbarLayout = findViewById(R.id.viewProjectCollapsingToolbarLayout);
        viewProjectDueStatusInfoBtn = findViewById(R.id.viewProjectDueStatusInfoBtn);
        viewProjectChecklistListView = findViewById(R.id.viewProjectChecklistListView);
        viewProjectDateCreatedTextView = findViewById(R.id.viewProjectDateCreatedTextView);
        viewProjectDateDueTextView = findViewById(R.id.viewProjectDateDueTextView);
        viewProjectPriorityTextView = findViewById(R.id.viewProjectPriorityTextView);
        checklistItemList = new ArrayList<>();

        projectHelper = new ProjectDAOImpl(this);
        userHelper = new UserDAOImpl(this);
        authenticatedUser = getIntent().getStringExtra(DBUtils.AUTHENTICATED_USER);
        userId = userHelper.getCurrentUserId(authenticatedUser);
        projectId = getIntent().getLongExtra(PROJECT_ID, 0);


        viewProjectCollapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        viewProjectCollapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);

        loadProjectDetails();



    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void loadProjectDetails() {
        Project project = projectHelper.getProjectById(projectId);

        System.out.println(project);

        viewProjectCollapsingToolbarLayout.setTitle(project.getTitle());
        viewProjectDescriptionTextView.setText(project.getDescription());


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        String formattedDateCreated = project.getDateCreated().format(formatter);
        String formattedDateDue = project.getDateDue().format(formatter);

        viewProjectDateCreatedTextView.setText(formattedDateCreated);
        viewProjectDateDueTextView.setText(formattedDateDue);
        viewProjectPriorityTextView.setText(project.getPriority());

        String[] checklistItemArray = convertStringToArray(project.getChecklist());
        checklistItemList = Arrays.stream(checklistItemArray).collect(Collectors.toList());

        System.out.println("checklistItemList: " + checklistItemList);


        checklistItemAdapter = new ProjectChecklistItemAdapter(getApplicationContext(), checklistItemList);
        viewProjectChecklistListView.setAdapter(checklistItemAdapter);
        ListViewHelper.getListViewSize(viewProjectChecklistListView);

        LinearLayout viewProjectChecklistLinearLayout = findViewById(R.id.viewProjectChecklistLinearLayout);

        if(checklistItemArray[0].equals("")) {
            viewProjectChecklistLinearLayout.setVisibility(View.GONE);
        } else {
            viewProjectChecklistLinearLayout.setVisibility(View.VISIBLE);
        }




        if(project.isProjectExpired()) {
            viewProjectDueStatusInfoBtn.setText("Expired");
            viewProjectDueStatusInfoBtn.setBackgroundResource(R.drawable.btn_prj_status_expired);
        } else {
            viewProjectDueStatusInfoBtn.setText("Active");
            viewProjectDueStatusInfoBtn.setBackgroundResource(R.drawable.btn_prj_status_not_expired);
        }



    }


}