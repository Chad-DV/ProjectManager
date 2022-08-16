package com.example.ppmtoolmobile;

import static com.example.ppmtoolmobile.utils.NotificationUtils.CHANNEL_DESC_1;
import static com.example.ppmtoolmobile.utils.NotificationUtils.CHANNEL_DESC_2;
import static com.example.ppmtoolmobile.utils.NotificationUtils.CHANNEL_ID_1;
import static com.example.ppmtoolmobile.utils.NotificationUtils.CHANNEL_ID_2;
import static com.example.ppmtoolmobile.utils.NotificationUtils.CHANNEL_NAME_1;
import static com.example.ppmtoolmobile.utils.NotificationUtils.CHANNEL_NAME_2;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.ppmtoolmobile.services.ProjectService;

import java.util.List;

public class AppNotificationChannel extends Application {


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannels();
    }

    private void createNotificationChannels() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = getSystemService(NotificationManager.class);

            NotificationChannel notificationChannel = new NotificationChannel(
                    CHANNEL_ID_1,
                    CHANNEL_NAME_1,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationChannel.setDescription(CHANNEL_DESC_1);

            NotificationChannel notificationServiceChannel = new NotificationChannel(
                    CHANNEL_ID_2,
                    CHANNEL_NAME_2,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationServiceChannel.setDescription(CHANNEL_DESC_2);


            List<NotificationChannel> x = manager.getNotificationChannels();
            if(x == null) {
                manager.createNotificationChannel(notificationChannel);
                manager.createNotificationChannel(notificationServiceChannel);
            } else {
                for(NotificationChannel s : x) {
                    System.out.println("Channel: " + s.getId());
                }
            }

        }



    }







}
