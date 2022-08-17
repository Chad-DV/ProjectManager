package com.example.projecto;

import static com.example.projecto.utils.NotificationUtils.CHANNEL_DESC_1;
import static com.example.projecto.utils.NotificationUtils.CHANNEL_DESC_2;
import static com.example.projecto.utils.NotificationUtils.CHANNEL_ID_1;
import static com.example.projecto.utils.NotificationUtils.CHANNEL_ID_2;
import static com.example.projecto.utils.NotificationUtils.CHANNEL_NAME_1;
import static com.example.projecto.utils.NotificationUtils.CHANNEL_NAME_2;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.List;

public class AppNotificationChannel extends Application {


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannels();
        System.out.println("created notification channels..");
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

            manager.createNotificationChannel(notificationChannel);
            manager.createNotificationChannel(notificationServiceChannel);
//            List<NotificationChannel> x = manager.getNotificationChannels();
//            for(NotificationChannel s : x) {
//                System.out.println("Channel: " + s.getId());
//            }

        }



    }







}
