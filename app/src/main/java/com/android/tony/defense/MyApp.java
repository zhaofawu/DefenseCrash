package com.android.tony.defense;

import android.util.Log;

import com.android.tony.defenselib.DefenseApplication;
import com.android.tony.defenselib.handler.IExceptionHandler;

public class MyApp extends DefenseApplication implements IExceptionHandler {
    @Override
    public void onCreate() {
        super.onCreate();

        throw new NullPointerException("Application测试崩溃生命周期");

    }

    @Override
    public void onUncaughtExceptionHappened(Thread thread, Throwable throwable, boolean isSafeMode) {
        throwable.printStackTrace();
    }

}
