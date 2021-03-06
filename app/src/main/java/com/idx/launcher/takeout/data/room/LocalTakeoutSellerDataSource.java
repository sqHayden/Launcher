package com.idx.launcher.takeout.data.room;

import com.idx.launcher.videocall.utils.LauncherExecutors;

import java.util.List;

/**
 * Created by danny on 4/21/18.
 */

public class LocalTakeoutSellerDataSource implements TakeoutDataSource {
    private static volatile LocalTakeoutSellerDataSource sInstance = null;
    private LauncherExecutors mLauncherExecutors;
    private TakeoutSellerDao mSellerDao;
    private TakeoutSeller mTakeoutSeller;
    private List<TakeoutSeller> mSellers;

    private LocalTakeoutSellerDataSource(LauncherExecutors launcherExecutors, TakeoutSellerDao takeoutSellerDao) {
        this.mLauncherExecutors = launcherExecutors;
        this.mSellerDao = takeoutSellerDao;
    }


    public static LocalTakeoutSellerDataSource getInstance(LauncherExecutors launcherExecutors, TakeoutSellerDao takeoutSellerDao) {
        if (sInstance == null) {
            synchronized (LocalTakeoutSellerDataSource.class) {
                if (sInstance == null) {
                    sInstance = new LocalTakeoutSellerDataSource(launcherExecutors, takeoutSellerDao);
                }
            }
        }
        return sInstance;
    }

    @Override
    public void insertTakeoutSeller(final TakeoutSeller seller, final SaveOverCallback callback) {
        mLauncherExecutors.getIoExecutor().execute(new Runnable() {
            @Override
            public void run() {
                mSellerDao.insertTakeoutSeller(seller);
                mLauncherExecutors.getMainExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (callback!=null){callback.onSuccess();}
                    }
                });
            }
        });
    }

    @Override
    public void queryAllFood(final String sellerName, final LoadAllSellerCallback callback) {
        mLauncherExecutors.getIoExecutor().execute(new Runnable() {
            @Override
            public void run() {
                mSellers = mSellerDao.queryAllFood(sellerName);
                mLauncherExecutors.getMainExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (!mSellers.isEmpty()) {
                            if (callback != null) {callback.onSuccess(mSellers);}
                        } else {
                            if (callback != null) {callback.onError();}
                        }
                    }
                });
            }
        });
    }

    @Override
    public void findFood(final String foodName, final LoadSellerCallback callback) {
        mLauncherExecutors.getIoExecutor().execute(new Runnable() {
            @Override
            public void run() {
                mTakeoutSeller=mSellerDao.findFood(foodName);
                mLauncherExecutors.getMainExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (mTakeoutSeller!=null){
                            if (callback!=null){callback.onSuccess(mTakeoutSeller);}
                        }else {
                            if (callback!=null){callback.onError();}
                        }
                    }
                });
            }
        });
    }

    @Override
    public void updateFoodCount(final int i, final String foodName, final SaveOverCallback callback) {
        mLauncherExecutors.getIoExecutor().execute(new Runnable() {
            @Override
            public void run() {
                mSellerDao.updateFoodCount(i,foodName);
                mLauncherExecutors.getMainExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (callback!=null){callback.onSuccess();}
                    }
                });
            }
        });
    }

    @Override
    public void modifyFoodCount(final int i, final String foodName, final SaveOverCallback callback) {
        mLauncherExecutors.getIoExecutor().execute(new Runnable() {
            @Override
            public void run() {
                mSellerDao.modifyFoodCount(i,foodName);
                mLauncherExecutors.getMainExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (callback!=null){callback.onSuccess();}
                    }
                });
            }
        });
    }

    @Override
    public void deleteTakeoutSeller(final String sellerName) {
        mLauncherExecutors.getIoExecutor().execute(new Runnable() {
            @Override
            public void run() {mSellerDao.deleteTakeoutSeller(sellerName);}
        });
    }

    @Override
    public void deleteFood(final String foodName, final SaveOverCallback callback) {
        mLauncherExecutors.getIoExecutor().execute(new Runnable() {
            @Override
            public void run() {
                mSellerDao.deleteFood(foodName);
                mLauncherExecutors.getMainExecutor().execute(new Runnable() {
                    @Override
                    public void run() {if (callback!=null){callback.onSuccess();}}
                });
            }
        });
    }

    @Override
    public void deleteTakeoutSeller() {
        mLauncherExecutors.getIoExecutor().execute(new Runnable() {
            @Override
            public void run() {mSellerDao.deleteTakeoutSeller();}
        });
    }
}
