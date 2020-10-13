package com.stackbuffers.doctor_patient_project.HelperClasses;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.stackbuffers.doctor_patient_project.R;
import com.stackbuffers.doctor_patient_project.SplashScreenActivity;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
 //   private static final String TAG = "ABc";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
    //    Log.i("token1", remoteMessage.getData().get("message"));
        String msg = remoteMessage.getData().get("message");
        String title = remoteMessage.getData().get("title");
        showNotification(msg, title);


    }

    private void showNotification(String title, String msg) {

        Intent i = new Intent(this, SplashScreenActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel channel = new NotificationChannel("default", "Mostawsaf", NotificationManager.IMPORTANCE_HIGH);
            Notification notification = new Notification.Builder(this, "default")
                    .setContentTitle(title)
                    .setContentText(msg)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setAutoCancel(true)
                    .setTicker(title)
                    .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                    .build();
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
                notificationManager.notify(0, notification);
            }
        } else {
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext());
            mBuilder.setContentTitle(title)
                    .setContentText(msg)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                    .setTicker(title);
            mBuilder.build();
            if (notificationManager != null) {
                notificationManager.notify(0, mBuilder.build());
            }
        }

    }
}
