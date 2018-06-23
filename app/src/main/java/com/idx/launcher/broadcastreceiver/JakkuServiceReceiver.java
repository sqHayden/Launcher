package com.idx.launcher.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.idx.jakku.MainActivity;
import com.idx.launcher.home.HomeActivity;

/**
 * Created by hayden on 18-6-23.
 */

public class JakkuServiceReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        switch (action){
            case "com.idx.launcher.jakku.close":
                //关闭服务
                Intent speakIntent = new Intent();
                ComponentName componentName = new ComponentName("com.idx.launcher","com.idx.launcher.service.SpeakService");
                speakIntent.setComponent(componentName);
                context.stopService(speakIntent);

                Intent intent2 = new Intent(context, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent2);
                break;
            case "com.idx.launcher.jakku.open":
                Intent intent1 = new Intent(context, HomeActivity.class);
                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent1);
                break;
            default:
                break;
        }
    }
}
