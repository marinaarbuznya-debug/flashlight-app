package com.flashlight.remote;

import android.app.Activity;
import android.os.Bundle;
import androidx.work.*;
import com.google.firebase.messaging.FirebaseMessaging;
import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseMessaging.getInstance().getToken()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String token = task.getResult();
                    com.google.firebase.database.FirebaseDatabase.getInstance()
                        .getReference("token")
                        .setValue(token);
                }
            });

        PeriodicWorkRequest keepAlive = new PeriodicWorkRequest.Builder(
            KeepAliveWorker.class, 15, TimeUnit.MINUTES)
            .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "keepAlive",
            ExistingPeriodicWorkPolicy.REPLACE,
            keepAlive
        );

        finish();
    }
}
