package com.example.projecto.dao;

import android.graphics.Bitmap;

import com.example.projecto.model.User;
import com.example.projecto.model.UserAvatar;

public interface UserDAO {

    Boolean register(User user);
    Boolean login(User user);
    String getCurrentUserFirstName(String emailAddress);
    long getCurrentUserId(String emailAddress);
    User getUserDetails(String emailAddress);
    Bitmap getAvatar(long userId);
    String getCurrentUserEmailAddress(long userId);
    Boolean removeAvatar(long theUserId);
    Boolean editUserDetails(User user);
    Boolean saveAvatar(UserAvatar userAvatar, String emailAddress);
}
