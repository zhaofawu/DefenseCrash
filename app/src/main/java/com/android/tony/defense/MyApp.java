package com.android.tony.defense;

import android.app.Application;
import android.content.Context;

import com.android.tony.defenselib.DefenseCrash;
import com.android.tony.defenselib.handler.IExceptionHandler;

public class MyApp extends Application implements IExceptionHandler {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        DefenseCrash.initialize();
        DefenseCrash.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        throw new NullPointerException("Application测试崩溃生命周期");
    }

    @Override
    public void onCaughtException(Thread thread, Throwable throwable, boolean isSafeMode) {
        throwable.printStackTrace();
    }

    @Override
    public void onEnterSafeMode() {

    }

    @Override
    public void onMayBeBlackScreen(Throwable throwable) {

    }

}
