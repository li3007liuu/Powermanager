package com.splxtech.powermanagor.engine;

import android.app.Application;

import com.splxtech.splxapplib.cache.CacheManager;

/**
 * Created by li300 on 2016/9/14 0014.
 */
public class PowerManagorApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CacheManager.getInstance().initCacheDir();
    }
}
