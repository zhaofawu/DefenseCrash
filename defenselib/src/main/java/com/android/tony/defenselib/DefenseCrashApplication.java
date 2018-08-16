package com.android.tony.defenselib;

import android.app.Application;
import android.content.Context;
import android.support.annotation.CallSuper;

import com.android.tony.defenselib.handler.IExceptionHandler;

public class DefenseCrashApplication extends Application implements IExceptionHandler {

    @Override
    @CallSuper
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        DefenseCrash.initialize();
        DefenseCrash.install(this);
    }

    @CallSuper
    public void unInstallDefense() {
        DefenseCrash.unInstall();
    }

    /**
     * 主线程抛出异常时调用该方法，
     * 子线程抛出异常时调用该方法。
     * 该方法中到的throwable 应该进行打印或者上报到Bug收集平台
     *
     * @param thread
     * @param throwable
     * @param isSafeMode
     */
    @Override
    public void onCaughtException(Thread thread, Throwable throwable, boolean isSafeMode) {

    }

    /**
     *
     */
    @Override
    public void onEnterSafeMode() {

    }

    @Override
    public void onMayBeBlackScreen(Throwable throwable) {

    }
}
