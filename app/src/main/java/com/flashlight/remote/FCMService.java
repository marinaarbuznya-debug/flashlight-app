package com.flashlight.remote;

import android.content.Intent;
import android.util.Log;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FCMService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.d("FCM_TOKEN", "Token: " + token);
        FirebaseDatabase.getInstance()
            .getReference("token")
            .setValue(token);
    }

    @Override
    public void onMessageReceived(RemoteMessage message) {
        String command = message.getData().get("command");
        if (command == null) return;
        Intent intent = new Intent(this, FlashlightService.class);
        intent.putExtra("command", command);
        startForegroundService(intent);
    }
}
