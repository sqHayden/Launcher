package com.idx.launcher.videocall.friend.data;

import android.content.Context;

import com.idx.launcher.videocall.utils.LauncherExecutors;

/**
 * 通过该类拿到好友表操作对象-FriendRepository
 * Created by danny on 3/31/18.
 */

public class FriendInjection {
    public static FriendRepository getInstance(Context context){
        LauncherDatabase database= LauncherDatabase.getInstance(context);
        FriendRepository repository= FriendRepository.getInstance(LocalFriendDataSource.getInstance(new LauncherExecutors(),database.uerDao()));
        return repository;
    }
}
