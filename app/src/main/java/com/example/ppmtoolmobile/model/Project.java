package com.example.ppmtoolmobile.model;

import android.os.Build;

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

public class Project {
    private long id;
    private String title;
    private String description;
    private LocalDateTime dateCreated;
    private LocalDateTime dateDue;
//    private String dueTime;
    private String priority;
    private String checklist;
    private long userId;

    public Project() {

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Project(String title, String description, LocalDateTime dateDue, String priority) {
        this.title = title;
        this.description = description;
        this.dateCreated = LocalDateTime.now();
        this.dateDue = dateDue;
        this.priority = priority;


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Project(long id, String title, String description, LocalDateTime dateCreated, LocalDateTime dateDue, String priority, String checklist, long userId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.dateCreated = dateCreated;
        this.dateDue = dateDue;
        this.priority = priority;
        this.userId = userId;


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Project(long id, String title, String description, LocalDateTime dateDue, String priority) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.dateCreated = LocalDateTime.now();
        this.dateDue = dateDue;
        this.priority = priority;
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
    public boolean isProjectExpired(LocalDateTime dateDue) {
        LocalDateTime current = LocalDateTime.now();


        // send push notification based on time remaining
        // Remind 1 week (168 hours) (10080 minutes) before due date
        // Remind 1 day (24 hours) (1440 minutes) before due date
        // Remind 12 hours (720 minutes) before due date
        // Remind 1 hour (60 minutes) before due date
        // Remind 20 minutes before due date

        long minutes = ChronoUnit.MINUTES.between(current, dateDue);
        long hours = ChronoUnit.HOURS.between(current, dateDue);
        long days = ChronoUnit.DAYS.between(current, dateDue);

        long seconds = ChronoUnit.SECONDS.between(current, dateDue);

        System.out.println("minutes till project is due: " + minutes);
//        System.out.println("hours: " + hours);
//        System.out.println("days: " + days);
//        System.out.println("seconds " + seconds);
        return current.isAfter(getDateDue());
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

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getChecklist() {
        return checklist;
    }

    public void setChecklist(String checkList) {
        this.checklist = checklist;
    }

    @Override
    public String toString() {
        return id + ", " + title + ", " + description + ", " + dateCreated + ", " + dateDue + ", " + priority + ", " + checklist + "\n";
    }
}
