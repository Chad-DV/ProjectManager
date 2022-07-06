package com.example.ppmtoolmobile;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ppmtoolmobile.dao.ProjectAndUserDAOImpl;
import com.example.ppmtoolmobile.model.Project;

import java.util.List;

public class EmptyProjectListFragment extends Fragment implements MyRecyclerAdapter.OnProjectClickListener {

    private List<Project> projectList;
    private MyRecyclerAdapter adapter;
    private ProjectAndUserDAOImpl projectAndUserDAOImpl;
    private RecyclerView recyclerView;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_empty_project_list, null);


//        daoHelper = new DaoHelper(getActivity().getApplicationContext());
//        projectList = new ArrayList<>();
//        projectList = daoHelper.getAllProjects();
//        recyclerView = v.findViewById(R.id.projectRecyclerView);
//        recyclerView.setLayoutManager(new LinearLayoutManager(EmptyProjectListFragment.this.getActivity()));
//
//        adapter = new MyRecyclerAdapter(EmptyProjectListFragment.this.getActivity(), projectList, this);
//        recyclerView.setAdapter(adapter);

        return v;


    }

    @Override
    public void onProjectClick(View view, int position) {

    }

    @Override
    public void onProjectLongClick(View view, int position) {

    }
}