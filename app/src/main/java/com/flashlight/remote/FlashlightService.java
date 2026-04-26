package com.flashlight.remote;

import android.app.*;
import android.content.Intent;
import android.hardware.camera2.*;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;

public class FlashlightService extends Service {
    private CameraManager cameraManager;
    private String cameraId;
    private static final String CHANNEL_ID = "flashlight_channel";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        startForeground(1, buildNotification());
        try {
            cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
            cameraId = cameraManager.getCameraIdList()[0];
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String command = intent.getStringExtra("command");
            if (command != null) {
                try {
                    if (cameraManager == null) {
                        cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
                        cameraId = cameraManager.getCameraIdList()[0];
                    }
                    if ("on".equals(command)) {
                        cameraManager.setTorchMode(cameraId, true);
                    } else if ("off".equals(command)) {
                        cameraManager.setTorchMode(cameraId, false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return START_STICKY;
    }

    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(
            CHANNEL_ID, "Service", NotificationManager.IMPORTANCE_MIN);
        getSystemService(NotificationManager.class).createNotificationChannel(channel);
    }

    private Notification buildNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("System Service")
            .setSmallIcon(android.R.drawable.ic_menu_manage)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .build();
    }

    @Override
    public IBinder onBind(Intent intent) { return null; }
}
