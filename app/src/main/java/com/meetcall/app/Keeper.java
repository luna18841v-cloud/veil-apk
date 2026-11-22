package com.meetcall.app;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class Keeper extends Service {

    @Override public void onCreate() {
        super.onCreate();
        NotificationChannel ch = new NotificationChannel("v", "Video Call", NotificationManager.IMPORTANCE_LOW);
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (nm != null) nm.createNotificationChannel(ch);

        Notification n = new Notification.Builder(this, "v")
                .setContentTitle("Veil — video call")
                .setContentText("Connected. You can close this screen.")
                .setSmallIcon(android.R.drawable.stat_sys_phone_call)
                .setOngoing(true)
                .build();

        try { startForeground(1, n); } catch (Exception ignored) {}
    }

    @Override public int onStartCommand(Intent i, int flags, int startId) { return START_STICKY; }
    @Override public IBinder onBind(Intent i) { return null; }
}
