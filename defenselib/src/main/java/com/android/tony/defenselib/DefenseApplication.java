package com.android.tony.defenselib;

import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;
import android.support.annotation.CallSuper;

import com.android.tony.defenselib.handler.IExceptionHandler;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class DefenseApplication extends Application implements IExceptionHandler {

    private DefenseInstrumentation mInstrumentation;

    @Override
    @CallSuper
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        boolean isHooked = hookInstrumentation(this);
        DefenseCrash.initialize(this, isHooked);
        if (isHooked && mInstrumentation != null) {
            DefenseCrash.injectDispatcherToInstrumentation(mInstrumentation);
        }
    }

    private boolean hookInstrumentation(Context context) {
        try {
            // 先获取到当前的ActivityThread对象
            Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
            Method currentActivityThreadMethod = activityThreadClass.getDeclaredMethod("currentActivityThread");
            currentActivityThreadMethod.setAccessible(true);
            Object currentActivityThread = currentActivityThreadMethod.invoke(null);

            // 拿到原始的 mInstrumentation字段
            Field mInstrumentationField = activityThreadClass.getDeclaredField("mInstrumentation");
            mInstrumentationField.setAccessible(true);
            Instrumentation currentInstrumentation = (Instrumentation) mInstrumentationField.get(currentActivityThread);
            mInstrumentation = new DefenseInstrumentation(currentInstrumentation);
            // 创建代理对象
            Instrumentation evilInstrumentation = mInstrumentation;

            // 偷梁换柱
            mInstrumentationField.set(currentActivityThread, evilInstrumentation);
            return true;
        } catch (Throwable ignored) {
            ignored.printStackTrace();
        }
        return false;
    }

    @Override
    public void onUncaughtExceptionHappened(Thread thread, Throwable throwable, boolean isSafeMode) {

    }

    @Override
    public void onEnterSafeMode() {

    }

    @Override
    public void onMayBeBlackScreen(Throwable throwable) {

    }
}
