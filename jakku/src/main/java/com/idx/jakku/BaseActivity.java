package com.idx.jakku;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.idx.jakku.service.DataListener;

import java.util.List;

public abstract class BaseActivity extends AppCompatActivity implements DataListener {

    private static final String TAG = BaseActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        Window window = getWindow();
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        window.setFlags(flag, flag);
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    public static boolean isServiceRunning(Context context, Class<?> clz) {
        boolean result = false;
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServices = activityManager.getRunningServices(20);
        if (runningServices != null && runningServices.size() > 0) {
            for (int i = 0; i < runningServices.size(); i++) {
                result = runningServices.get(i).service.getClassName().equals(clz.getName());
                if (result) {
                    break;
                }
            }
        }
        Log.d(TAG, "isServiceRunning: " + result);
        return result;
    }

    @Override
    public void onJsonReceived(String json) {
    }

    @Override
    public void onFinish() {
        finish();
    }
}
