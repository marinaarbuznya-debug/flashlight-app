package com.flashlight.remote;

import android.app.*;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.camera2.*;
import android.os.IBinder;
import android.os.PowerManager;
import androidx.core.app.NotificationCompat;

public class FlashlightService extends Service {
    private CameraManager cameraManager;
    private String cameraId;
    private static final String CHANNEL_ID = "flashlight_channel";
    private PowerManager.WakeLock wakeLock;
    private BroadcastReceiver screenReceiver;

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

        // WakeLock щоб не засинав
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "FlashlightService::WakeLock");
        wakeLock.acquire();

        // Динамічна реєстрація receiver для екрану
        screenReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Перезапускаємо сервіс при розблокуванні
                startForeground(1, buildNotification());
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_USER_PRESENT);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(screenReceiver, filter);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (wakeLock != null && wakeLock.isHeld()) wakeLock.release();
            if (screenReceiver != null) unregisterReceiver(screenReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Перезапускаємо сервіс якщо його вбили
        Intent restart = new Intent(this, FlashlightService.class);
        startForegroundService(restart);
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
