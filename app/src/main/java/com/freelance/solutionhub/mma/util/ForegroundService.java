package com.freelance.solutionhub.mma.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.work.Data;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.freelance.solutionhub.mma.R;
import com.freelance.solutionhub.mma.activity.MainActivity;

import java.util.concurrent.TimeUnit;

public class ForegroundService extends Service {
    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    @Override
    public void onCreate() {
        super.onCreate();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String input = intent.getStringExtra("inputExtra");
        createNotificationChannel();
        // Intent notificationIntent = new Intent(this, MainActivity.class);
        // PendingIntent pendingIntent = PendingIntent.getActivity(this,
        //       0, notificationIntent, 0);
        NotificationCompat.Builder notification = new NotificationCompat.Builder(this, CHANNEL_ID);
        notification
                .setContentTitle("Background")
                .setContentText(input)
                .setSmallIcon(R.drawable.mma_notification)
                .build();
        startForeground(1, notification.getNotification());//do heavy work on a background thread
        //stopSelf();
        return START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Intent serviceIntent = new Intent(this, ForegroundService.class);
        serviceIntent.putExtra("inputExtra", "MMA is running in background.");
        ContextCompat.startForegroundService(this, serviceIntent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent serviceIntent = new Intent(this, ForegroundService.class);
        serviceIntent.putExtra("inputExtra", "MMA is running in background.");
        ContextCompat.startForegroundService(this, serviceIntent);
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
}