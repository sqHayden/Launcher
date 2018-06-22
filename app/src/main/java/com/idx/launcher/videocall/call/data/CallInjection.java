package com.idx.launcher.videocall.call.data;

import android.content.Context;

import com.idx.launcher.videocall.friend.data.LauncherDatabase;
import com.idx.launcher.videocall.utils.LauncherExecutors;

/**
 * 通过该类拿到未接电话表操作对象-CallRepository
 * Created by danny on 3/31/18.
 */

public class CallInjection {
    public static CallRepository getInstance(Context context){
        LauncherDatabase database= LauncherDatabase.getInstance(context);
        CallRepository repository= CallRepository.getInstance(LocalCallDataSource.getInstance(new LauncherExecutors(),database.missedCallDao()));
        return repository;
    }
}
