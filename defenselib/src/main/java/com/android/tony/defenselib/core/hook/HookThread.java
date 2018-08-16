package com.android.tony.defenselib.core.hook;

import android.os.Looper;

import com.android.tony.defenselib.core.DefenseCore;
import com.android.tony.defenselib.handler.ExceptionDispatcher;

public class HookThread implements IHook {

    private boolean isHooked;

    private ExceptionDispatcher mExceptionDispatcher;

    private Thread.UncaughtExceptionHandler mOriginHandler;

    public HookThread(ExceptionDispatcher exceptionDispatcher) {
        mExceptionDispatcher = exceptionDispatcher;
    }

    @Override
    public void hook() {
        if (isHooked()) {
            return;
        }
        mOriginHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                if (mExceptionDispatcher != null) {
                    mExceptionDispatcher.uncaughtExceptionHappened(t, e, DefenseCore.isInSafeMode());
                }
                if (t == Looper.getMainLooper().getThread()) {
                    DefenseCore.maybeChoreographerException(e, mExceptionDispatcher);
                    DefenseCore.enterSafeModeKeepLoop(mExceptionDispatcher);
                }
            }
        });
        isHooked = true;
    }

    @Override
    public void unHook() {
        if (!isHooked()) {
            return;
        }
        Thread.setDefaultUncaughtExceptionHandler(mOriginHandler);
        isHooked = false;
    }

    @Override
    public boolean isHooked() {
        return isHooked;
    }
}
