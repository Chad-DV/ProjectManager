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
//    private String dueTime;
    private String priority;
    private String checklist;
    private String remindMeInterval;
    private long userId;
    private boolean status = false;

    public Project() {

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Project(String title, String description, LocalDateTime dateDue, String priority, String remindMeInterval) {
        this.title = title;
        this.description = description;
        this.dateCreated = LocalDateTime.now();
        this.dateDue = dateDue;
        this.priority = priority;
        this.remindMeInterval = remindMeInterval;


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Project(long id, String title, String description, LocalDateTime dateCreated, LocalDateTime dateDue, String priority, String checklist, String remindMeInterval, long userId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.dateCreated = dateCreated;
        this.dateDue = dateDue;
        this.priority = priority;
        this.remindMeInterval = remindMeInterval;
        this.userId = userId;


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Project(long id, String title, String description, LocalDateTime dateDue, String priority, String remindMeInterval) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.dateCreated = LocalDateTime.now();
        this.dateDue = dateDue;
        this.priority = priority;
        this.remindMeInterval = remindMeInterval;
        this.checklist = checklist;

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public long calculateDaysTillProjectDue(LocalDateTime dateCreated, LocalDateTime dateDue) {

//        Period period = Period.between(dateCreated.toLocalDate(), dateDue.toLocalDate());
//        Duration duration = Duration.between(dateCreated.toLocalTime(), dateDue.toLocalTime());

//        System.out.println("STATUS OF PROJECT");
//        System.out.println("Project created: " + dateCreated);
//        System.out.println("Project due: " + dateDue);
//
//        System.out.println("Project is due in : " + ChronoUnit.DAYS.between(dateCreated, dateDue) + " days");


        return ChronoUnit.DAYS.between(dateCreated, dateDue);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean isProjectExpired() {
        LocalDateTime current = LocalDateTime.now();

        // Here, notify the user when 1 hour is left till the project due date
//        if(minutes == 60) {
//            System.out.println("PROJECT EXPIRES IN 1 HOUR: " + getTitle());
//        }

        // send push notification based on time remaining
        // Remind 2 weeks (336 hours) (20160 minutes) (1,209,600,000 milliseconds) before due date
        // Remind 1 week (168 hours) (10080 minutes) (604,800,000 milliseconds) before due date
        // Remind 1 day (24 hours) (1440 minutes) (86,400,000 milliseconds) before due date
        // Remind 1 hour (60 minutes) (3,600,000 milliseconds) before due date
        // Remind 30 minutes (1,800,000 milliseconds) before due date
//        System.out.println("minutes till project " + getTitle() + " is due: " + minutes);
        return current.isAfter(getDateDue());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public long getProjectRemainingTimeInMilliseconds(LocalDateTime dateDue) {

//        System.out.println("project " + getTitle() + ", " + ChronoUnit.MILLIS.between(LocalDateTime.now(), dateDue) + ", expired=" + isProjectExpired(dateDue));
        return ChronoUnit.MILLIS.between(LocalDateTime.now(), dateDue);
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

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getChecklist() {
        return checklist;
    }

    public void setChecklist(String checkList) {
        this.checklist = checklist;
    }

    public String getRemindMeInterval() {
        return remindMeInterval;
    }

    public void setRemindMeInterval(String remindMe) {
        this.remindMeInterval = remindMeInterval;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
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
                ", checklist='" + checklist + '\'' +
                ", remindMeInterval='" + remindMeInterval + '\'' +
                ", userId=" + userId +
                ", status=" + status +
                '}';
    }
}
