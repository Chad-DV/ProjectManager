package com.example.projecto;

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

public class ViewProjectActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView viewProjectTitleTextView, viewProjectDescriptionTextView, viewProjectDateCreatedTextView, viewProjectDateDueTextView, viewProjectPriorityTextView;
    private CollapsingToolbarLayout viewProjectCollapsingToolbarLayout;
    private String authenticatedUser;
    private ImageView viewProjectMenu;
    private Button viewProjectDueStatusInfoBtn;
    private long userId;
    private long projectId;
    private UserDAOImpl userHelper;
    private ProjectDAOImpl projectHelper;
    private Dialog dialog;
    private ListView viewProjectChecklistListView;
    private List<String> checklistItemList;
    private static String strSeparator = ", ";
    private ProjectChecklistItemAdapter checklistItemAdapter;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_project);


//        viewProjectTitleTextView = findViewById(R.id.viewProjectTitleTextView);
        viewProjectDescriptionTextView = findViewById(R.id.viewProjectDescriptionTextView);
//        viewProjectDueDateTextView = findViewById(R.id.viewProjectDateDueTextView2);
        viewProjectCollapsingToolbarLayout = findViewById(R.id.viewProjectCollapsingToolbarLayout);
        viewProjectDueStatusInfoBtn = findViewById(R.id.viewProjectDueStatusInfoBtn);
        viewProjectMenu = findViewById(R.id.viewProjectMenu);
        viewProjectChecklistListView = findViewById(R.id.viewProjectChecklistListView);
//        viewProjectNoChecklistItems = findViewById(R.id.viewProjectNoChecklistItems);
        viewProjectDateCreatedTextView = findViewById(R.id.viewProjectDateCreatedTextView);
        viewProjectDateDueTextView = findViewById(R.id.viewProjectDateDueTextView);
        viewProjectPriorityTextView = findViewById(R.id.viewProjectPriorityTextView);
        checklistItemList = new ArrayList<>();

        dialog = new Dialog(this);

        projectHelper = new ProjectDAOImpl(this);
        userHelper = new UserDAOImpl(this);
        authenticatedUser = getIntent().getStringExtra(DBUtils.AUTHENTICATED_USER);
        userId = userHelper.getCurrentUserId(authenticatedUser);
        projectId = getIntent().getLongExtra("projectId", 0);

        Toast.makeText(this, "view project activity: " + projectId, Toast.LENGTH_SHORT).show();

        viewProjectCollapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        viewProjectCollapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);

        viewProjectMenu.setOnClickListener(this);


        loadProjectDetails();



    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void loadProjectDetails() {
        Project project = projectHelper.getProjectById(projectId);

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


        checklistItemAdapter = new ProjectChecklistItemAdapter(getApplicationContext(), checklistItemList);
        viewProjectChecklistListView.setAdapter(checklistItemAdapter);
        ListViewHelper.getListViewSize(viewProjectChecklistListView);

//        System.out.println("Checklist arr: " + Arrays.toString(checklistItemArray));
        System.out.println("Checklist list: " + checklistItemList);

        LinearLayout viewProjectChecklistLinearLayout = findViewById(R.id.viewProjectChecklistLinearLayout);

        if(checklistItemArray[0].isEmpty()) {
            viewProjectChecklistLinearLayout.setVisibility(View.INVISIBLE);
        }




        if(project.isProjectExpired()) {
            viewProjectDueStatusInfoBtn.setText("Expired");
            viewProjectDueStatusInfoBtn.setBackgroundResource(R.drawable.btn_prj_status_expired);
        } else {
            viewProjectDueStatusInfoBtn.setText("Not Expired");
            viewProjectDueStatusInfoBtn.setBackgroundResource(R.drawable.btn_prj_status_not_expired);
        }



    }

    @Override
    public void onClick(View view) {
        if(view == viewProjectMenu) {
            PopupMenu popupMenu = new PopupMenu(ViewProjectActivity.this, viewProjectMenu);

            // Inflating popup menu from popup_menu.xml file
            popupMenu.getMenuInflater().inflate(R.menu.project_menu, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.option_delete:
                            deleteProject();
                            break;
                        case R.id.option_edit:
                            Intent getProjectIdIntent = new Intent(ViewProjectActivity.this, EditProjectActivity.class);
                            getProjectIdIntent.putExtra("projectId", projectId);
                            startActivity(getProjectIdIntent);

                    }


                    return true;
                }

            });
            // Showing the popup menu
            popupMenu.show();
        }
    }


    private void deleteProject() {
        displayDialog(R.layout.caution_dialog_layout);

        Button Okay = dialog.findViewById(R.id.btn_okay);
        Button Cancel = dialog.findViewById(R.id.btn_cancel);

        Okay.setOnClickListener(view -> {
            boolean res = projectHelper.deleteProjectById(projectId);
            if(res) {
                finish();
            }

            Toast.makeText(ViewProjectActivity.this, String.valueOf(res), Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        Cancel.setOnClickListener(view -> {
            dialog.dismiss();
        });

        dialog.show();

    }



    private void displayDialog(int layoutView) {
        dialog.setContentView(layoutView);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.custom_dialog_background));
        }
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false); //Optional
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation; //Setting the animations to dialog

    }


    public static String[] convertStringToArray(String str){
        if(str == null) {
            return new String[]{};
        }
        return str.split(strSeparator);

    }


}