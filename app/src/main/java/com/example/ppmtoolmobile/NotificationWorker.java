package com.example.ppmtoolmobile;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class NotificationWorker extends Worker {

    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    @Override
    public Result doWork() {

        showNotification();

        return Result.success();
    }

    private void showNotification() {

        NotificationCompat.Builder builder;
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(getApplicationContext(), ProjectFragment.class);
        PendingIntent targetActivity = PendingIntent.getActivity(getApplicationContext(),
                1, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            String chanelId = "chanelId";
            String chanelName = "chanelName";
            String chanelDescription = "chanelDescription";
            int chanelPriority = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel chanel = notificationManager.getNotificationChannel(chanelId);

            if (chanel == null) {
                chanel = new NotificationChannel(chanelId, chanelName, chanelPriority);
                chanel.setDescription(chanelDescription);
                notificationManager.createNotificationChannel(chanel);
            }

            builder = new NotificationCompat.Builder(getApplicationContext(), chanelId);

            builder.setContentTitle("Title")
                    .setContentText("text")
                    .setSmallIcon(android.R.drawable.ic_dialog_alert)
                    .setAutoCancel(true);


        } else {
            //Depreceted
            builder = new NotificationCompat.Builder(getApplicationContext());

            builder.setContentTitle("Title")
                    .setContentText("text")
                    .setSmallIcon(android.R.drawable.ic_dialog_alert)
                    .setAutoCancel(true)
                    .setPriority(Notification.PRIORITY_HIGH);

        }

        notificationManager.notify(1, builder.build());
    }

}

