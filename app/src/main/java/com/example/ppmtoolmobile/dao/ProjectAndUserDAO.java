package com.example.ppmtoolmobile.dao;

import android.graphics.Bitmap;

import com.example.ppmtoolmobile.model.Project;
import com.example.ppmtoolmobile.model.User;
import com.example.ppmtoolmobile.model.UserAvatar;

import java.util.List;


public interface ProjectAndUserDAO {

    // User
    Boolean register(User user);
    Boolean login(User user);
    String getCurrentUserFirstName(String emailAddress);
    long getCurrentUserId(String emailAddress);
    User getUserDetails(String emailAddress);
    Bitmap getAvatar(long userId);
    Boolean removeAvatar(long theUserId);
    Boolean editUserDetails(User user);
    Boolean saveAvatar(UserAvatar userAvatar, String emailAddress);

    // Project
    Boolean addProject(Project project, String emailAddress);
    Boolean editProject(Project project);
    Boolean deleteProjectById(long id);
    List<Project> getUserProjects(long userId);
    Project getProjectById(long projectId);
    List<Project> searchProjects(long userId, String query);
    int getProjectCount(long userId);
    List<Project> sortByPriorityHighToLow(long userId);
    List<Project> sortByPriorityLowToHigh(long userId);
    List<Project> sortByDateNewestToOldest(long userId);
    List<Project> sortByDateOldestToNewest(long userId);

}
