package com.example.projecto;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.projecto.dao.ProjectDAOImpl;
import com.example.projecto.dao.UserDAOImpl;
import com.example.projecto.model.Project;

import java.util.List;

public class EmptyProjectListFragment extends Fragment{

    private ProjectActivityViewModel projectViewModel;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_empty_project_list, null);

        System.out.println("in EmptyProjectListFragment");

        projectViewModel = new ViewModelProvider(requireActivity()).get(ProjectActivityViewModel.class);

        return v;


    }


}