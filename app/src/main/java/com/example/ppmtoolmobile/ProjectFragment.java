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

import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ppmtoolmobile.dao.ProjectDAOImpl;
import com.example.ppmtoolmobile.dao.UserDAOImpl;
import com.example.ppmtoolmobile.model.Project;
import com.example.ppmtoolmobile.utils.DBUtils;

import java.util.ArrayList;
import java.util.List;

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
        authenticatedUser = getActivity().getIntent().getStringExtra(DBUtils.AUTHENTICATED_USER);
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

//        for(Project p : projectList) {
//            System.out.println("Project " + p.getTitle() + " is due in " + p.getProjectRemainingTimeInMinutes() + " minutes");
//        }
        projectViewModel.setProjectCount(projectCount);
        adapter.refreshList(projectList);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View view) {
        if(view == sortProjectsTextView) {

            sortProjects();
        }
    }

    private void sortProjects() {
        PopupMenu popupMenu = new PopupMenu(ProjectFragment.this.getActivity(), sortProjectsTextView);

        // Inflating popup menu from popup_menu.xml file
        popupMenu.getMenuInflater().inflate(R.menu.sort_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
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
        projectId = projectList.get(position).getId();

        Intent goToProjectIntent = new Intent(ProjectFragment.this.getActivity(), ViewProjectActivity.class);
        goToProjectIntent.putExtra(DBUtils.AUTHENTICATED_USER, authenticatedUser);
        goToProjectIntent.putExtra("projectId", projectId);
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
                    getProjectIdIntent.putExtra(DBUtils.AUTHENTICATED_USER, authenticatedUser);
                    getProjectIdIntent.putExtra("userId", theUserId);
                    startActivity(getProjectIdIntent);
                    break;
            }
            return true;
        });
        // Showing the popup menu
        popupMenu.show();



    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        projectViewModel.getQuery().observe(getViewLifecycleOwner(), item -> {

            if(item.isEmpty()) {
                viewModelTextView1.setVisibility(View.GONE);
                viewModelTextView2.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                projectList = projectHelper.getUserProjects(theUserId);
                adapter.refreshList(projectList);
                projectCount = projectList.size();
                projectViewModel.setProjectCount(projectCount);
            }else {
                viewModelTextView1.setVisibility(View.GONE);
                viewModelTextView2.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                projectList = projectHelper.filterProjects(theUserId, item.toString());
                projectCount = projectList.size();

                projectViewModel.setProjectCount(projectCount);
                if(projectList.isEmpty()) {
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

        Okay.setOnClickListener(v -> {
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
        });

        Cancel.setOnClickListener(view -> {
            dialog.dismiss();
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