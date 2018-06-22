package com.idx.launcher.videocall.friend.data;


import com.idx.launcher.videocall.utils.LauncherExecutors;

import java.util.List;

/**
 * 数据库操作类实现
 * Created by danny on 3/30/18.
 */

public class LocalFriendDataSource implements FriendDataSource {
    private static volatile LocalFriendDataSource sInstance;
    private LauncherExecutors mLauncherExecutors;
    private UserDao mUserDao;
    private HxUser mUser;
    private List<HxUser> mUsers;
    private Friend mFriend;
    private List<Friend> mFriends;
    private List<String> mAccounts;

    private LocalFriendDataSource(LauncherExecutors launcherExecutors, UserDao userDao){
        mLauncherExecutors = launcherExecutors;
        mUserDao=userDao;
    }

    public static LocalFriendDataSource getInstance(LauncherExecutors loginExecutors, UserDao userDao){
        if (sInstance==null){
            synchronized (LocalFriendDataSource.class){
                if (sInstance==null){
                    sInstance=new LocalFriendDataSource(loginExecutors,userDao);
                }
            }
        }
        return sInstance;
    }

    @Override
    public void insertUser(final HxUser user) {
        mLauncherExecutors.getIoExecutor().execute(new Runnable() {
            @Override
            public void run() {
                mUserDao.insertUser(user);
            }
        });
    }

    @Override
    public void queryUser(final String account, final LoadUserCallback callback) {
        mLauncherExecutors.getIoExecutor().execute(new Runnable() {
            @Override
            public void run() {
                mUser=mUserDao.queryUser(account);
                mLauncherExecutors.getMainExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (mUser!=null) {
                            if (callback != null) {
                                callback.onSuccess(mUser);
                            }
                        }else {
                            if (callback!=null){
                                callback.onError();
                            }
                        }
                    }
                });
            }
        });
    }

    @Override
    public void queryAllUser(final LoadAllUserCallback callback) {
        mLauncherExecutors.getIoExecutor().execute(new Runnable() {
            @Override
            public void run() {
                mUsers=mUserDao.queryAllUser();
                mLauncherExecutors.getMainExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (!mUsers.isEmpty()){
                            if (callback!=null){
                                callback.onSuccess(mUsers);
                            }
                        }else {
                            if (callback!=null){
                                callback.onError();
                            }
                        }
                    }
                });
            }
        });
    }

    @Override
    public void deleteUser() {
        mLauncherExecutors.getIoExecutor().execute(new Runnable() {
            @Override
            public void run() {
                mUserDao.deleteUser();
            }
        });
    }

    @Override
    public void deleteUser(final String account) {
        mLauncherExecutors.getIoExecutor().execute(new Runnable() {
            @Override
            public void run() {
                mUserDao.deleteUser(account);
            }
        });
    }

    @Override
    public void insertFriend(final Friend friend, final AddFriendSuccessCallback callback) {
        mLauncherExecutors.getIoExecutor().execute(new Runnable() {
            @Override
            public void run() {
                mUserDao.insertFriend(friend);
                mLauncherExecutors.getMainExecutor().execute(new Runnable() {
                    @Override
                    public void run() {if (callback!=null){callback.onSuccess();}}
                });
            }
        });
    }

    @Override
    public void queryFriend(final String friendAccount, final LoadFriendCallback callback) {
        mLauncherExecutors.getIoExecutor().execute(new Runnable() {
            @Override
            public void run() {
                mFriend=mUserDao.queryFriend(friendAccount);
                mLauncherExecutors.getMainExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (mFriend!=null){
                            if (callback!=null){
                                callback.onSuccess(mFriend);
                            }
                        }else {
                            if (callback!=null){
                                callback.onError();
                            }
                        }
                    }
                });
            }
        });
    }

    @Override
    public void queryAliasFriend(final String alias, final LoadFriendCallback callback) {
        mLauncherExecutors.getIoExecutor().execute(new Runnable() {
            @Override
            public void run() {
                mFriend=mUserDao.queryAliasFriend(alias);
                mLauncherExecutors.getMainExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (mFriend!=null){
                            if (callback!=null){
                                callback.onSuccess(mFriend);
                            }
                        }else {
                            if (callback!=null){
                                callback.onError();
                            }
                        }
                    }
                });
            }
        });
    }

    @Override
    public void queryMoreAliasFriend(final String alias, final LoadAllFriendCallback callback) {
        mLauncherExecutors.getIoExecutor().execute(new Runnable() {
            @Override
            public void run() {
                mFriends=mUserDao.queryMoreAliasFriend(alias);
                mLauncherExecutors.getMainExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (mFriends!=null){
                            if (callback!=null){
                                callback.onSuccess(mFriends);
                            }
                        }else {
                            if (callback!=null){
                                callback.onError();
                            }
                        }
                    }
                });
            }
        });
    }

    @Override
    public void queryAllFriend(final String userId, final LoadAllFriendCallback callback) {
        mLauncherExecutors.getIoExecutor().execute(new Runnable() {
            @Override
            public void run() {
                mFriends=mUserDao.queryAllFriend(userId);
                mLauncherExecutors.getMainExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (!mFriends.isEmpty()){
                            if (callback!=null){
                                callback.onSuccess(mFriends);
                            }
                        }else {
                            if (callback!=null){
                                callback.onError();
                            }
                        }
                    }
                });
            }
        });
    }

    @Override
    public void queryAllFriendAccount(final String userId, final LoadAllFriendAccountCallback callback) {
        mLauncherExecutors.getIoExecutor().execute(new Runnable() {
            @Override
            public void run() {
                mAccounts=mUserDao.queryAllFriendAccount(userId);
                mLauncherExecutors.getMainExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (!mAccounts.isEmpty()){
                            if (callback!=null){
                                callback.onSuccess(mAccounts);
                            }
                        }else {
                            if (callback!=null){
                                callback.onError();
                            }
                        }
                    }
                });
            }
        });
    }

    @Override
    public void deleteFriend() {
        mLauncherExecutors.getIoExecutor().execute(new Runnable() {
            @Override
            public void run() {
                mUserDao.deleteFriend();
            }
        });
    }

    @Override
    public void deleteFriend(final String friendAccount, final DeleteFriendSuccessCallback callback) {
        mLauncherExecutors.getIoExecutor().execute(new Runnable() {
            @Override
            public void run() {
                mUserDao.deleteFriend(friendAccount);
                mLauncherExecutors.getMainExecutor().execute(new Runnable() {
                    @Override
                    public void run() {if (callback!=null){callback.onSuccess();}}
                });
            }
        });
    }

    @Override
    public void deleteAliasFriend(final String alias, final DeleteFriendAliasSuccessCallback callback) {
        mLauncherExecutors.getIoExecutor().execute(new Runnable() {
            @Override
            public void run() {
                mUserDao.deleteAliasFriend(alias);
                mLauncherExecutors.getMainExecutor().execute(new Runnable() {
                    @Override
                    public void run() {if (callback!=null){callback.onSuccess();}}
                });
            }
        });
    }
}
