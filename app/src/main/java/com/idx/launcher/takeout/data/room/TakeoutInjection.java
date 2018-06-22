package com.idx.launcher.takeout.data.room;

import android.content.Context;

import com.idx.launcher.videocall.friend.data.LauncherDatabase;
import com.idx.launcher.videocall.utils.LauncherExecutors;

/**
 * 通过该类拿到外卖表操作对象-TakeoutRepository
 * Created by danny on 4/21/18.
 */

public class TakeoutInjection {
    public static TakeoutRepository getInstance(Context context){
        LauncherDatabase launcherDatabase = LauncherDatabase.getInstance(context);
        TakeoutRepository repository=TakeoutRepository.getInstance(LocalTakeoutSellerDataSource.getInstance(new LauncherExecutors(), launcherDatabase.takeoutSellerDao()));
        return repository;
    }
}
