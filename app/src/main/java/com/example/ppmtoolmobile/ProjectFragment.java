package com.example.ppmtoolmobile;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ppmtoolmobile.dao.ProjectAndUserDAOImpl;
import com.example.ppmtoolmobile.dao.ProjectDAOImpl;
import com.example.ppmtoolmobile.dao.UserDAOImpl;
import com.example.ppmtoolmobile.model.Project;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ProjectFragment extends Fragment implements View.OnClickListener, ProjectRecyclerAdapter.OnProjectClickListener {

    private TextView sortProjectsTextView, viewModelTextView1, viewModelTextView2;
    private List<Project> projectList;
    private ProjectRecyclerAdapter adapter;
    private RecyclerView recyclerView;
    private ProgressBar projectListLoadingProgressBar;
    private long projectId;
    private ProjectDAOImpl projectHelper;
    private UserDAOImpl userHelper;
    private long theUserId;
    private String authenticatedUser;
    private ProjectActivityViewModel projectViewModel;
    private int projectCount;
    private Dialog dialog;




    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_project, null);
        dialog = new Dialog(this.getActivity());
        projectHelper = new ProjectDAOImpl(getActivity());
        userHelper = new UserDAOImpl(getActivity());

        projectViewModel = new ViewModelProvider(requireActivity()).get(ProjectActivityViewModel.class);

        sortProjectsTextView = v.findViewById(R.id.sortProjectsTextView);
        authenticatedUser = getActivity().getIntent().getStringExtra("authenticatedUser");
        theUserId = userHelper.getCurrentUserId(authenticatedUser);

        viewModelTextView1 = v.findViewById(R.id.viewModelTextView1);
        viewModelTextView2 = v.findViewById(R.id.viewModelTextView2);


        loadProjects(v);
        buildRecyclerView(v);
