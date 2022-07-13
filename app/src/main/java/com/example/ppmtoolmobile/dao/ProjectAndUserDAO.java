package com.example.ppmtoolmobile.dao;

import com.example.ppmtoolmobile.model.Project;
import com.example.ppmtoolmobile.model.User;

import java.util.List;


public interface ProjectAndUserDAO {

    // User
    Boolean register(User user);
    Boolean login(User user);
    Boolean isEmailExists(String email);
    String getCurrentUserFirstName(String emailAddress);
    long getCurrentUserId(String emailAddress);
    User getUserDetails(String theEmailAddress);

    // Project
    Boolean addProject(Project project, String emailAddress);
    Boolean editProject(Project project);
    Boolean deleteProjectById(long id);
    List<Project> getUserProjects(long userId);
    Project getProjectById(long projectId);
    int getProjectCount(long userId);
    List<Project> sortByPriorityHighToNone(long userId);
    List<Project> sortByPriorityNoneToHigh(long userId);
    List<Project> sortByDateNewestToOldest(long userId);
    List<Project> sortByDateOldestToNewest(long userId);

}
