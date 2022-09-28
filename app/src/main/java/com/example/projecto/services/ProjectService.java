package com.example.projecto.services;



import static com.example.projecto.utils.NotificationUtils.CHANNEL_ID_1;
import static com.example.projecto.utils.NotificationUtils.CHANNEL_ID_2;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import android.app.Notification;

import com.example.projecto.LoginActivity;

import com.example.projecto.R;
import com.example.projecto.dao.ProjectDAOImpl;
import com.example.projecto.model.Project;
import com.example.projecto.utils.ArrayConversionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class ProjectService extends Service  {
    private ProjectDAOImpl projectHelper;
    private List<Project> projectList;
    // 2 weeks, 1 week, 1 day, 1 hour, 30 min
    private final long[] intervalArr = {20160, 10080, 1440, 60, 30};
    private long userId;
    private String[] remindMeIntervals;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();

        projectHelper = new ProjectDAOImpl(this);

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                projectList = projectHelper.getUserProjects(userId);
                for(Project p : projectList) {

                     remindMeIntervals = ArrayConversionUtils.convertStringToArray(p.getRemindMeInterval());

                    if(!remindMeIntervals[0].equals("null")) {
                        if(p.getProjectRemainingTimeInMinutes() == intervalArr[0]) {
                            sendNotification("Have you completed your project?", "Project " + p.getTitle() + " is due in 2 week(s)");
                        }
                    }
                    if(!remindMeIntervals[1].equals("null")) {
                        if(p.getProjectRemainingTimeInMinutes() == intervalArr[1]) {
                            sendNotification("Have you completed your project?", "Project " + p.getTitle() + " is due in 1 week(s)");
                        }
                    }
                    if(!remindMeIntervals[2].equals("null")) {
                        if(p.getProjectRemainingTimeInMinutes() == intervalArr[2]) {
                            sendNotification("Have you completed your project?", "Project " + p.getTitle() + " is due in 1 day(s)");
                        }
                    }

                    if(!remindMeIntervals[3].equals("null")) {
                        if(p.getProjectRemainingTimeInMinutes() == intervalArr[3]) {
                            sendNotification("Have you completed your project?", "Project " + p.getTitle() + " is due in 1 hour(s)");
                        }
                    }

                    if(!remindMeIntervals[4].equals("null")) {
                        if(p.getProjectRemainingTimeInMinutes() == intervalArr[4]) {
                            sendNotification("Have you completed your project?", "Project " + p.getTitle() + " is due in 30 minute(s)");
                        }
                    }

                    if(p.getProjectRemainingTimeInMinutes() == 0) {
                        sendNotification("Project expired", "Your project " + p.getTitle() + " has expired.");
                    }


                }
            }
        }, 10, 60000);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void sendNotification(String title, String description) {
        Intent resultIntent = new Intent(this, LoginActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Action action = new Notification.Action.Builder(R.drawable.ic_sort_icon, "Open", pendingIntent)
            .build();

        Notification notification = new Notification.Builder(this, CHANNEL_ID_1)
                .setContentTitle(title)
                .setContentText(description)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setChannelId(CHANNEL_ID_1)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setActions(action)
                .build();

        NotificationManager manager = getSystemService(NotificationManager.class);

        manager.notify(1001, notification);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void sendForegroundNotification(String title, String text) {
        Intent notificationIntent = new Intent(this, LoginActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);


        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID_2)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.drawable.ic_logo_purple)
                .setColor(getResources().getColor(R.color.primary_purple))
                .setContentIntent(pendingIntent)
                .build();


        startForeground(1, notification);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        userId = intent.getLongExtra("userId", -999);
        sendForegroundNotification("Projecto", "Service is running...");

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