//        buttonShowNotification();

        sortProjectsTextView.setOnClickListener(this);


        return v;

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onStart() {
        super.onStart();
        projectList = projectHelper.getUserProjects(theUserId);
        projectCount = projectList.size();
        projectViewModel.setProjectCount(projectCount);
        adapter.refreshList(projectList);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View view) {
        if(view == sortProjectsTextView) {
            sortProjects();
            filterProjects();


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
                        projectList = projectHelper.sortByPriorityHighToLow(theUserId);
                        adapter.refreshList(projectList);
                        break;
                    case R.id.option_priority_low_high:
                        // sort function
                        projectList = projectHelper.sortByPriorityLowToHigh(theUserId);
                        adapter.refreshList(projectList);
                        break;
                    case R.id.option_due_date_newest_to_oldest:
                        // sort function
                        projectList = projectHelper.sortByDateNewestToOldest(theUserId);
                        adapter.refreshList(projectList);
                        break;
                    case R.id.option_due_date_oldest_to_newest:
                        // sort function
                        projectList = projectHelper.sortByDateOldestToNewest(theUserId);
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



        Context wrapper = new ContextThemeWrapper(getActivity(), R.style.MyPopupOtherStyle);
        PopupMenu popupMenu = new PopupMenu(wrapper, sortProjectsTextView);
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
                    break;
            }
            return true;
        });
        // Showing the popup menu
        popupMenu.show();



    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void filterProjects() {
//        String query = filterProjectEditText.getText().toString().trim();
//
//        projectList = databaseHelper.searchProjects(theUserId, viewModelTextView.getText().toString());
//        adapter.refreshList(projectList);
//        if(projectList.isEmpty()) {
//            Toast.makeText(getActivity(), "No projects matched with " + query , Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(getActivity(), projectList.toString() , Toast.LENGTH_SHORT).show();
//        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        projectViewModel.getQuery().observe(getViewLifecycleOwner(), item -> {
//            viewModelTextView.setText(item);

            if(item.isEmpty()) {
                viewModelTextView1.setVisibility(View.GONE);
                viewModelTextView2.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                projectList = projectHelper.getUserProjects(theUserId);
                adapter.refreshList(projectList);
                System.out.println("ITEM IS EMPTY");
                projectCount = projectList.size();
                projectViewModel.setProjectCount(projectCount);
            }else {
                viewModelTextView1.setVisibility(View.GONE);
                viewModelTextView2.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                projectList = projectHelper.filterProjects(theUserId, item.toString());
                projectCount = projectList.size();

                System.out.println("RESULT: " + projectCount);

                projectViewModel.setProjectCount(projectCount);
                if(projectList.isEmpty()) {
                    Toast.makeText(getActivity(), "No projects matched with " + item , Toast.LENGTH_SHORT).show();
                    viewModelTextView1.setVisibility(View.VISIBLE);
                    viewModelTextView2.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    viewModelTextView1.setVisibility(View.GONE);
                    viewModelTextView2.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    Toast.makeText(getActivity(), projectList.toString() , Toast.LENGTH_SHORT).show();
                }
                adapter.refreshList(projectList);
            }



        });

    }


    private void buildRecyclerView(View v) {
        recyclerView = v.findViewById(R.id.projectRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ProjectFragment.this.getActivity()));
        adapter = new ProjectRecyclerAdapter(ProjectFragment.this.getActivity(), projectList, this);
        recyclerView.setAdapter(adapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void loadProjects(View v) {
        projectList = new ArrayList<>();
        projectListLoadingProgressBar = v.findViewById(R.id.projectListLoadingProgressBar);

        projectListLoadingProgressBar.setVisibility(View.VISIBLE);

        projectList = projectHelper.getUserProjects(theUserId);

        projectListLoadingProgressBar.setVisibility(View.GONE);


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void deleteProject(long position) {
        displayDialog(R.layout.caution_dialog_layout);

        Button Okay = dialog.findViewById(R.id.btn_okay);
        Button Cancel = dialog.findViewById(R.id.btn_cancel);

        Okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                projectHelper.deleteProjectById(projectId);

                projectList = projectHelper.getUserProjects(theUserId);

                projectCount = projectList.size();
                projectViewModel.setProjectCount(projectCount);

                if(projectCount <= 0) {
                    EmptyProjectListFragment fragment = new EmptyProjectListFragment();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.projectFrameLayout, fragment);
                    fragmentTransaction.addToBackStack(null);

                    fragmentTransaction.commit();
                }
                adapter.refreshList(projectList);
                dialog.dismiss();
            }
        });

        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });

        dialog.show();

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void buttonShowNotification() {

        long[] minutes = new long[projectList.size()];


        // Remind 2 weeks (336 hours) (20160 minutes) before due date
        // Remind 1 week (168 hours) (10080 minutes) (604,800,000 milliseconds) before due date
        // Remind 1 day (24 hours) (1440 minutes) (86,400,000 milliseconds) before due date
        // Remind 1 hour (60 minutes) (3,600,000 milliseconds) before due date
        // Remind 30 minutes (1,800,000 milliseconds) before due date

        for(int i = 0; i < projectList.size(); i++) {
            System.out.println(projectList.get(i).getRemindMeInterval());
            minutes[i] = projectList.get(i).getProjectRemainingTimeInMinutes();

            if(minutes[i] == 20160) {

            } else if(minutes[i] == 10080) {

            } else if(minutes[i] == 1440) {

            } else if(minutes[i] == 60) {

            } else if(minutes[i] == 3) {

            }
            System.out.println("Project title: " + projectList.get(i).getTitle() + ", Minutes left: " + minutes[i]);
        }

//        WorkRequest request = new PeriodicWorkRequest.Builder(NotificationWorker.class, 10, TimeUnit.MINUTES)
//                .setInitialDelay(1, TimeUnit.SECONDS)
//                .build();
//
//        WorkManager.getInstance(getActivity().getApplicationContext()).enqueue(request);


    }

    private void displayDialog(int layoutView) {
        dialog.setContentView(layoutView);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog.getWindow().setBackgroundDrawable(getActivity().getDrawable(R.drawable.custom_dialog_background));
        }
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false); //Optional
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation; //Setting the animations to dialog

    }



}