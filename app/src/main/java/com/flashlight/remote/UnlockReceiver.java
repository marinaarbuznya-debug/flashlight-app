package com.flashlight.remote;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class UnlockReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("UnlockReceiver", "Screen unlocked, starting service");
        try {
            Intent service = new Intent(context, FlashlightService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(service);
            } else {
                context.startService(service);
            }
        } catch (Exception e) {
            Log.e("UnlockReceiver", "Error: " + e.getMessage());
        }
    }
}
