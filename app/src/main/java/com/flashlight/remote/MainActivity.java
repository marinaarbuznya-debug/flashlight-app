package com.flashlight.remote;

import android.app.Activity;
import android.os.Bundle;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String token = task.getResult();
                        com.google.firebase.database.FirebaseDatabase.getInstance()
                            .getReference("token")
                            .setValue(token);
                    }
                });
        } catch (Exception e) {
            e.printStackTrace();
        }

        finish();
    }
}
