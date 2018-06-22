package com.idx.jakku.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.idx.jakku.service.JakkuService;

/**
 * Created by derik on 18-6-4.
 */

public class GlobalBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = GlobalBroadcastReceiver.class.getSimpleName();
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(TAG, "onReceive: " + action);

        switch (action){
            case Intent.ACTION_BOOT_COMPLETED:
                context.startService(new Intent(context, JakkuService.class));
                break;
            default:
        }

    }
}
