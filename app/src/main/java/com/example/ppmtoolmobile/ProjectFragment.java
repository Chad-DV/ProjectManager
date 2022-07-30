package com.example.ppmtoolmobile;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ppmtoolmobile.dao.ProjectAndUserDAOImpl;
import com.example.ppmtoolmobile.model.Project;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class ProjectFragment extends Fragment implements View.OnClickListener, MyRecyclerAdapter.OnProjectClickListener {

    private TextView sortProjectsTextView;
    private List<Project> projectList;
    private MyRecyclerAdapter adapter;
    private RecyclerView recyclerView;

    private ProgressBar projectListLoadingProgressBar;
    private long projectId;
    private ProjectAndUserDAOImpl databaseHelper;
    private long theUserId;
    private String authenticatedUser;
    private String query;
    private ProjectViewModel projectViewModel;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_project, null);

        createNotificationChannel();
        databaseHelper = new ProjectAndUserDAOImpl(getActivity().getApplicationContext());

        projectViewModel = new ProjectViewModel();

        sortProjectsTextView = v.findViewById(R.id.sortProjectsTextView);
        authenticatedUser = getActivity().getIntent().getStringExtra("authenticatedUser");

//        Toast.makeText(ProjectFragment.this.getActivity(), "project fragment: " + authenticatedUser, Toast.LENGTH_SHORT).show();




        theUserId = databaseHelper.getCurrentUserId(authenticatedUser);

        loadProjects(v);
        buildRecyclerView(v);

//        filterProjectEditText.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//                filterProjects(editable.toString());
//            }
//        });




        sortProjectsTextView.setOnClickListener(this);


        return v;

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onStart() {
        super.onStart();
        projectList = databaseHelper.getUserProjects(theUserId);
        adapter.refreshList(projectList);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View view) {
        if(view == sortProjectsTextView) {
            sortProjects();

            if (getArguments() != null) {
                query = getArguments().getString("query");
            }
            System.out.println("fragme " + query);

        }
    }

    private void sortProjects() {
        // Initializing the popup menu and giving the reference as current context
        PopupMenu popupMenu = new PopupMenu(ProjectFragment.this.getActivity(), sortProjectsTextView);

        // Inflating popup menu from popup_menu.xml file
        popupMenu.getMenuInflater().inflate(R.menu.sort_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.option_priority_high_low:
                        // sort function
                        projectList = databaseHelper.sortByPriorityHighToLow(theUserId);
                        adapter.refreshList(projectList);
                        break;
                    case R.id.option_priority_low_high:
                        // sort function
                        projectList = databaseHelper.sortByPriorityLowToHigh(theUserId);
                        adapter.refreshList(projectList);
                        break;
                    case R.id.option_due_date_newest_to_oldest:
                        // sort function
                        projectList = databaseHelper.sortByDateNewestToOldest(theUserId);
                        adapter.refreshList(projectList);
                        break;
                    case R.id.option_due_date_oldest_to_newest:
                        // sort function
                        projectList = databaseHelper.sortByDateOldestToNewest(theUserId);
                        adapter.refreshList(projectList);
                        break;
                }


                return true;
            }
        });
        // Showing the popup menu
        popupMenu.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onProjectClick(View view, int position) {
//        Project project = projectList.get(position);
        projectId = projectList.get(position).getId();

//        filterList("69");

        Intent goToProjectIntent = new Intent(ProjectFragment.this.getActivity(), ViewProjectActivity.class);
        goToProjectIntent.putExtra("authenticatedUser", authenticatedUser);
        goToProjectIntent.putExtra("projectId", projectId);
//        goToProjectIntent.putExtra("title", project.getTitle());
//        goToProjectIntent.putExtra("description", project.getDescription());

        startActivity(goToProjectIntent);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onProjectLongClick(View view, int position) {


        Project project = projectList.get(position);
        projectId = projectList.get(position).getId();


        Toast.makeText(ProjectFragment.this.getActivity(), project.toString(), Toast.LENGTH_SHORT).show();

        PopupMenu popupMenu = new PopupMenu(ProjectFragment.this.getActivity(), sortProjectsTextView);

        // Inflating popup menu from popup_menu.xml file
        popupMenu.getMenuInflater().inflate(R.menu.project_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.option_delete:

                    deleteProject(projectId);
                    break;
                case R.id.option_edit:
                    Intent getProjectIdIntent = new Intent(ProjectFragment.this.getActivity(), EditProjectActivity.class);
                    getProjectIdIntent.putExtra("projectId", projectId);
                    startActivity(getProjectIdIntent);

//                    Project theProject =  daoHelper.getProjectById(projectId);
//                    Toast.makeText(ProjectFragment.this.getActivity(), theProject.toString(), Toast.LENGTH_SHORT).show();
//                    System.out.println(project.toString());
                    break;
            }
            return true;
        });
        // Showing the popup menu
        popupMenu.show();



    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void filterProjects() {



//        List<Project> filteredList = new ArrayList<>();
//
//        for (Project project : projectList) {
//            if (project.getTitle().toLowerCase().contains(query.toLowerCase())) {
//                filteredList.add(project);
//            }
//        }
//
//        adapter.refreshList(filteredList);
    }


//    @RequiresApi(api = Build.VERSION_CODES.O)
//    private void refreshProjects() {
//        projectList = databaseHelper.getUserProjects(theUserId);
//        adapter.refreshList(projectList);
//    }

    private void buildRecyclerView(View v) {
        recyclerView = v.findViewById(R.id.projectRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ProjectFragment.this.getActivity()));
        adapter = new MyRecyclerAdapter(ProjectFragment.this.getActivity(), projectList, this);
        recyclerView.setAdapter(adapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void loadProjects(View v) {
        projectList = new ArrayList<>();
        projectListLoadingProgressBar = v.findViewById(R.id.projectListLoadingProgressBar);

        projectListLoadingProgressBar.setVisibility(View.VISIBLE);
        projectList.clear();

        projectList = databaseHelper.getUserProjects(theUserId);



        projectListLoadingProgressBar.setVisibility(View.GONE);


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void deleteProject(long position) {
        new AlertDialog.Builder(ProjectFragment.this.getActivity())
                .setTitle("Delete entry")
                .setMessage("Are you sure you want to delete this entry?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    boolean res = databaseHelper.deleteProjectById(position);
                    projectList = databaseHelper.getUserProjects(theUserId);

                    adapter = new MyRecyclerAdapter(ProjectFragment.this.getActivity(), projectList, this);
                    recyclerView.setAdapter(adapter);
                    Toast.makeText(ProjectFragment.this.getActivity(), String.valueOf(res), Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(android.R.string.no, (dialog, which) -> {
                    Toast.makeText(ProjectFragment.this.getActivity(), "Not deleted", Toast.LENGTH_SHORT).show();
                })
                .setIcon(android.R.drawable.alert_dark_frame)
                .show();
    }


    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "ppmtoolReminderChannel";
            String description = "Channel for ppmtool reminder";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel("ppmtool", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getActivity().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}