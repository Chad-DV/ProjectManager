package com.example.ppmtoolmobile;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.ppmtoolmobile.dao.ProjectAndUserDAOImpl;
import com.example.ppmtoolmobile.dao.ProjectDAOImpl;
import com.example.ppmtoolmobile.dao.UserDAOImpl;
import com.example.ppmtoolmobile.model.Project;

import java.util.List;

public class EmptyProjectListFragment extends Fragment{

    private List<Project> projectList;
    private ProjectRecyclerAdapter adapter;
    private ProjectDAOImpl projectHelper;
    private UserDAOImpl userHelper;
    private RecyclerView recyclerView;
    private ProjectActivityViewModel projectViewModel;
    private String authenticatedUser;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {




        View v = inflater.inflate(R.layout.fragment_empty_project_list, null);

        System.out.println("in EmptyProjectListFragment");

        projectViewModel = new ViewModelProvider(requireActivity()).get(ProjectActivityViewModel.class);

        return v;


    }


}