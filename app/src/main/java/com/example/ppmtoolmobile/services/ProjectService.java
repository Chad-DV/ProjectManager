package com.example.ppmtoolmobile.services;



import static com.example.ppmtoolmobile.utils.NotificationUtils.CHANNEL_ID_1;
import static com.example.ppmtoolmobile.utils.NotificationUtils.CHANNEL_ID_2;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import android.app.Notification;

import com.example.ppmtoolmobile.AppNotificationChannel;
import com.example.ppmtoolmobile.LoginActivity;
import com.example.ppmtoolmobile.R;
import com.example.ppmtoolmobile.dao.ProjectDAOImpl;
import com.example.ppmtoolmobile.model.Project;
import com.example.ppmtoolmobile.utils.ArrayConversionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;


public class ProjectService extends Service  {

    private AppNotificationChannel appNotification;
    private ProjectDAOImpl projectHelper;
    private List<Project> projectList;
    private Logger logger = Logger.getLogger(ProjectService.class.getName());
    private long[] intervalArr = {20160, 10080, 1440, 2, 1};
    private long userId;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();

        projectHelper = new ProjectDAOImpl(this);
        appNotification = new AppNotificationChannel();


        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                projectList = projectHelper.getUserProjects(userId);
                for(Project p : projectList) {

                    String[] remindMeIntervals = ArrayConversionUtils.convertStringToArray(p.getRemindMeInterval());

                    System.out.println(Arrays.toString(remindMeIntervals));

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
                            sendNotification("Have you completed your project?", "Project " + p.getTitle() + " is due in 2 minute(s)");
                        }
                    }

                    if(!remindMeIntervals[4].equals("null")) {
                        if(p.getProjectRemainingTimeInMinutes() == intervalArr[4]) {
                            sendNotification("Have you completed your project?", "Project " + p.getTitle() + " is due in 1 minute(s)");
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
        Notification.Action action =
                new Notification.Action.Builder(R.drawable.ic_sort_icon, "Open", pendingIntent)
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


        int NOTIFICATION_ID = 1001;
        manager.notify(NOTIFICATION_ID, notification);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void sendForegroundNotification(String title, String text) {


        Intent notificationIntent = new Intent(this, LoginActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);


        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID_2)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.drawable.ic_logo)
                .setContentIntent(pendingIntent)
                .build();


        startForeground(1, notification);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        userId = intent.getLongExtra("userId", -999);



        System.out.println(userId);
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
