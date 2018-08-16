package com.android.tony.defenselib.core.hook;

import android.app.Instrumentation;

import com.android.tony.defenselib.core.DefenseInstrumentation;
import com.android.tony.defenselib.handler.ExceptionDispatcher;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class HookInstrumentation implements IHook {

    private boolean isHooked;

    private ExceptionDispatcher mExceptionDispatcher;

    private DefenseInstrumentation mCustomInstrumentation;

    private Instrumentation mOriginInstrumentation;

    public HookInstrumentation(ExceptionDispatcher exceptionDispatcher) {
        mExceptionDispatcher = exceptionDispatcher;
    }

    @Override
    public void hook() {
        if (isHooked()) {
            return;
        }
        try {
            // 先获取到当前的ActivityThread对象
            Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
            Method currentActivityThreadMethod = activityThreadClass.getDeclaredMethod("currentActivityThread");
            currentActivityThreadMethod.setAccessible(true);
            Object currentActivityThread = currentActivityThreadMethod.invoke(null);

            // 拿到原始的 mInstrumentation字段
            Field mInstrumentationField = activityThreadClass.getDeclaredField("mInstrumentation");
            mInstrumentationField.setAccessible(true);
            mOriginInstrumentation = (Instrumentation) mInstrumentationField.get(currentActivityThread);
            mCustomInstrumentation = new DefenseInstrumentation(mOriginInstrumentation);
            mCustomInstrumentation.setExceptionDispatcher(mExceptionDispatcher);
            // 创建代理对象
            Instrumentation evilInstrumentation = mCustomInstrumentation;

            // 偷梁换柱
            mInstrumentationField.set(currentActivityThread, evilInstrumentation);
            isHooked = true;
        } catch (Throwable ignored) {
            ignored.printStackTrace();
            isHooked = false;
        }
    }

    @Override
    public void unHook() {
        if (!isHooked()) {
            return;
        }
        try {
            // 先获取到当前的ActivityThread对象
            Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
            Method currentActivityThreadMethod = activityThreadClass.getDeclaredMethod("currentActivityThread");
            currentActivityThreadMethod.setAccessible(true);
            Object currentActivityThread = currentActivityThreadMethod.invoke(null);

            // 拿到原始的 mInstrumentation字段
            Field mInstrumentationField = activityThreadClass.getDeclaredField("mInstrumentation");
            mInstrumentationField.setAccessible(true);

            // 偷梁换柱
            mInstrumentationField.set(currentActivityThread, mOriginInstrumentation);
            isHooked = false;
        } catch (Throwable ignored) {
            ignored.printStackTrace();
            isHooked = true;
        }
    }

    @Override
    public boolean isHooked() {
        return isHooked;
    }

}
