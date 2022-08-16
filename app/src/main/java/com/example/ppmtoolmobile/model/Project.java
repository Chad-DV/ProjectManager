package com.example.ppmtoolmobile.model;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

@RequiresApi(api = Build.VERSION_CODES.O)
public class Project {
    private long id;
    private String title;
    private String description;
    private LocalDateTime dateCreated;
    private LocalDateTime dateDue;
    private String priority;
    private String remindMeInterval;
    private String checklist;
    private long userId;

    public Project() {

    }

    public Project(String title, String description, LocalDateTime dateDue, String priority, String remindMeInterval, String checklist, long userId) {
        this.title = title;
        this.description = description;
        this.dateDue = dateDue;
        this.dateCreated = LocalDateTime.now();
        this.priority = priority;
        this.remindMeInterval = remindMeInterval;
        this.checklist = checklist;
        this.userId = userId;
    }

    public Project(long id, String title, String description, LocalDateTime dateCreated, LocalDateTime dateDue, String priority, String remindMeInterval, String checklist, long userId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.dateCreated = LocalDateTime.now();
        this.dateDue = dateDue;
        this.priority = priority;
        this.checklist = checklist;
        this.remindMeInterval = remindMeInterval;
        this.userId = userId;
    }


    public Project(long id, String title, String description, LocalDateTime dateDue, String priority, String remindMeInterval, String checklist, long userId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.dateDue = dateDue;
        this.priority = priority;
        this.remindMeInterval = remindMeInterval;
        this.checklist = checklist;
        this.userId = userId;
    }


    public Project(long id, String title, String description, LocalDateTime dateDue, String priority, String remindMeInterval, String checklist) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.dateCreated = LocalDateTime.now();
        this.dateDue = dateDue;
        this.priority = priority;
        this.remindMeInterval = remindMeInterval;
        this.checklist = checklist;

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public LocalDateTime getDateDue() {
        return dateDue;
    }

    public void setDateDue(LocalDateTime dateDue) {
        this.dateDue = dateDue;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }


    public String getChecklist() {
        return checklist;
    }

    public void setChecklist(String checklist) {
        this.checklist = checklist;
    }

    public String getRemindMeInterval() {
        return remindMeInterval;
    }

    public void setRemindMeInterval(String remindMeInterval) {
        this.remindMeInterval = remindMeInterval;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean isProjectExpired() {
        LocalDateTime current = LocalDateTime.now();
        return current.isAfter(getDateDue());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public long getProjectRemainingTimeInMinutes() {
        return ChronoUnit.MINUTES.between(LocalDateTime.now(), getDateDue());
    }


    @Override
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", dateCreated=" + dateCreated +
                ", dateDue=" + dateDue +
                ", priority='" + priority + '\'' +
                ", remindMeInterval='" + remindMeInterval + '\'' +
                ", checklist=" + checklist +
                ", userId=" + userId +
                '}';
    }
}
