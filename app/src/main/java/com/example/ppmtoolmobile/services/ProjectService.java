package com.example.ppmtoolmobile.services;

import static com.example.ppmtoolmobile.AppNotification.CHANNEL_ID;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import android.app.Notification;

import com.example.ppmtoolmobile.LoginActivity;
import com.example.ppmtoolmobile.ProjectActivity;
import com.example.ppmtoolmobile.R;
import com.example.ppmtoolmobile.dao.ProjectDAOImpl;
import com.example.ppmtoolmobile.model.Project;
import com.example.ppmtoolmobile.utils.ArrayConversionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ProjectService extends Service {

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



        new Timer().scheduleAtFixedRate(new TimerTask() {
            @RequiresApi(api = Build.VERSION_CODES.O)
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
                            logger.log(Level.INFO, "Project " + p.getTitle() + " is due in 2 mins");
                        }
                    }

                    if(!remindMeIntervals[4].equals("null")) {
                        if(p.getProjectRemainingTimeInMinutes() == intervalArr[4]) {
                            sendNotification("Have you completed your project?", "Project " + p.getTitle() + " is due in 1 minute(s)");
                            logger.log(Level.INFO, "Project " + p.getTitle() + " is due in 1 min");
                        }
                    }
                }
            }
        }, 10, 60000);



//        System.out.println("projectSerivce: " + projectList);
    }

    private void sendNotification(String title, String text) {
        Intent notificationIntent = new Intent(this, LoginActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);


        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.drawable.ic_caution_icon)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        userId = intent.getLongExtra("userId", -999);



        System.out.println(userId);
        sendNotification("Projecto", "Service is running...");

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
