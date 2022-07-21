package com.example.ppmtoolmobile.model;

import android.graphics.Bitmap;

public class UserAvatar {
    private String avatarName;
    private Bitmap avatar;
    private int length;
    private long userId;

    public UserAvatar(String avatarName, Bitmap avatar) {
        this.avatarName = avatarName;
        this.avatar = avatar;
    }

    public String getAvatarName() {
        return avatarName;
    }

    public void setAvatarName(String avatarName) {
        this.avatarName = avatarName;
    }

    public Bitmap getAvatar() {
        return avatar;
    }

    public void setAvatar(Bitmap avatar) {
        this.avatar = avatar;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "UserAvatar{" +
                "avatarName='" + avatarName + '\'' +
                ", avatar=" + avatar +
                ", userId=" + userId +
                '}';
    }
}
