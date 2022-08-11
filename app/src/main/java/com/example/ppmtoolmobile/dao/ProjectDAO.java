package com.example.ppmtoolmobile.dao;

import com.example.ppmtoolmobile.model.Project;

import java.util.List;

public interface ProjectDAO {
    Boolean addProject(Project project, String emailAddress);
    Boolean editProject(Project project);
    Boolean deleteProjectById(long id);
    List<Project> getUserProjects(long userId);
    Project getProjectById(long projectId);
    List<Project> filterProjects(long userId, String query);
    int getProjectCount(long userId);
    List<Project> sortByPriorityHighToLow(long userId);
    List<Project> sortByPriorityLowToHigh(long userId);
    List<Project> sortByDateNewestToOldest(long userId);
    List<Project> sortByDateOldestToNewest(long userId);
}
