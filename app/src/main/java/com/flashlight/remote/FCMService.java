package com.flashlight.remote;

import android.hardware.camera2.CameraManager;
import android.util.Log;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FCMService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        FirebaseDatabase.getInstance()
            .getReference("token")
            .setValue(token);
    }

    @Override
    public void onMessageReceived(RemoteMessage message) {
        String command = message.getData().get("command");
        if (command == null) return;

        try {
            CameraManager cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
            String cameraId = cameraManager.getCameraIdList()[0];
            if ("on".equals(command)) {
                cameraManager.setTorchMode(cameraId, true);
            } else if ("off".equals(command)) {
                cameraManager.setTorchMode(cameraId, false);
            }
        } catch (Exception e) {
            Log.e("FCMService", "Flashlight error: " + e.getMessage());
        }
    }
}
