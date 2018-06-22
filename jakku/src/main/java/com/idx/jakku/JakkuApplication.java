package com.idx.jakku;

import android.app.Application;

/**
 * Created by derik on 18-6-2.
 */

public class JakkuApplication extends Application {

    private static JakkuApplication INSTANCE;
    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
    }

    public static JakkuApplication getInstance(){
        return INSTANCE;
    }

}
