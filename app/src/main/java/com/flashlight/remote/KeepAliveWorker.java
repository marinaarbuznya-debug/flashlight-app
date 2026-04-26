package com.flashlight.remote;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class KeepAliveWorker extends Worker {
    public KeepAliveWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @Override
    public Result doWork() {
        Context context = getApplicationContext();
        Intent intent = new Intent(context, FlashlightService.class);
        context.startForegroundService(intent);
        return Result.success();
    }
}
